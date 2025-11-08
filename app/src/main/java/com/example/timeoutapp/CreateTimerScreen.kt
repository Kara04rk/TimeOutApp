package com.example.timeoutapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CreateTimerScreen() {
    var minutes by remember { mutableStateOf("") }
    var isTimerRunning by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableStateOf(0) }


    LaunchedEffect(isTimerRunning, secondsRemaining) {
        if (isTimerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        } else if (secondsRemaining == 0 && isTimerRunning) {
            isTimerRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Timer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp, bottom = 48.dp)
        )

        if (!isTimerRunning) {
            //when timer not running
            OutlinedTextField(
                value = minutes,
                onValueChange = { minutes = it },
                label = { Text("Minutes") },
                placeholder = { Text("Enter minutes") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val mins = minutes.toIntOrNull()
                if (mins != null && mins > 0) {
                    secondsRemaining = mins * 60
                    isTimerRunning = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Start Timer",
                fontSize = 18.sp
            )
        }
    }
        else {
            // Show when timer running
            val minutesLeft = secondsRemaining / 60
            val secondsLeft = secondsRemaining % 60

            Text(
                text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isTimerRunning = false
                    secondsRemaining = 0
                    minutes = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Cancel Timer",
                    fontSize = 18.sp
                )
            }
        }
    }
}
