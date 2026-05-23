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
    private lateinit var txtMontoTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_principal)

        val btnTotal = findViewById<LinearLayout>(R.id.BotonGastos)
        txtMontoTotal = findViewById(R.id.txtMontoTotal)
        val btnIrAGastos = findViewById<Button>(R.id.botongasto)

        contenedorGastos = findViewById(R.id.contenedorGastos)

        // Cargamos el total inicial guardado
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val montoGuardado = sharedPref.getString("monto_total", "00.00€") // Usamos punto por consistencia
        txtMontoTotal.text = montoGuardado

        btnTotal.setOnClickListener {
            mostrarDialogoIngreso(txtMontoTotal)
        }

        btnIrAGastos.setOnClickListener {
            val intent = Intent(this, PaginaGastosActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 1. Primero procesamos si hay alguna resta de gasto pendiente
        procesarRestaGasto()
        // 2. Después actualizamos la lista visual inferior
        actualizarListaGastos()
    }

    private fun procesarRestaGasto() {
        val sharedPrefRestar = getSharedPreferences("RestarGastos", Context.MODE_PRIVATE)
        val gastoPendiente = sharedPrefRestar.getFloat("gasto_pendiente", 0.0f)

        // Si hay un gasto mayor que 0 pendiente de restar
        if (gastoPendiente > 0) {
            // Obtenemos el texto actual del total (ej: "150.50€" o "00.00€")
            val textoActual = txtMontoTotal.text.toString()

            // Limpiamos el texto para quedarnos solo con el número (quitamos '€' y espacios)
            val numeroLimpio = textoActual.replace("€", "").replace(",", ".").trim()
            val totalActualNum = numeroLimpio.toDoubleOrNull() ?: 0.0

            // Realizamos la resta matemática
            val nuevoTotalNum = totalActualNum - gastoPendiente

            // Usamos Locale.US para asegurar que el formato use siempre punto (.) en vez de coma (,)
            // Esto evita errores al volver a parsear el número más adelante
            val nuevoTextoFinal = "${String.format(Locale.US, "%.2f", nuevoTotalNum)}€"

            // Actualizamos el TextView en la pantalla
            txtMontoTotal.text = nuevoTextoFinal

            // Guardamos el nuevo total de forma permanente en las preferencias del Main
            val sharedPrefMain = getPreferences(Context.MODE_PRIVATE)
            sharedPrefMain.edit {
                putString("monto_total", nuevoTextoFinal)
            }

            // Consumimos el gasto pendiente poniéndolo a 0
            sharedPrefRestar.edit {
                putFloat("gasto_pendiente", 0.0f)
            }
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
                if (gasto.isBlank()) continue // Evita añadir líneas en blanco innecesarias

                val tvGasto = TextView(this).apply {
                    text = gasto
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                    // CORRECCIÓN: Evitamos recursos deprecados usando ContextCompat
                    setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.black))
                }

                contenedorGastos.addView(tvGasto)
            }
        } else {
            val tvVacio = TextView(this).apply {
                text = "No hay gastos registrados todavía."
                textSize = 14f
                // CORRECCIÓN: ContextCompat aquí también
                setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.darker_gray))
            }
            contenedorGastos.addView(tvVacio)
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
            val textoIngresado = input.text.toString().replace(",", ".") // Aseguramos punto decimal
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