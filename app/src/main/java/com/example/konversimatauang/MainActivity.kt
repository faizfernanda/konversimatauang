package com.example.konversimatauang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.konversimatauang.ui.theme.KonversiMataUangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KonversiMataUangTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CurrencyConverter(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CurrencyConverter(modifier: Modifier = Modifier) {
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("IDR") }
    var toCurrency by remember { mutableStateOf("USD") }
    var result by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val currencies = listOf("IDR", "USD", "EUR", "JPY", "GBP", "AUD", "SGD")


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "💱 Konversi Mata Uang",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Card untuk input dan dropdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = amount,
                    onValueChange = {
                        val cleanInput = it.replace(".", "").replace(",", "")
                        amount = if (cleanInput.isNotBlank()) {
                            cleanInput.toLongOrNull()?.let { num ->
                                String.format("%,d", num).replace(',', '.')
                            } ?: cleanInput
                        } else {
                            ""
                        }
                    },
                    label = { Text("Masukkan jumlah") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                DropdownMenuComponent(
                    label = "Dari",
                    options = currencies,
                    selectedOption = fromCurrency,
                    onOptionSelected = { fromCurrency = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DropdownMenuComponent(
                    label = "Ke",
                    options = currencies,
                    selectedOption = toCurrency,
                    onOptionSelected = { toCurrency = it }
                )
            }
        }

        // Hitung otomatis
        LaunchedEffect(amount, fromCurrency, toCurrency) {
            if (amount.isNotBlank()) {
                errorMessage = ""
                val rate = getConversionRate(fromCurrency, toCurrency)
                val cleanAmount = amount.replace(".", "")
                val amountDouble = cleanAmount.toDoubleOrNull() ?: 0.0
                result = String.format("%.2f", amountDouble * rate)
            } else {
                result = ""
            }
        }

        // Hasil
        if (result.isNotEmpty() && errorMessage.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Dari: $amount $fromCurrency")
                    Text(text = "Hasil: $result $toCurrency", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
fun DropdownMenuComponent(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }, text = { Text(option) })
                }
            }
        }
    }
}

fun getConversionRate(from: String, to: String): Double {
    return when (from to to) {
        "IDR" to "USD" -> 0.000065
        "USD" to "IDR" -> 15400.0
        "IDR" to "EUR" -> 0.000057
        "EUR" to "IDR" -> 17500.0
        "USD" to "EUR" -> 0.92
        "EUR" to "USD" -> 1.09
        "IDR" to "JPY" -> 0.0094
        "JPY" to "IDR" -> 106.0
        "USD" to "JPY" -> 153.0
        "JPY" to "USD" -> 0.0065
        "EUR" to "JPY" -> 165.0
        "JPY" to "EUR" -> 0.0060

        // GBP
        "USD" to "GBP" -> 0.79
        "GBP" to "USD" -> 1.27
        "IDR" to "GBP" -> 0.000051
        "GBP" to "IDR" -> 19500.0

        // AUD
        "USD" to "AUD" -> 1.51
        "AUD" to "USD" -> 0.66
        "IDR" to "AUD" -> 0.000096
        "AUD" to "IDR" -> 10400.0

        // SGD
        "USD" to "SGD" -> 1.35
        "SGD" to "USD" -> 0.74
        "IDR" to "SGD" -> 0.000069
        "SGD" to "IDR" -> 14500.0

        else -> 1.0
    }
}


@Preview(showBackground = true)
@Composable
fun CurrencyConverterPreview() {
    KonversiMataUangTheme {
        CurrencyConverter()
    }
}