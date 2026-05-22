package com.example.proyectofinaldam.login.pagina_principal

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
import com.example.proyectofinaldam.R

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
        val montoGuardado = sharedPref.getString("monto_total", "00,00€")
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
            // Obtenemos el texto actual del total (ej: "150.50€" o "00,00€")
            val textoActual = txtMontoTotal.text.toString()

            // Limpiamos el texto para quedarnos solo con el número (quitamos '€' y espacios)
            val numeroLimpio = textoActual.replace("€", "").replace(",", ".").trim()
            val totalActualNum = numeroLimpio.toDoubleOrNull() ?: 0.0

            // Realizamos la resta matemática
            val nuevoTotalNum = totalActualNum - gastoPendiente
            val nuevoTextoFinal = "${String.format("%.2f", nuevoTotalNum)}€"

            // Actualizamos el TextView en la pantalla
            txtMontoTotal.text = nuevoTextoFinal

            // Guardamos el nuevo total de forma permanente en las preferencias del Main
            val sharedPrefMain = getPreferences(Context.MODE_PRIVATE)
            with(sharedPrefMain.edit()) {
                putString("monto_total", nuevoTextoFinal)
                apply()
            }

            // Consumimos el gasto pendiente poniéndolo a 0 para que no se vuelva a restar al rotar la pantalla
            with(sharedPrefRestar.edit()) {
                putFloat("gasto_pendiente", 0.0f)
                apply()
            }
        }
    }

    private fun actualizarListaGastos() {
        contenedorGastos.removeAllViews()

        val sharedPref = getSharedPreferences("HistoricoGastos", Context.MODE_PRIVATE)
        val stringGastos = sharedPref.getString("lista_gastos", "") ?: ""

        if (stringGastos.isNotEmpty()) {
            val listaGastos = stringGastos.split(";")

            for (gasto in listaGastos) {
                val tvGasto = TextView(this)
                tvGasto.text = gasto
                tvGasto.textSize = 16f
                tvGasto.setPadding(0, 8, 0, 8)
                tvGasto.setTextColor(resources.getColor(android.R.color.black))

                contenedorGastos.addView(tvGasto)
            }
        } else {
            val tvVacio = TextView(this)
            tvVacio.text = "No hay gastos registrados todavía."
            tvVacio.textSize = 14f
            tvVacio.setTextColor(resources.getColor(android.R.color.darker_gray))
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
            val textoIngresado = input.text.toString()
            if (textoIngresado.isNotEmpty()) {
                val valorFinal = "${textoIngresado}€"
                textView.text = valorFinal

                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("monto_total", valorFinal)
                    apply()
                }
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}