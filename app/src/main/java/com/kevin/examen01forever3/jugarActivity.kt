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

        // Obtener referencias a los dados
        val dice1 = findViewById<ImageView>(R.id.dice1)
        val dice2 = findViewById<ImageView>(R.id.dice2)
        val dice3 = findViewById<ImageView>(R.id.dice3)

        // Animar los dados
        animarDados(dice1, dice2, dice3, resultados)

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

        // Condiciones para verificar si el jugador ha ganado 3 veces seguidas o si perdiótodo su dinero
        verificarFinJuego()
    }

    private fun isBetValid(betString: String): Boolean {
        return betString.isNotEmpty() && betString.toInt() >= 2000000 // Validar apuesta mínima de 2 millones
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
        if (availableFunds <= 0) { // Verificar si se ha perdidotodo el dinero
            // Si se ha perdidotodo el dinero, enviar a finalActivity
            val intent = Intent(this, finalActivity::class.java)
            intent.putExtra("HAS_WON", false)  // No ha ganado si ha perdidotodo el dinero
            startActivity(intent)
            finish()  // Finaliza la actividad actual
        } else if (consecutiveWins >= 3) { // Verificar si ha ganado 3 veces seguidas
            val intent = Intent(this, finalActivity::class.java)
            intent.putExtra("HAS_WON", true)  // Ha ganado si ha ganado 3 veces seguidas
            startActivity(intent)
            finish()  // Finaliza la actividad actual
        }
    }

    // Animación de los dados
    private fun animarDados(dice1: ImageView, dice2: ImageView, dice3: ImageView, resultados: List<Int>) {
        val handler = Handler(Looper.getMainLooper())
        val delay = 100L // Tiempo entre cambios de imagen

        // Animación para cambiar las imágenes de los dados rápidamente
        for (i in 1..6) {
            handler.postDelayed({
                // Cambia las imágenes de los dados durante la animación
                val resId1 = resources.getIdentifier("cara$i", "drawable", packageName)
                dice1.setImageResource(resId1)

                val resId2 = resources.getIdentifier("cara$i", "drawable", packageName)
                dice2.setImageResource(resId2)

                if (dice3.visibility == View.VISIBLE) {
                    val resId3 = resources.getIdentifier("cara$i", "drawable", packageName)
                    dice3.setImageResource(resId3)
                }
            }, delay * i)
        }

        // Después de la animación, muestra el resultado real de los dados
        handler.postDelayed({
            mostrarResultadoDados(dice1, dice2, dice3, resultados)
        }, delay * 6)
    }

    // Mostrar el resultado final de los dados
    private fun mostrarResultadoDados(dice1: ImageView, dice2: ImageView, dice3: ImageView, resultados: List<Int>) {
        dice1.setImageResource(resources.getIdentifier("cara${resultados[0]}", "drawable", packageName))
        dice2.setImageResource(resources.getIdentifier("cara${resultados[1]}", "drawable", packageName))

        if (dice3.visibility == View.VISIBLE) {
            dice3.setImageResource(resources.getIdentifier("cara${resultados[2]}", "drawable", packageName))
        }
    }
}
