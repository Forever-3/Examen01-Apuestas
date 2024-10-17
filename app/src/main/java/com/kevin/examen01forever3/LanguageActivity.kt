package com.kevin.examen01forever3

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class LanguageActivity : AppCompatActivity() {

    private lateinit var languageRadioGroup: RadioGroup
    private lateinit var btnSave: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        languageRadioGroup = findViewById(R.id.radioGroupLanguages)
        btnSave = findViewById(R.id.btnSave)

        // Configura el RadioGroup según el idioma guardado
        when (sharedPreferences.getString("app_language", "es")) {
            "es" -> languageRadioGroup.check(R.id.radioSpanish)
            "pt" -> languageRadioGroup.check(R.id.radioPortuguese)
            "en" -> languageRadioGroup.check(R.id.radioEnglish)
        }

        btnSave.setOnClickListener {
            val selectedLanguage = when (languageRadioGroup.checkedRadioButtonId) {
                R.id.radioSpanish -> "es"
                R.id.radioPortuguese -> "pt"
                R.id.radioEnglish -> "en"
                else -> "es" // Valor por defecto
            }
            saveLanguage(selectedLanguage)
            restartApp()
        }
    }

    private fun saveLanguage(language: String) {
        val editor = sharedPreferences.edit()
        editor.putString("app_language", language)
        editor.apply()
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
