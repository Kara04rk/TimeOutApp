package com.example.timeoutapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyTimersScreen(onBack: () -> Unit) {
    //placeholder
    val savedTimers = listOf(
        "15 minutes",
        "30 minutes",
        "1 hour"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "My Timers",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp, bottom = 24.dp)
        )
        @Suppress("ConstantConditionIf")
        if (savedTimers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved timers yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // show list
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                savedTimers.forEach { timer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO:use this timer
                            }
                    ) {
                        Text(
                            text = timer,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}