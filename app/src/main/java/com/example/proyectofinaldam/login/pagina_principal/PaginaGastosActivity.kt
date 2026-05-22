package com.example.proyectofinaldam.login.pagina_principal

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinaldam.R

class PaginaGastosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_gastos)

        val etDescripcionGasto = findViewById<EditText>(R.id.etDescripcionGasto)
        val etMontoGasto = findViewById<EditText>(R.id.etMontoGasto)
        val btnRegistrarGasto = findViewById<Button>(R.id.btnRegistrarGasto)

        btnRegistrarGasto.setOnClickListener {
            val descripcion = etDescripcionGasto.text.toString().trim()
            val montoStr = etMontoGasto.text.toString().trim()

            if (descripcion.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Formateamos el registro individual para la lista
                val nuevoGasto = "$descripcion: ${montoStr}€"

                // 1. Guardamos en el histórico de la lista
                val sharedPrefGastos = getSharedPreferences("HistoricoGastos", Context.MODE_PRIVATE)
                val listaActual = sharedPrefGastos.getString("lista_gastos", "") ?: ""
                val listaActualizada = if (listaActual.isEmpty()) nuevoGasto else "$listaActual;$nuevoGasto"

                with(sharedPrefGastos.edit()) {
                    putString("lista_gastos", listaActualizada)
                    apply()
                }

                // 2. NUEVO: Guardamos el valor numérico para indicarle al Main que debe restarlo
                val montoDoble = montoStr.toDoubleOrNull() ?: 0.0
                val sharedPrefRestar = getSharedPreferences("RestarGastos", Context.MODE_PRIVATE)
                with(sharedPrefRestar.edit()) {
                    putFloat("gasto_pendiente", montoDoble.toFloat())
                    apply()
                }

                Toast.makeText(this, "Gasto guardado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}