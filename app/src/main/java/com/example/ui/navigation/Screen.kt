package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calendar : Screen("reminders", "Calendar", Icons.Default.CalendarMonth)
    object Scan : Screen("scan", "Scan", Icons.Default.CameraAlt)
    object Medicines : Screen("medicines", "Medicines", Icons.Default.Medication)
    object Settings : Screen("settings", "Profile", Icons.Default.Person)

    // Secondary destinations
    object HealthDashboard : Screen("health_dashboard", "Health Insights", Icons.AutoMirrored.Filled.TrendingUp)
    object AiAssistant : Screen("ai_assistant", "AI Assistant", Icons.Default.Psychology)
    object MedicineDetail : Screen("medicine_detail/{medicineId}", "Medicine Detail") {
        fun createRoute(id: Long) = "medicine_detail/$id"
    }

    object CreateReminder : Screen("create_reminder/{medicineId}/{medicineName}", "Set Reminder") {
        fun createRoute(id: Long, name: String) = "create_reminder/$id/${java.net.URLEncoder.encode(name, "UTF-8")}"
    }

    object ScanHistory : Screen("scan_history", "Scan History")
}

val primaryBottomNavItems = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.Scan,
    Screen.Medicines,
    Screen.Settings
)

