package com.example.bubblesort

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import kotlin.system.measureTimeMillis
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var iterationView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userInput = findViewById<EditText>(R.id.inputTextBox)
        val sortButton = findViewById<Button>(R.id.sortButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val quitButton = findViewById<Button>(R.id.quitButton)
        var resultView = findViewById<TextView>(R.id.resultView)
        val statsView = findViewById<TextView>(R.id.statsView)
        var errorView = findViewById<TextView>(R.id.errorTextBox)
        iterationView = findViewById(R.id.iterationTextBox)
        val exitMessage = findViewById<TextView>(R.id.exitMessageBox)

        sortButton.setOnClickListener {
            Toast.makeText(this, "Bubble Sort starting.", Toast.LENGTH_SHORT).show()
            val rawInput = userInput.text.toString()
            val numsList = parseInts(rawInput)

            if (numsList == null || numsList.isEmpty()) {
                errorView.text = "Invalid input. Use comma or space separated integers."
                errorView.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            if (numsList.size < 3 || numsList.size > 8) {
                errorView.text = "Please enter between 3–8 integers."
                errorView.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            if (numsList.any { it < 0 || it > 9 }) {
                errorView.text = "All numbers must be between 0 and 9."
                errorView.visibility = TextView.VISIBLE
                resultView.text = ""
                statsView.text = ""
                return@setOnClickListener
            }

            val numsArray = numsList.toIntArray()
            resultView.text = "Input array: ${numsArray.joinToString(", ")}"


            /*MAYBE DELETE THIS AND INSTEAD DISPLAY THE ARRAYS FOR EACH ITERATION*/
            var comparisons = 0
            var swaps = 0
            val arr = numsList.toMutableList()

            /*MAYBE DELETE THIS SINCE TIME ISNT NECESSARY*/
            val elapsedMs = measureTimeMillis {
                bubbleSort(arr, onCompare = { comparisons++ }, onSwap = { swaps++ })
            }

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
            resultView.text = ""
            statsView.text = ""
            errorView.text = ""
            iterationView.text = ""
        }

        quitButton.setOnClickListener {
            exitMessage.text = "Thank you for bubbling with us, goodbye now!"
            exitMessage.visibility = TextView.VISIBLE
            lifecycleScope.launch {
                delay(2000)
                finish()
            }
        }
    }

    /** Parses "1, 2  -3" -> listOf(1,2,-3); null on error */
    private fun parseInts(raw: String): List<Int>? = runCatching {
        raw.split(',', ' ', '，', '；')
            .mapNotNull { it.trim().takeIf { s -> s.isNotEmpty() } }
            .map { it.toInt() }
    }.getOrNull()

    /** Bubble sort with hooks for comparisons and swaps */
    private fun bubbleSort(
        arr: MutableList<Int>,
        onCompare: () -> Unit = {},
        onSwap: () -> Unit = {}
    ) {
        val logBuilder = StringBuilder()
        val arrLength = arr.size
        for (i in 0 until arrLength - 1) {
            var swapped = false
            for (j in 0 until arrLength - i - 1) {
                onCompare()
                if (arr[j] > arr[j + 1]) {
                    val tmp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = tmp
                    onSwap()
                    swapped = true
                }
            }
            logBuilder.append("After iteration ${i + 1}: \n\t\t${arr.joinToString(", ")}\n\n")

            if (!swapped) break
        }
        iterationView.text = logBuilder.toString()
        iterationView.visibility = TextView.VISIBLE

        Toast.makeText(this, "Bubble Sort complete.", Toast.LENGTH_SHORT).show()

    }
}