package com.kevin.examen01forever3

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var txtPlayerName: EditText
    private lateinit var txtFunds: EditText
    private lateinit var txtBirthdate: EditText
    private lateinit var numberPickerPieces: NumberPicker
    private lateinit var btnPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {super.onCreate(savedInstanceState)

        // Inicializa SharedPreferences y establece el idioma
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val language = sharedPreferences.getString("app_language", "es")
        setLocale(language)

        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        txtPlayerName = findViewById(R.id.etPlayerName)
        txtFunds = findViewById(R.id.etFunds)
        txtBirthdate = findViewById(R.id.etBirthdate)
        numberPickerPieces = findViewById(R.id.numberPickerPieces)
        btnPlay = findViewById(R.id.btnPlay)

        // Configura el NumberPicker
        numberPickerPieces.minValue = 2
        numberPickerPieces.maxValue = 3

        // Listener para abrir el DatePickerDialog
        txtBirthdate.setOnClickListener {
            showDatePickerDialog()
        }

        // Listener para el botón de jugar
        btnPlay.setOnClickListener {
            val playerName = txtPlayerName.text.toString()
            val funds = txtFunds.text.toString().toIntOrNull()
            val birthdate = txtBirthdate.text.toString()
            val dieces = numberPickerPieces.value

            // Validaciones
            if (playerName.isEmpty()) {
                showToast(getString(R.string.empty_name_error))
                return@setOnClickListener
            }
            if (funds == null || funds < 2000000) {
                showToast(getString(R.string.insufficient_funds_error))
                return@setOnClickListener
            }
            if (birthdate.isEmpty()) {
                showToast(getString(R.string.empty_birthdate_error))
                return@setOnClickListener
            }

            // Verificar si el jugador tiene al menos 21 años
            val age = calculateAge(birthdate)
            if (age < 21) {
                showToast(getString(R.string.age_limit_error))
                return@setOnClickListener
            }

            // Crear el Intent para iniciar jugarActivity
            val intent = Intent(this, jugarActivity::class.java).apply {
                putExtra("PLAYER_NAME", playerName)
                putExtra("FUNDS", funds)
                putExtra("BIRTHDATE", birthdate)
                putExtra("DIECES", dieces)
            }
            startActivity(intent)
        }
    }

    private fun setLocale(language: String?) {
        val locale = Locale(language ?: "es") // Por defecto a español
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }


    // Muestra el DatePickerDialog
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                txtBirthdate.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    // Función para calcular la edad del jugador
    private fun calculateAge(birthdate: String): Int {
        val parts = birthdate.split("/")
        if (parts.size != 3) return 0

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        val birthCalendar = Calendar.getInstance()
        birthCalendar.set(year, month - 1, day)

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    // Función para mostrar mensajes Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
