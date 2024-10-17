package com.kevin.examen01forever3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class jugarActivity : AppCompatActivity() {

    private var availableFunds = 0
    private var betAmount = 0
    private var wins = 0
    private var consecutiveWins = 0
    private var selectedRadioButtonId: Int? = null
    private var initialFunds = 0 // Almacena los fondos iniciales

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jugar_layout)

        val playerName = intent.getStringExtra("PLAYER_NAME") ?: ""
        initialFunds = intent.getIntExtra("FUNDS", 0) // Almacena los fondos iniciales
        availableFunds = initialFunds // Configura los fondos disponibles a los fondos iniciales
        val numberOfDice = intent.getIntExtra("DIECES", 2)

        findViewById<EditText>(R.id.player).setText(playerName)
        findViewById<EditText>(R.id.funds).setText(availableFunds.toString())

        val dice1 = findViewById<ImageView>(R.id.dice1)
        val dice2 = findViewById<ImageView>(R.id.dice2)
        val dice3 = findViewById<ImageView>(R.id.dice3)

        dice1.visibility = View.VISIBLE
        dice2.visibility = View.VISIBLE
        dice3.visibility = if (numberOfDice == 3) View.VISIBLE else View.GONE

        configurarRadioButtons(numberOfDice)
        configurarRadioButtonsSeleccionables()
    }

    private fun configurarRadioButtons(numberOfDice: Int) {
        for (i in 1..18) {
            val radioButton = findViewById<RadioButton>(
                resources.getIdentifier("radioButton$i", "id", packageName)
            )
            radioButton.isEnabled = when (numberOfDice) {
                2 -> i in 2..12
                3 -> i in 3..18
                else -> false
            }
        }
    }

    private fun configurarRadioButtonsSeleccionables() {
        for (i in 1..18) {
            val radioButton = findViewById<RadioButton>(
                resources.getIdentifier("radioButton$i", "id", packageName)
            )
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedRadioButtonId?.let { previousId ->
                        val previousRadioButton = findViewById<RadioButton>(previousId)
                        previousRadioButton.isChecked = false
                    }
                    selectedRadioButtonId = buttonView.id
                } else if (selectedRadioButtonId == buttonView.id) {
                    selectedRadioButtonId = null
                }
            }
        }
    }

    fun lanzarDados(view: View) {
        val betEditText = findViewById<EditText>(R.id.funds_bet)
        val betString = betEditText.text.toString()

        if (!isBetValid(betString)) {
            Toast.makeText(this, "Ingresa un monto válido para apostar.", Toast.LENGTH_SHORT).show()
            return
        }

        betAmount = betString.toInt()

        if (betAmount > availableFunds) {
            Toast.makeText(this, "Fondos insuficientes", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedRadioButtonId == null) {
            Toast.makeText(this, "Selecciona un número para apostar.", Toast.LENGTH_SHORT).show()
            return
        }

        val resultados = generarResultadosDados()

        val dice1 = findViewById<ImageView>(R.id.dice1)
        val dice2 = findViewById<ImageView>(R.id.dice2)
        val dice3 = findViewById<ImageView>(R.id.dice3)

        animarDados(dice1, dice2, dice3, resultados)

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId!!)
        val selectedNumber = selectedRadioButton.text.toString().toInt()

        val totalDados = resultados.sum()
        val statusImageView = findViewById<ImageView>(R.id.statusImageView)

        if (totalDados == selectedNumber) {
            wins++
            consecutiveWins++
            availableFunds += betAmount
            statusImageView.setImageResource(R.drawable.ganar1)
        } else {
            consecutiveWins = 0
            availableFunds -= betAmount
            statusImageView.setImageResource(R.drawable.perder1)
        }

        actualizarFondos()
        verificarFinJuego()
    }

    private fun isBetValid(betString: String): Boolean {
        return betString.isNotEmpty() && betString.toIntOrNull() != null && betString.toInt() >= 100000
    }

    private fun generarResultadosDados(): List<Int> {
        val resultado1 = (1..6).random()
        val resultado2 = (1..6).random()
        val resultado3 = if (findViewById<ImageView>(R.id.dice3).visibility == View.VISIBLE) (1..6).random() else 0
        return listOf(resultado1, resultado2, resultado3)
    }

    private fun actualizarFondos() {
        findViewById<EditText>(R.id.funds).setText(availableFunds.toString())
    }

    private fun verificarFinJuego() {
        // Solo termina el juego si los fondos son 0 o si ha ganado tres veces consecutivas
        if (availableFunds <= 0 || consecutiveWins >= 3) {
            val intent = Intent(this, finalActivity::class.java)
            intent.putExtra("HAS_WON", consecutiveWins >= 3) // true si ganó tres veces
            intent.putExtra("INITIAL_FUNDS", initialFunds)
            intent.putExtra("FINAL_FUNDS", availableFunds)
            startActivity(intent)
            finish()
        }
    }

    private fun animarDados(dice1: ImageView, dice2: ImageView, dice3: ImageView, resultados: List<Int>) {
        val handler = Handler(Looper.getMainLooper())
        val delay = 100L

        for (i in 1..6) {
            handler.postDelayed({
                val resId1 = resources.getIdentifier("cara$i", "drawable", packageName)
                val resId2 = resources.getIdentifier("cara$i", "drawable", packageName)
                val resId3 = resources.getIdentifier("cara$i", "drawable", packageName)
                dice1.setImageResource(resId1)
                dice2.setImageResource(resId2)
                if (dice3.visibility == View.VISIBLE) {
                    dice3.setImageResource(resId3)
                }
            }, i * delay)
        }

        handler.postDelayed({
            mostrarResultadoDados(dice1, dice2, dice3, resultados)
        }, 6 * delay)
    }

    private fun mostrarResultadoDados(dice1: ImageView, dice2: ImageView, dice3: ImageView, resultados: List<Int>) {
        dice1.setImageResource(resources.getIdentifier("cara${resultados[0]}", "drawable", packageName))
        dice2.setImageResource(resources.getIdentifier("cara${resultados[1]}", "drawable", packageName))

        if (dice3.visibility == View.VISIBLE) {
            dice3.setImageResource(resources.getIdentifier("cara${resultados[2]}", "drawable", packageName))
        }
    }
}
