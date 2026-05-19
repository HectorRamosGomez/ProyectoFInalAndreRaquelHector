package com.example.proyectofinaldam.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinaldam.login.MainActivity2Idiomas
import com.example.proyectofinaldam.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagina_inicio_aplicacion)

        val button = findViewById<Button>(R.id.botonComenzar)

        button.setOnClickListener {

            val intent = Intent(this, MainActivity2Idiomas::class.java)

        }


    }
}