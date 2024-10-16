package com.kevin.examen01forever3

import android.os.Bundle
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
    private var consecutiveWins = 0  // Variable para contar victorias consecutivas
    private var selectedRadioButtonId: Int? = null  // Para almacenar el ID del RadioButton seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jugar_layout)

        // Recibir datos de la actividad anterior
        val playerName = intent.getStringExtra("PLAYER_NAME") ?: ""
        availableFunds = intent.getIntExtra("FUNDS", 0)
        val numberOfDice = intent.getIntExtra("DIECES", 2) // valor predeterminado de 2 dados

        // Mostrar nombre del jugador y fondos disponibles en el layout
        findViewById<EditText>(R.id.player).setText(playerName)
        findViewById<EditText>(R.id.funds).setText(availableFunds.toString())

        // Obtener referencias a las imágenes de dados en el layout
        val dice1 = findViewById<ImageView>(R.id.dice1)
        val dice2 = findViewById<ImageView>(R.id.dice2)
        val dice3 = findViewById<ImageView>(R.id.dice3)

        // Mostrar/ocultar imágenes de dados según el número seleccionado
        dice1.visibility = View.VISIBLE
        dice2.visibility = View.VISIBLE
        dice3.visibility = if (numberOfDice == 3) View.VISIBLE else View.GONE

        // Deshabilitar RadioButtons según la cantidad de dados seleccionados
        configurarRadioButtons(numberOfDice)

        // Configurar RadioButtons para que sólo uno se seleccione
        configurarRadioButtonsSeleccionables()
    }

    private fun configurarRadioButtons(numberOfDice: Int) {
        for (i in 1..18) {
            val radioButton = findViewById<RadioButton>(
                resources.getIdentifier("radioButton$i", "id", packageName)
            )
            radioButton.isEnabled = when (numberOfDice) {
                2 -> i in 2..12  // Habilitar solo valores entre 2 y 12 para 2 dados
                3 -> i in 3..18  // Habilitar solo valores entre 3 y 18 para 3 dados
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
                    // Si se selecciona este RadioButton, desmarcar los otros
                    selectedRadioButtonId?.let { previousId ->
                        val previousRadioButton = findViewById<RadioButton>(previousId)
                        previousRadioButton.isChecked = false
                    }
                    // Actualizar el ID del RadioButton seleccionado
                    selectedRadioButtonId = buttonView.id
                } else if (selectedRadioButtonId == buttonView.id) {
                    // Si se deselecciona el RadioButton actualmente seleccionado, establecerlo como null
                    selectedRadioButtonId = null
                }
            }
        }
    }

    // Lógica para lanzar los dados y determinar si el jugador gana o pierde
    fun lanzarDados(view: View) {
        // Obtener el monto apostado
        val betEditText = findViewById<EditText>(R.id.funds_bet)
        val betString = betEditText.text.toString()

        // Validar que el monto apostado sea un número positivo
        if (!isBetValid(betString)) {
            Toast.makeText(this, "Ingresa un monto válido para apostar.", Toast.LENGTH_SHORT).show()
            return
        }

        betAmount = betString.toInt()

        // Verificar si tiene suficiente dinero para apostar
        if (betAmount > availableFunds) {
            Toast.makeText(this, "Fondos insuficientes", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si se ha seleccionado un RadioButton
        if (selectedRadioButtonId == null) {
            Toast.makeText(this, "Selecciona un número para apostar.", Toast.LENGTH_SHORT).show()
            return
        }

        // Generar resultados aleatorios de los dados
        val resultados = generarResultadosDados()

        // Obtener la selección del número apostado
        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId!!)
        val selectedNumber = selectedRadioButton.text.toString().toInt()

        // Sumar los resultados de los dados
        val totalDados = resultados.sum()

        // Obtener la referencia a la ImageView de estado
        val statusImageView = findViewById<ImageView>(R.id.statusImageView)

        // Verificar si el jugador ganó o perdió y cambiar la imagen de estado
        if (totalDados == selectedNumber) {
            wins++
            consecutiveWins++  // Incrementar victorias consecutivas
            availableFunds += betAmount  // Gana el monto apostado
            statusImageView.setImageResource(R.drawable.ganar1)  // Cambiar a imagen de "ganaste"
        } else {
            consecutiveWins = 0  // Reiniciar victorias consecutivas
            availableFunds -= betAmount  // Pierde el monto apostado
            statusImageView.setImageResource(R.drawable.perder1)  // Cambiar a imagen de "perdiste"
        }

        // Actualizar los fondos disponibles
        actualizarFondos()

        // Condiciones para verificar si el jugador ha ganado 3 veces seguidas o si perdió todo su dinero
        verificarFinJuego()
    }

    private fun isBetValid(betString: String): Boolean {
        return betString.isNotEmpty() && betString.toInt() > 0
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
        if (consecutiveWins >= 3) {
            Toast.makeText(this, "¡Ganaste 3 veces seguidas! ¡Felicidades!", Toast.LENGTH_SHORT).show()
            // Aquí puedes pasar a la siguiente actividad o finalizar el juego
            finish()
        } else if (availableFunds <= 0) {
            Toast.makeText(this, "Te has quedado sin dinero. Fin del juego.", Toast.LENGTH_SHORT).show()
            // Aquí puedes pasar a la siguiente actividad o finalizar el juego
            finish()
        }
    }
}
