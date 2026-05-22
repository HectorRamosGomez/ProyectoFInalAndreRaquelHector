package com.example.proyectofinaldam.login.pagina_principal

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
                // Formateamos el registro individual
                val nuevoGasto = "$descripcion: ${montoStr}€"

                // Guardamos en un archivo común de SharedPreferences
                val sharedPref = getSharedPreferences("HistoricoGastos", Context.MODE_PRIVATE)
                val listaActual = sharedPref.getString("lista_gastos", "") ?: ""

                // Agregamos el nuevo registro al listado existente usando el separador ";"
                val listaActualizada = if (listaActual.isEmpty()) nuevoGasto else "$listaActual;$nuevoGasto"

                with(sharedPref.edit()) {
                    putString("lista_gastos", listaActualizada)
                    apply()
                }

                Toast.makeText(this, "Gasto guardado con éxito", Toast.LENGTH_SHORT).show()
                finish() // Volver automáticamente atrás
            }
        }
    }
}