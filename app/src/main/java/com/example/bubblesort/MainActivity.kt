package com.example.bubblesort

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import kotlin.system.measureTimeMillis
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var resultView: TextView
    private lateinit var statsView: TextView
    private lateinit var iterationView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userInput = findViewById<EditText>(R.id.inputTextBox)
        val sortButton = findViewById<Button>(R.id.sortButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val quitButton = findViewById<Button>(R.id.quitButton)
        resultView = findViewById<TextView>(R.id.resultView)
        statsView = findViewById<TextView>(R.id.statsView)
        var errorView = findViewById<TextView>(R.id.errorTextBox)
        iterationView = findViewById(R.id.iterationTextBox)
        val exitMessage = findViewById<TextView>(R.id.exitMessageBox)

        sortButton.setOnClickListener {
            val rawInput = userInput.text.toString()
            val numsList = parseInts(rawInput)

            if (numsList == null) {
                errorView.text = "Invalid input. Please use comma or space separated integers."
                errorView.visibility = TextView.VISIBLE
                clearOutputViews()
                return@setOnClickListener
            } else if ( numsList.isEmpty() ) {
                errorView.text = "No input. Please use comma or space separated integers."
                errorView.visibility = TextView.VISIBLE
                clearOutputViews()
                return@setOnClickListener
            } else if (numsList.size < 3 || numsList.size > 8) {
                errorView.text = "Please enter between 3–8 integers."
                errorView.visibility = TextView.VISIBLE
                clearOutputViews()
                return@setOnClickListener
            } else if (numsList.any { it < 0 || it > 9 }) {
                errorView.text = "All numbers must be between 0 and 9."
                errorView.visibility = TextView.VISIBLE
                clearOutputViews()
                return@setOnClickListener
            }

            Toast.makeText(this, "Bubble Sort starting.", Toast.LENGTH_SHORT).show()
            val numsArray = numsList.toIntArray()
            resultView.text = "Input array: ${numsArray.joinToString(", ")}"


            var comparisons = 0
            var swaps = 0
            val arr = numsList.toMutableList()

            val elapsedMs = measureTimeMillis {
                bubbleSort(arr, onCompare = { comparisons++ }, onSwap = { swaps++ })
            }
            errorView.text = ""
            resultView.text = "Sorted: ${arr.joinToString(", ")}"
            statsView.text = """
                Comparisons: $comparisons
                Swaps: $swaps
                Time: ${elapsedMs}ms
            """.trimIndent()
        }

        resetButton.setOnClickListener {
            Toast.makeText(this, "Bubble Sort reset.", Toast.LENGTH_SHORT).show()
            userInput.setText("")
            clearOutputViews()
            errorView.text = ""
        }

        quitButton.setOnClickListener {
            clearOutputViews()
            exitMessage.text = "\tThank you for bubbling with us,\n\t\t\t\t\t\t\t\t\t\t Goodbye Now!"
            exitMessage.visibility = TextView.VISIBLE
            lifecycleScope.launch {
                delay(2000)
                finish()
            }
        }
    }
    private fun parseInts(raw: String): List<Int>? = runCatching {
        raw.split(',', ' ', '，', '；')
            .mapNotNull { it.trim().takeIf { s -> s.isNotEmpty() } }
            .map { it.toInt() }
    }.getOrNull()

    private fun clearOutputViews() {
        resultView.text = ""
        statsView.text = ""
        iterationView.text = ""
    }
    private fun bubbleSort(
        arr: MutableList<Int>,
        onCompare: () -> Unit = {},
        onSwap: () -> Unit = {}
    ) {
        val logBuilder = StringBuilder()
        val arrLength = arr.size
        for (i in 0 until arrLength - 1) {
            var swapped = false
            for (j in arrLength - 1 downTo i + 1) {
                onCompare()
                if (arr[j] < arr[j - 1]) {
                    val tmp = arr[j]
                    arr[j] = arr[j - 1]
                    arr[j - 1] = tmp
                    onSwap()
                    swapped = true
                }
            }
            logBuilder.append("After iteration ${i + 1}: \n\t\t${arr.joinToString("   ")}\n\n")

            if (!swapped) break
        }
        iterationView.text = logBuilder.toString()
        iterationView.visibility = TextView.VISIBLE

        Toast.makeText(this, "Bubble Sort complete.", Toast.LENGTH_SHORT).show()

    }
}