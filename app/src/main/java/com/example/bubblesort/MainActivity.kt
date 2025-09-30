package com.example.bubblesort

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bubblesort.ui.theme.BubbleSortTheme
import kotlin.system.measureTimeMillis

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BubbleSortTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BubbleSortScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BubbleSortScreen(modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("5, 3, 8, 2, 1, 4") }
    var error by remember { mutableStateOf<String?>(null) }

    var result by remember { mutableStateOf<List<Int>>(emptyList()) }
    var comparisons by remember { mutableStateOf(0) }
    var swaps by remember { mutableStateOf(0) }
    var elapsedMs by remember { mutableStateOf(0L) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Bubble Sort Demo", style = MaterialTheme.typography.headlineSmall)

        ，OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter integers (comma/space separated)") },
            singleLine = true,
            isError = error != null,
            supportingText = {
                Text(
                    text = error ?: "Example: 5, 3, 8, 2, 1, 4",
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                error = null
                val nums = parseInts(input)
                if (nums == null || nums.isEmpty()) {
                    error = "Invalid input. Only integers separated by comma or space."
                    return@Button
                }
                var cmp = 0
                var swp = 0
                val arr = nums.toMutableList()
                val t = measureTimeMillis {
                    bubbleSort(
                        arr,
                        onCompare = { cmp++ },
                        onSwap = { swp++ }
                    )
                }
                result = arr
                comparisons = cmp
                swaps = swp
                elapsedMs = t
            }) {
                Text("Sort")
            }
            OutlinedButton(onClick = {
                input = ""
                error = null
                result = emptyList()
                comparisons = 0
                swaps = 0
                elapsedMs = 0
            }) {
                Text("Reset")
            }
        }

        if (result.isNotEmpty()) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sorted Result", style = MaterialTheme.typography.titleMedium)
                    Text(result.joinToString(", "))
                }
            }
        }

        if (elapsedMs > 0 || comparisons > 0 || swaps > 0) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Statistics", style = MaterialTheme.typography.titleMedium)
                    Text("Comparisons: $comparisons")
                    Text("Swaps: $swaps")
                    Text("Elapsed: ${elapsedMs} ms")
                }
            }
        }
    }
}

/** parse "1, 2  -3" -> listOf(1,2,-3); null on error */
private fun parseInts(raw: String): List<Int>? = runCatching {
    raw.split(',', ' ', '，', '；')
        .mapNotNull { it.trim().takeIf { s -> s.isNotEmpty() } }
        .map { it.toInt() }
}.getOrNull()

/** standard bubble sort with hooks to count compares/swaps */
private fun bubbleSort(
    arr: MutableList<Int>,
    onCompare: () -> Unit = {},
    onSwap: () -> Unit = {}
) {
    val n = arr.size
    if (n < 2) return
    for (i in 0 until n - 1) {
        var swapped = false
        for (j in 0 until n - i - 1) {
            onCompare()
            if (arr[j] > arr[j + 1]) {
                val tmp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = tmp
                onSwap()
                swapped = true
            }
        }
        if (!swapped) break
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleSortPreview() {
    BubbleSortTheme { BubbleSortScreen(Modifier.padding(16.dp)) }
}
