package com.example.proyectofinaldam.login.pagina_principal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.proyectofinaldam.R
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class MainAplicacion : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var contenedorGastos: LinearLayout
    private lateinit var txtMontoTotal: TextView
    private lateinit var txtMediaMensual: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagina_principal)

        // Inicializar Drawer Layout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        // Evento para abrir las barritas al pulsarlo
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Acciones al hacer click en los elementos del panel de ajustes
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_modo_oscuro -> {
                    cambiarModoOscuro()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_borrar_datos -> {
                    mostrarDialogoConfirmarBorrado()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        val btnTotal = findViewById<LinearLayout>(R.id.BotonGastos)
        txtMontoTotal = findViewById(R.id.txtMontoTotal)
        txtMediaMensual = findViewById(R.id.txtMediaMensual)
        val btnIrAGastos = findViewById<Button>(R.id.botongasto)

        contenedorGastos = findViewById(R.id.contenedorGastos)

        // Cargamos el total inicial guardado
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val montoGuardado = sharedPref.getString("monto_total", "00.00€")
        txtMontoTotal.text = montoGuardado

        btnTotal.setOnClickListener {
            mostrarDialogoIngreso(txtMontoTotal)
        }

        btnIrAGastos.setOnClickListener {
            val intent = Intent(this, PaginaGastosActivity::class.java)
            startActivity(intent)
        }

        // SOLUCCIÓN AL RECUADRO AMARILLO: Manejo del botón atrás moderno
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        procesarRestaGasto()
        actualizarListaGastos()
    }

    // AJUSTE: Cambiar entre Modo Claro y Oscuro
    private fun cambiarModoOscuro() {
        val modoActual = AppCompatDelegate.getDefaultNightMode()
        if (modoActual == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Toast.makeText(this, "Modo Claro Activado", Toast.LENGTH_SHORT).show()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Toast.makeText(this, "Modo Oscuro Activado", Toast.LENGTH_SHORT).show()
        }
    }

    // AJUSTE: Diálogo de confirmación para limpiar los datos
    private fun mostrarDialogoConfirmarBorrado() {
        AlertDialog.Builder(this)
            .setTitle("Borrar todos los datos")
            .setMessage("¿Estás seguro de que quieres restablecer la aplicación? Se perderán todos tus gastos guardados.")
            .setCancelable(false)
            .setPositiveButton("Borrar Todo") { _, _ ->
                borrarTodaLaData()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Limpieza absoluta de SharedPreferences e interfaz gráfica
    @SuppressLint("SetTextI18n")
    private fun borrarTodaLaData() {
        getPreferences(Context.MODE_PRIVATE).edit { clear() }
        getSharedPreferences("HistoricoGastos", Context.MODE_PRIVATE).edit { clear() }
        getSharedPreferences("RestarGastos", Context.MODE_PRIVATE).edit { clear() }

        txtMontoTotal.text = "00.00€"
        txtMediaMensual.text = "00.00€"
        contenedorGastos.removeAllViews()

        actualizarListaGastos()
        Toast.makeText(this, "Todos los datos han sido eliminados", Toast.LENGTH_SHORT).show()
    }

    private fun procesarRestaGasto() {
        val sharedPrefRestar = getSharedPreferences("RestarGastos", Context.MODE_PRIVATE)
        val gastoPendiente = sharedPrefRestar.getFloat("gasto_pendiente", 0.0f)

        if (gastoPendiente > 0) {
            val textoActual = txtMontoTotal.text.toString()
            val numeroLimpio = textoActual.replace("€", "").replace(",", ".").trim()
            val totalActualNum = numeroLimpio.toDoubleOrNull() ?: 0.0

            val nuevoTotalNum = totalActualNum - gastoPendiente
            val nuevoTextoFinal = "${String.format(Locale.US, "%.2f", nuevoTotalNum)}€"

            txtMontoTotal.text = nuevoTextoFinal

            val sharedPrefMain = getPreferences(Context.MODE_PRIVATE)
            sharedPrefMain.edit {
                putString("monto_total", nuevoTextoFinal)
            }

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
                if (gasto.isBlank()) continue

                val tvGasto = TextView(this).apply {
                    text = gasto
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                    setTextColor(ContextCompat.getColor(this@MainAplicacion, android.R.color.black))
                }
                contenedorGastos.addView(tvGasto)
            }
        } else {
            val tvVacio = TextView(this).apply {
                text = "No hay gastos registrados todavía."
                textSize = 14f
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
            val textoIngresado = input.text.toString().replace(",", ".")
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