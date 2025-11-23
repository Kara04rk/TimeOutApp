package com.example.timeoutapp

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CreateTimerScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var minutes by remember { mutableStateOf("") }
    var isTimerRunning by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableStateOf(0) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    fun isAccessibilityEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return services?.contains("${context.packageName}/${LockdownService::class.java.name}") == true
        }
        return false
    }

    fun startTimerWithLockdown(mins: Int) {
        if (!isAccessibilityEnabled()) {
            showAccessibilityDialog = true
        } else {
            secondsRemaining = mins * 60
            isTimerRunning = true
            LockdownService.setLockdownActive(context, true)
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        secondsRemaining = 0
        minutes = ""
        LockdownService.setLockdownActive(context, false)
    }

    LaunchedEffect(isTimerRunning, secondsRemaining) {
        if (isTimerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        } else if (secondsRemaining == 0 && isTimerRunning) {
            stopTimer()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Timer",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (!isTimerRunning) {
            OutlinedTextField(
                value = minutes,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        minutes = it
                    }
                },
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
                        startTimerWithLockdown(mins)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = minutes.isNotEmpty()
            ) {
                Text(
                    text = "Start Timer",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "App Lock Mode",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When the timer starts, all apps except Phone and Messages will be blocked until the timer ends.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        } else {
            val minutesLeft = secondsRemaining / 60
            val secondsLeft = secondsRemaining % 60

            Text(
                text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🔒 Apps are locked",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { stopTimer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Cancel Timer & Unlock",
                    fontSize = 18.sp
                )
            }
        }
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("Enable Accessibility Service") },
            text = {
                Text("To lock apps during the timer, TimeOut needs accessibility permission. Please enable it in Settings.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        showAccessibilityDialog = false
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAccessibilityDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}