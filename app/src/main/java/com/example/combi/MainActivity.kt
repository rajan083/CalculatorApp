package com.example.combi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var display: EditText
    private var input = ""
    private var lastNumber = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.text_id)

        val buttons = listOf(
            R.id.bt0, R.id.bt1,R.id.bt2, R.id.bt3,R.id.bt4, R.id.bt5, R.id.bt6, R.id.bt7,
            R.id.bt8, R.id.bt9, R.id.bt_plus, R.id.bt_minus, R.id.bt_multiply, R.id.bt_divide,
            R.id.bt_fullstop, R.id.bt_comma, R.id.bt_percentage, R.id.bt_equal, R.id.bt_back
        )

        for (id in buttons){
            findViewById<MaterialButton>(id).setOnClickListener(){onButtonClick(it as Button)}
        }
    }
    private fun onButtonClick(button: Button){
        when (button.id){
            R.id.bt_back -> {
                input = ""
                display.setText("")
            }
            R.id.bt_equal -> calculateResult()
            else -> appendToInput(button.text.toString())
        }
    }
    private fun appendToInput(value: String){
        input += value
        display.setText(input)
    }
    private fun calculateResult(){
        try {
            val result = eval(input)
            display.setText(result.toString())
            input = result.toString()
        }catch (e: Exception){
            display.setText("Error")
            input = ""
        }
    }

    private fun eval(expression: String): Double{
        return object : Any(){
            fun parse(): Double{
                val tokens = expression.replace(" ", "").toCharArray()
                var index= 0

                fun nextToken(): Char?= if (index< tokens.size) tokens[index++] else null
                fun peekToken(): Char?= if (index< tokens.size) tokens[index] else null

                fun factor(): Double{
                    val token = nextToken()
                    return when(token){
                        in '0'..'9' -> token.toString().toDouble()
                        '(' ->{
                            val result = parse()
                            nextToken()
                            result
                        }
                        else -> throw IllegalArgumentException("Invalid token : $token")
                    }
                }
                fun term(): Double{
                    var result = factor()
                    while (peekToken() in listOf('*','/')){
                        val op = nextToken()
                        result = when(op){
                            '*' -> result*factor()
                            '/' -> result/factor()
                            else -> result
                        }
                    }
                    return result
                }

                fun parse(): Double{
                    var result = term()
                    while (peekToken() in listOf('+', '-')){
                        val op = nextToken()
                        result = when(op){
                            '+' -> result + term()
                            '-' -> result - term()
                            else -> result
                        }
                    }
                    return result
                }
                return parse()
            }
        }.parse()
    }
}