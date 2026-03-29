package com.example.simplecalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SimpleCalcActivity : AppCompatActivity() {
    private var isNewOperation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simple_calc)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)

        val numberIDs = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btnComma
        )

        numberIDs.forEach { id ->
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                val number = button.text.toString()

                if (isNewOperation) {
                    // Якщо починаємо з коми, робимо "0,"
                    if (number == ",") tvDisplay.text = "0,"
                    else tvDisplay.text = number
                    isNewOperation = false
                } else {
                    if (number == ",") {
                        if (canAddComa(tvDisplay.text.toString())) {
                            tvDisplay.append(",")
                        }
                    } else {
                        // Прибираємо початковий нуль, якщо це не "0,"
                        if (tvDisplay.text.toString() == "0") {
                            tvDisplay.text = number
                        } else {
                            tvDisplay.append(number)
                        }
                    }
                }
            }
        }

        val operationIDs = listOf(R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide)

        operationIDs.forEach { id ->
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                val displayText = tvDisplay.text.toString()
                // Додаємо знак, тільки якщо в кінці не інший знак чи кома
                if (displayText.isNotEmpty() && !isLastCharOperator(displayText)) {
                    tvDisplay.append(button.text.toString())
                    isNewOperation = false
                }
            }
        }

        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            val text = tvDisplay.text.toString()
            // Просто додаємо символ %, розрахунок буде в calculateResult
            if (text.isNotEmpty() && text.last().isDigit()) {
                tvDisplay.append("%")
                isNewOperation = false
            }
        }

        findViewById<Button>(R.id.btnAC).setOnClickListener {
            tvDisplay.text = "0"
            isNewOperation = true
        }

        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener {
            val text = tvDisplay.text.toString()
            // Працює коректно тільки для одного числа на екрані
            if (text.isNotEmpty() && !text.contains(Regex("[+×÷-]"))) {
                val currentValue = text.replace(",", ".").toDouble()
                tvDisplay.text = formatResult(-currentValue)
            }
        }

        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            val expression = tvDisplay.text.toString()
            if (expression.isNotEmpty() && !isLastCharOperator(expression)) {
                try {
                    val result = calculateResult(expression)
                    tvDisplay.text = formatResult(result)
                    isNewOperation = true
                } catch (e: Exception) {
                    tvDisplay.text = "Error"
                    isNewOperation = true
                }
            }
        }
    }

    private fun isLastCharOperator(displayText: String): Boolean {
        if (displayText.isEmpty()) return false
        val lastChar = displayText.last()
        return lastChar == '+' || lastChar == '-' || lastChar == '×' || lastChar == '÷' || lastChar == ','
    }

    private fun canAddComa(text: String): Boolean {
        // Розбиваємо вираз, щоб перевірити кому тільки в останньому числі
        val parts = text.split("+", "-", "×", "÷", "%")
        val lastNumber = parts.last()
        return !lastNumber.contains(",")
    }

    private fun calculateResult(expression: String): Double {
        val replaced = expression.replace("×", "*")
            .replace("÷", "/")
            .replace(",", ".")

        // розбиваємо рядок, зберігаючи оператори та знак %
        val tokens = replaced.split(Regex("(?<=[-+*/%])|(?=[-+*/%])"))
            .filter { it.isNotBlank() }
            .toMutableList()

        if (tokens.isEmpty()) return 0.0

        // Обробка відсотків
        var i = 0
        while (i < tokens.size) {
            if (tokens[i] == "%") {
                val percentageValue = tokens[i - 1].toDouble()

                // Якщо є база (напр. 12 - 25%)
                if (i >= 3 && (tokens[i - 2] == "+" || tokens[i - 2] == "-")) {
                    val baseValue = tokens[i - 3].toDouble()
                    val calculatedPercent = baseValue * (percentageValue / 100)
                    tokens[i - 1] = calculatedPercent.toString()
                } else {
                    // Якщо просто число (напр. 5 * 10%)
                    tokens[i - 1] = (percentageValue / 100).toString()
                }
                tokens.removeAt(i) // Видаляємо символ %
                i--
            }
            i++
        }

        var result = tokens[0].toDouble()
        var j = 1
        while (j < tokens.size) {
            val operator = tokens[j]
            val nextVal = tokens[j + 1].toDouble()

            when (operator) {
                "+" -> result += nextVal
                "-" -> result -= nextVal
                "*" -> result *= nextVal
                "/" -> if (nextVal != 0.0) result /= nextVal else throw ArithmeticException()
            }
            j += 2
        }
        return result
    }

    private fun formatResult(result: Double): String {
        return if (result % 1 == 0.0) result.toLong().toString()
        else result.toString().replace(".", ",")
    }
}