package com.example.proyectofinaldam.login

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinaldam.R

class MainActivity2Idiomas: AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_idiomas)


        val buttonImagen = findViewById<ImageButton>(R.id.btnSpain)

        buttonImagen.setOnClickListener {

            val intent = Intent(this, MainAplicacion::class.java)
            startActivity(intent)
        }

    }
}