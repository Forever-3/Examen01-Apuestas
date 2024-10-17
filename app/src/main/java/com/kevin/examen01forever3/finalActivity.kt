package com.kevin.examen01forever3

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class finalActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)

        // Obtener los datos del Intent
        val hasWon = intent.getBooleanExtra("HAS_WON", false)

        // Obtener las referencias a los elementos de la UI
        val statusTextView = findViewById<TextView>(R.id.status)
        val statusImageView = findViewById<ImageView>(R.id.statusImageView)

        // Actualizar la UI basado en si el jugador ganó o perdió
        if (hasWon) {
            statusTextView.text = "¡Ganador!"
            statusImageView.setImageResource(R.drawable.ganar1)  // Cambiar a imagen de "ganador"
        } else {
            statusTextView.text = "Perdiste"
            statusImageView.setImageResource(R.drawable.perder1)  // Cambiar a imagen de "perdedor"
        }
    }
}
