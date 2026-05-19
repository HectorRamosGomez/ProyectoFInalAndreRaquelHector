package com.example.proyectofinaldam.login.pagina_principal

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinaldam.R

class MainAplicacion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_principal) // Asegúrate de que el nombre coincida con tu XML

        val btnTotal = findViewById<LinearLayout>(R.id.BotonGastos)
        val txtMontoTotal = findViewById<TextView>(R.id.txtMontoTotal) // El ID que añadimos al TextView del monto

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val montoGuardado = sharedPref.getString("monto_total", "00,00€")
        txtMontoTotal.text = montoGuardado

        btnTotal.setOnClickListener {
            mostrarDialogoIngreso(txtMontoTotal)
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