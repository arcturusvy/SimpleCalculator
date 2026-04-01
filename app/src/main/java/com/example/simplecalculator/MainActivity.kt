package com.example.simplecalculator

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val simpleButton = findViewById<Button>(R.id.button)
        val advancedButton = findViewById<Button>(R.id.button4)
        val aboutButton = findViewById<Button>(R.id.button5)
        val exitButton = findViewById<Button>(R.id.button6)

        simpleButton.setOnClickListener {
            val intent = Intent(this, SimpleCalcActivity::class.java)
            startActivity(intent)
        }

        advancedButton.setOnClickListener {
            val intent = Intent(this, AdvancedCalcActivity::class.java)
            startActivity(intent)
        }

        aboutButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.about_title))
            builder.setMessage(getString(R.string.about_message))
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        exitButton.setOnClickListener {
            finishAffinity()
        }
    }
}