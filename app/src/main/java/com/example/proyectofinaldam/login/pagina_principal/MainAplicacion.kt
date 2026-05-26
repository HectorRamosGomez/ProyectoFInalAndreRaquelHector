package com.example.proyectofinaldam.login.pagina_principal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.proyectofinaldam.R
import java.util.Locale

class MainAplicacion : AppCompatActivity() {

    private lateinit var contenedorGastos: LinearLayout
    private lateinit var contenedorSuscripciones: LinearLayout
    private lateinit var txtMontoTotal: TextView
    private lateinit var txtMediaMensual: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_principal)

        val btnTotal = findViewById<LinearLayout>(R.id.BotonGastos)
        txtMontoTotal = findViewById(R.id.txtMontoTotal)
        txtMediaMensual = findViewById(R.id.txtMediaMensual) // ID directo y único corregido

        val btnIrAGastos = findViewById<Button>(R.id.botongasto)
        val btnIrASuscripciones = findViewById<Button>(R.id.btnsuscripciones)

        contenedorGastos = findViewById(R.id.contenedorGastos)
        contenedorSuscripciones = findViewById(R.id.contenedorSuscripciones)

        // Cargamos el total inicial guardado
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val montoGuardado = sharedPref.getString("monto_total", "00.00€")
        txtMontoTotal.text = montoGuardado

        btnTotal.setOnClickListener {
            mostrarDialogoIngreso(txtMontoTotal)
        }

        btnIrAGastos.setOnClickListener {
            val intent = Intent(this, PaginaGastosActivity::class.java)
            startActivity(intent)
        }

        btnIrASuscripciones.setOnClickListener {
            // Asegúrate de tener creada tu actividad para registrar suscripciones
            val intent = Intent(this, PaginaSuscripcionesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 1. Procesamos si hay restas pendientes
        procesarRestaGasto()
        // 2. Actualizamos ambas vistas independientes
        actualizarListaGastos()
        actualizarListaSuscripciones()
        // 3. Calculamos la media global
        calcularYActualizarMedia()
    }

    private fun procesarRestaGasto() {
        val sharedPrefRestar = getSharedPreferences("RestarGastos", Context.MODE_PRIVATE)
        val gastoPendiente = sharedPrefRestar.getFloat("gasto_pendiente", 0.0f)

        if (gastoPendiente > 0) {
            val textoActual = txtMontoTotal.text.toString()
            val numeroLimpio = textoActual.replace("€", "").replace(",", ".").trim()
            val totalActualNum = numeroLimpio.toDoubleOrNull() ?: 0.0

            val nuevoTotalNum = totalActualNum - gastoPendiente
            val nuevoTextoFinal = "${String.format(Locale.US, "%.2f", nuevoTotalNum)}€"

            txtMontoTotal.text = nuevoTextoFinal

            val sharedPrefMain = getPreferences(Context.MODE_PRIVATE)
            sharedPrefMain.edit {
                putString("monto_total", nuevoTextoFinal)
            }

            sharedPrefRestar.edit {
                putFloat("gasto_pendiente", 0.0f)
            }
        }
    }

    private fun calcularYActualizarMedia() {
        val sharedPrefGastos = getSharedPreferences("HistoricoGastos", Context.MODE_PRIVATE)
        val stringGastos = sharedPrefGastos.getString("lista_gastos", "") ?: ""

        val sharedPrefSuscri = getSharedPreferences("HistoricoSuscripciones", Context.MODE_PRIVATE)
        val stringSuscri = sharedPrefSuscri.getString("lista_suscripciones", "") ?: ""

        var sumaTotal = 0.0
        var contadorElementos = 0

        if (stringGastos.isNotEmpty()) {
            val listaGastos = stringGastos.split(";")
            for (gasto in listaGastos) {
                if (gasto.isBlank()) continue
                sumaTotal += extraerMontoNumerico(gasto)
                contadorElementos++
            }
        }

        if (stringSuscri.isNotEmpty()) {
            val listaSuscri = stringSuscri.split(";")
            for (suscripcion in listaSuscri) {
                if (suscripcion.isBlank()) continue
                sumaTotal += extraerMontoNumerico(suscripcion)
                contadorElementos++
            }
        }

        val media = if (contadorElementos > 0) sumaTotal / contadorElementos else 0.0
        txtMediaMensual.text = "${String.format(Locale.US, "%.2f", media)}€"
    }

    private fun extraerMontoNumerico(registro: String): Double {
        return try {
            val partes = registro.split(":")
            if (partes.size > 1) {
                val precioTexto = partes[1].replace("€", "").replace(",", ".").trim()
                precioTexto.toDoubleOrNull() ?: 0.0
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarListaGastos() {
        contenedorGastos.removeAllViews()
        val sharedPref = getSharedPreferences("HistoricoGastos", Context.MODE_PRIVATE)
        val stringGastos = sharedPref.getString("lista_gastos", "") ?: ""

        if (stringGastos.isNotEmpty()) {
            val listaGastos = stringGastos.split(";")
            for (gasto in listaGastos) {
                if (gasto.isBlank()) continue
                val tvGasto = TextView(this).apply {
                    text = gasto
                    textSize = 14f
                    setPadding(4, 6, 4, 6)
                    setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.black))
                }
                contenedorGastos.addView(tvGasto)
            }
        } else {
            val tvVacio = TextView(this).apply {
                text = "Sin gastos."
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.darker_gray))
            }
            contenedorGastos.addView(tvVacio)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarListaSuscripciones() {
        contenedorSuscripciones.removeAllViews()
        val sharedPref = getSharedPreferences("HistoricoSuscripciones", Context.MODE_PRIVATE)
        val stringSuscri = sharedPref.getString("lista_suscripciones", "") ?: ""

        if (stringSuscri.isNotEmpty()) {
            val listaSuscri = stringSuscri.split(";")
            for (suscripcion in listaSuscri) {
                if (suscripcion.isBlank()) continue
                val tvSuscri = TextView(this).apply {
                    text = suscripcion
                    textSize = 14f
                    setPadding(4, 6, 4, 6)
                    setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.black))
                }
                contenedorSuscripciones.addView(tvSuscri)
            }
        } else {
            val tvVacio = TextView(this).apply {
                text = "Sin suscripciones."
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.darker_gray))
            }
            contenedorSuscripciones.addView(tvVacio)
        }
    }

    private fun mostrarDialogoIngreso(textView: TextView) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Actualizar Total")
        builder.setMessage("Introduce el nuevo monto:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "00.00"
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val textoIngresado = input.text.toString().replace(",", ".")
            val montoNum = textoIngresado.toDoubleOrNull() ?: 0.0

            if (textoIngresado.isNotEmpty()) {
                val valorFinal = "${String.format(Locale.US, "%.2f", montoNum)}€"
                textView.text = valorFinal

                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                sharedPref.edit {
                    putString("monto_total", valorFinal)
                }
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}