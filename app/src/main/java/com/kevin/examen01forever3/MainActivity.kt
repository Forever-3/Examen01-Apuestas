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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                showToast("Por favor, ingresa tu nombre.")
                return@setOnClickListener
            }
            if (funds == null || funds < 2000000) {
                showToast("El monto de fondos debe ser al menos 2 millones.")
                return@setOnClickListener
            }
            if (birthdate.isEmpty()) {
                showToast("Por favor, selecciona tu fecha de nacimiento.")
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

    // Función para mostrar mensajes Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
