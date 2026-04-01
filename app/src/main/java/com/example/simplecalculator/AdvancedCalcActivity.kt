package com.example.simplecalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.*

class AdvancedCalcActivity : AppCompatActivity() {

    private var isNewOperation: Boolean = true
    private var isRadianMode: Boolean = false
    private var memoryValue: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_advanced_calc)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)

        if (savedInstanceState != null) {
            tvDisplay.text = savedInstanceState.getString("DISPLAY_TEXT")
            isNewOperation = savedInstanceState.getBoolean("IS_NEW_OP")
            isRadianMode = savedInstanceState.getBoolean("IS_RADIAN")
            memoryValue = savedInstanceState.getDouble("MEMORY_VALUE")

            val btnRad = findViewById<Button>(R.id.btnRad)
            btnRad.text = if (isRadianMode) "Deg" else "Rad"
        }


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
                    if (number == ",") tvDisplay.text = "0,"
                    else tvDisplay.text = number
                    isNewOperation = false
                } else {
                    if (number == ",") {
                        if (canAddComa(tvDisplay.text.toString())) {
                            tvDisplay.append(",")
                        }
                    } else {
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
                if (displayText.isNotEmpty() && !isLastCharOperator(displayText)) {
                    tvDisplay.append(button.text.toString())
                    isNewOperation = false
                }
            }
        }

        findViewById<Button>(R.id.btnAC).setOnClickListener {
            tvDisplay.text = "0"
            isNewOperation = true
        }

        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            val text = tvDisplay.text.toString()
            if (text.isNotEmpty() && text.last().isDigit()) {
                tvDisplay.append("%")
                isNewOperation = false
            }
        }

        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener {
            val text = tvDisplay.text.toString()
            if (text.isNotEmpty() && !text.contains(Regex("[+×÷-]"))) {
                val currentValue = text.replace(",", ".").toDouble()
                tvDisplay.text = formatResult(-currentValue)
            }
        }

        findViewById<Button>(R.id.btnDel).setOnClickListener {
            val text = tvDisplay.text.toString()
            if (text.length > 1) tvDisplay.text = text.dropLast(1)
            else {
                tvDisplay.text = "0"; isNewOperation = true
            }
        }

        findViewById<Button>(R.id.btnMc).setOnClickListener { memoryValue = 0.0 }

        findViewById<Button>(R.id.btnMplus).setOnClickListener {
            try {
                memoryValue += tvDisplay.text.toString().replace(",", ".").toDouble()
            } catch (e: Exception) {
            }
            isNewOperation = true
        }

        findViewById<Button>(R.id.btnMminus).setOnClickListener {
            try {
                memoryValue -= tvDisplay.text.toString().replace(",", ".").toDouble()
            } catch (e: Exception) {
            }
            isNewOperation = true
        }

        findViewById<Button>(R.id.btnMr).setOnClickListener {
            val memStr = formatResult(memoryValue)
            if (isNewOperation || tvDisplay.text.toString() == "0") tvDisplay.text = memStr
            else tvDisplay.append(memStr)
            isNewOperation = false
        }

        findViewById<Button>(R.id.btnOpenParen).setOnClickListener { appendText(tvDisplay, "(") }
        findViewById<Button>(R.id.btnCloseParen).setOnClickListener { appendText(tvDisplay, ")") }

        val btnRad = findViewById<Button>(R.id.btnRad)
        btnRad.setOnClickListener {
            isRadianMode = !isRadianMode
            btnRad.text = if (isRadianMode) "Deg" else "Rad"
        }

        findViewById<Button>(R.id.btnSin).setOnClickListener { appendText(tvDisplay, "sin(") }
        findViewById<Button>(R.id.btnCos).setOnClickListener { appendText(tvDisplay, "cos(") }
        findViewById<Button>(R.id.btnTan).setOnClickListener { appendText(tvDisplay, "tan(") }
        findViewById<Button>(R.id.btnSinh).setOnClickListener { appendText(tvDisplay, "sinh(") }
        findViewById<Button>(R.id.btnCosh).setOnClickListener { appendText(tvDisplay, "cosh(") }
        findViewById<Button>(R.id.btnTanh).setOnClickListener { appendText(tvDisplay, "tanh(") }

        findViewById<Button>(R.id.btn1x).setOnClickListener { appendText(tvDisplay, "1/") }
        findViewById<Button>(R.id.btnSqrt2).setOnClickListener { appendText(tvDisplay, "sqrt(") }
        findViewById<Button>(R.id.btnSqrt3).setOnClickListener { appendText(tvDisplay, "cbrt(") }
        findViewById<Button>(R.id.btnSqrtY).setOnClickListener { appendText(tvDisplay, "^(1/") }
        findViewById<Button>(R.id.btnLn).setOnClickListener { appendText(tvDisplay, "ln(") }
        findViewById<Button>(R.id.btnLog10).setOnClickListener { appendText(tvDisplay, "log(") }

        findViewById<Button>(R.id.btnX2).setOnClickListener { appendText(tvDisplay, "^2") }
        findViewById<Button>(R.id.btnX3).setOnClickListener { appendText(tvDisplay, "^3") }
        findViewById<Button>(R.id.btnXy).setOnClickListener { appendText(tvDisplay, "^") }
        findViewById<Button>(R.id.btnEx).setOnClickListener { appendText(tvDisplay, "e^") }
        findViewById<Button>(R.id.btn10x).setOnClickListener { appendText(tvDisplay, "10^") }
        findViewById<Button>(R.id.btnFact).setOnClickListener { appendText(tvDisplay, "!") }
        findViewById<Button>(R.id.btnPi).setOnClickListener { appendText(tvDisplay, "π") }
        findViewById<Button>(R.id.btnE).setOnClickListener { appendText(tvDisplay, "e") }
        findViewById<Button>(R.id.btnEE).setOnClickListener { appendText(tvDisplay, "E") }
        findViewById<Button>(R.id.btnRand).setOnClickListener {
            appendText(
                tvDisplay,
                Math.random().toString().take(6)
            )
        }

        findViewById<Button>(R.id.btn2nd).setOnClickListener {}

        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            val expression = tvDisplay.text.toString()
            try {
                val formattedExpression = expression
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace(",", ".")
                    .replace("π", Math.PI.toString())
                    .replace("e", Math.E.toString())
                    .replace("%", "/100")

                val result = eval(formattedExpression)
                tvDisplay.text = formatResult(result)
                isNewOperation = true
            } catch (e: Exception) {
                tvDisplay.text = "Error"
                isNewOperation = true
            }
        }
    }

    private fun appendText(tv: TextView, textToAppend: String) {
        if (isNewOperation || tv.text.toString() == "0") {
            tv.text = textToAppend
            isNewOperation = false
        } else {
            tv.append(textToAppend)
        }
    }

    private fun isLastCharOperator(text: String): Boolean {
        if (text.isEmpty()) return false
        val lastChar = text.last()
        return lastChar == '+' || lastChar == '-'
                || lastChar == '×' || lastChar == '÷'
                || lastChar == ',' || lastChar == '^'
    }

    private fun canAddComa(text: String): Boolean {
        val parts = text.split("+", "-", "×", "÷", "(", ")", "^")
        return !parts.last().contains(",")
    }

    private fun formatResult(result: Double): String {
        return if (result.isNaN() || result.isInfinite()) "Error"
        else if (result % 1 == 0.0) result.toLong().toString()
        else {
            val rounded = round(result * 100000000) / 100000000
            rounded.toString().replace(".", ",")
        }
    }

    private fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = this.pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, this.pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()

                    x = when (func) {
                        "sqrt" -> sqrt(x)
                        "cbrt" -> Math.cbrt(x)
                        "sin" -> if (isRadianMode) sin(x) else sin(Math.toRadians(x))
                        "cos" -> if (isRadianMode) cos(x) else cos(Math.toRadians(x))
                        "tan" -> if (isRadianMode) tan(x) else tan(Math.toRadians(x))
                        "sinh" -> sinh(x)
                        "cosh" -> cosh(x)
                        "tanh" -> tanh(x)
                        "ln" -> ln(x)
                        "log" -> log10(x)
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = x.pow(parseFactor())
                if (eat('E'.code)) x *= 10.0.pow(parseFactor())

                if (eat('!'.code)) {
                    var fact = 1.0
                    for (i in 1..x.toInt()) fact *= i
                    x = fact
                }

                return x
            }
        }.parse()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)

        outState.putString("DISPLAY_TEXT", tvDisplay.text.toString())
        outState.putBoolean("IS_NEW_OP", isNewOperation)
        outState.putBoolean("IS_RADIAN", isRadianMode)
        outState.putDouble("MEMORY_VALUE", memoryValue)
    }

}