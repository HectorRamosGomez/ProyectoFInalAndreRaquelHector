package com.example.proyectofinaldam.login.pagina_principal // Asegúrate de que use tu mismo paquete

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
        // 1. Enlazar la actividad con el diseño XML que creamos antes
        setContentView(R.layout.pagina_gastos)

        // 2. Vincular los componentes del XML con el código Kotlin
        val etDescripcionGasto = findViewById<EditText>(R.id.etDescripcionGasto)
        val etMontoGasto = findViewById<EditText>(R.id.etMontoGasto)
        val btnRegistrarGasto = findViewById<Button>(R.id.btnRegistrarGasto)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)

        // 3. Configurar la acción del botón negro central "Registrar Gasto"
        btnRegistrarGasto.setOnClickListener {
            val descripcion = etDescripcionGasto.text.toString().trim()
            val montoStr = etMontoGasto.text.toString().trim()

            // Validar que el usuario no deje campos vacíos
            if (descripcion.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val monto = montoStr.toDouble()

                // Por ahora mostramos un mensaje flotante con los datos introducidos
                Toast.makeText(this, "Registrado: $descripcion por $monto €", Toast.LENGTH_LONG).show()

                // Opcional: Limpiar los campos para que pueda meter otro gasto de inmediato
                etDescripcionGasto.text.clear()
                etMontoGasto.text.clear()
            }
        }

        // Acciones opcionales para los iconos superiores
        btnMenu.setOnClickListener {
            Toast.makeText(this, "Menú", Toast.LENGTH_SHORT).show()
        }

        btnProfile.setOnClickListener {
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
        }
    }
}