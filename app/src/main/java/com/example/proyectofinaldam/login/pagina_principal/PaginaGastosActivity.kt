package com.example.proyectofinaldam.login.pagina_principal

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
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)

        btnRegistrarGasto.setOnClickListener {
            val descripcion = etDescripcionGasto.text.toString().trim()
            val montoStr = etMontoGasto.text.toString().trim()

            if (descripcion.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val monto = montoStr.toDouble()

                Toast.makeText(this, "Registrado: $descripcion por $monto €", Toast.LENGTH_LONG).show()

                etDescripcionGasto.text.clear()
                etMontoGasto.text.clear()
            }
        }

        btnMenu.setOnClickListener {
            Toast.makeText(this, "Menú", Toast.LENGTH_SHORT).show()
        }

        btnProfile.setOnClickListener {
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
        }
    }
}