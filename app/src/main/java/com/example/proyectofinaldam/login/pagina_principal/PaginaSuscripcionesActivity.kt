package com.example.proyectofinaldam.login.pagina_principal

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinaldam.R

class PaginaSuscripcionesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_suscripciones)

        val etDescripcion = findViewById<EditText>(R.id.etDescripcionSuscripcion)
        val etMonto = findViewById<EditText>(R.id.etMontoSuscripcion)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarSuscripcion)

        btnRegistrar.setOnClickListener {
            val descripcion = etDescripcion.text.toString().trim()
            val montoStr = etMonto.text.toString().trim()

            if (descripcion.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val nuevaSuscripcion = "$descripcion: ${montoStr}€"

                // 1. Guardamos en el histórico de suscripciones
                val sharedPrefSuscri = getSharedPreferences("HistoricoSuscripciones", Context.MODE_PRIVATE)
                val listaActual = sharedPrefSuscri.getString("lista_suscripciones", "") ?: ""
                val listaActualizada = if (listaActual.isEmpty()) nuevaSuscripcion else "$listaActual;$nuevaSuscripcion"

                with(sharedPrefSuscri.edit()) {
                    putString("lista_suscripciones", listaActualizada)
                    apply()
                }

                // 2. Restamos al total disponible reusando tu lógica actual
                val montoDoble = montoStr.toDoubleOrNull() ?: 0.0
                val sharedPrefRestar = getSharedPreferences("RestarGastos", Context.MODE_PRIVATE)
                with(sharedPrefRestar.edit()) {
                    putFloat("gasto_pendiente", montoDoble.toFloat())
                    apply()
                }

                Toast.makeText(this, "Suscripción añadida con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}