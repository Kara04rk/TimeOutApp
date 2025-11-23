package com.example.timeoutapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class LockdownService : AccessibilityService() {

    companion object {
        private const val PREFS_NAME = "TimeOutPrefs"
        private const val KEY_LOCKDOWN_ACTIVE = "lockdown_active"

        // Allowed apps during lockdown
        private val ALLOWED_PACKAGES = setOf(
            "com.example.timeoutapp", // the app
            "com.android.dialer", // Phone dialer
            "com.google.android.dialer", // Google Phone
            "com.samsung.android.dialer", // Samsung Phone
            "com.android.contacts", // Contacts
            "com.android.mms", // Messages
            "com.google.android.apps.messaging", // Google Messages
            "com.samsung.android.messaging", // Samsung Messages
            "com.android.systemui", // System UI
            "com.android.settings", // Settings [FOR NOW]
            "com.android.launcher3", // Default launcher
            "com.google.android.apps.nexuslauncher", // Pixel Launcher
            "com.samsung.android.app.launcher", // Samsung Launcher
            "com.android.incallui" // In-call UI
        )

        fun setLockdownActive(context: Context, active: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_LOCKDOWN_ACTIVE, active).apply()
        }

        fun isLockdownActive(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_LOCKDOWN_ACTIVE, false)
        }
    }

    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val packageName = it.packageName?.toString() ?: return

                if (isLockdownActive(this) && !isPackageAllowed(packageName)) {
                    blockApp(packageName)
                }
            }
        }
    }

    private fun isPackageAllowed(packageName: String): Boolean {
        return ALLOWED_PACKAGES.contains(packageName) ||
                packageName.startsWith("com.android.systemui") ||
                packageName.startsWith("com.android.launcher")
    }

    private fun blockApp(packageName: String) {
        Toast.makeText(this, "App blocked during TimeOut session", Toast.LENGTH_SHORT).show()

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Called when interrupted
    }
}