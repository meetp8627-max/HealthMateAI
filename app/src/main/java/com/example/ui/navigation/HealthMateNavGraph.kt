package com.example.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.PolishBluePrimary
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.db.AppDatabase
import com.example.data.repository.HealthMateRepository
import com.example.ui.components.FloatingBottomBar
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.CreateReminderScreen
import com.example.ui.screens.HealthDashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicineDetailScreen
import com.example.ui.screens.MyMedicinesScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.ScanHistoryScreen
import com.example.ui.screens.ScanMedicineScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.HealthMateViewModel
import com.example.ui.viewmodel.HealthMateViewModelFactory

@Composable
fun HealthMateMainApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember {
        HealthMateRepository(
            userDao = database.userDao(),
            medicineDao = database.medicineDao(),
            reminderDao = database.reminderDao(),
            reminderLogDao = database.reminderLogDao(),
            scanHistoryDao = database.scanHistoryDao(),
            knowledgeDao = database.medicineKnowledgeDao()
        )
    }

    val viewModel: HealthMateViewModel = viewModel(
        factory = HealthMateViewModelFactory(repository)
    )

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PolishBluePrimary)
        }
    } else if (currentUser?.displayName.isNullOrBlank()) {
        OnboardingScreen(
            onNameSubmitted = { name ->
                viewModel.saveUserName(name)
            }
        )
    } else {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = primaryBottomNavItems.any { it.route == currentRoute } || currentRoute == Screen.HealthDashboard.route

        val bottomTabRoutes = remember {
            listOf(
                Screen.Home.route,
                Screen.Calendar.route,
                Screen.Scan.route,
                Screen.Medicines.route,
                Screen.Settings.route
            )
        }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    FloatingBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { screen ->
                            if (screen.route == Screen.Home.route) {
                                val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                                if (!popped) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(navController.graph.findStartDestination().id)
                                        launchSingleTop = true
                                    }
                                }
                            } else if (currentRoute == screen.route) {
                                // Re-selected currently active tab: pop sub-destinations if any to ensure immediate root view
                                navController.popBackStack(screen.route, inclusive = false)
                            } else {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                enterTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val isTabSwitch = initialRoute in bottomTabRoutes && targetRoute in bottomTabRoutes

                    if (isTabSwitch) {
                        val initialIndex = bottomTabRoutes.indexOf(initialRoute)
                        val targetIndex = bottomTabRoutes.indexOf(targetRoute)
                        val slideDirection = if (targetIndex >= initialIndex) 1 else -1
                        slideInHorizontally(
                            initialOffsetX = { (it * 0.12f * slideDirection).toInt() },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                        ) + fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.97f, animationSpec = tween(220))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.88f)
                        ) + fadeIn(animationSpec = tween(250))
                    }
                },
                exitTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val isTabSwitch = initialRoute in bottomTabRoutes && targetRoute in bottomTabRoutes

                    if (isTabSwitch) {
                        val initialIndex = bottomTabRoutes.indexOf(initialRoute)
                        val targetIndex = bottomTabRoutes.indexOf(targetRoute)
                        val slideDirection = if (targetIndex >= initialIndex) 1 else -1
                        slideOutHorizontally(
                            targetOffsetX = { (-it * 0.12f * slideDirection).toInt() },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                        ) + fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.97f, animationSpec = tween(180))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { (-it * 0.28f).toInt() },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.88f)
                        ) + fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.96f, animationSpec = tween(200))
                    }
                },
                popEnterTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val isTabSwitch = initialRoute in bottomTabRoutes && targetRoute in bottomTabRoutes

                    if (isTabSwitch) {
                        fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.97f, animationSpec = tween(220))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { (-it * 0.28f).toInt() },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.88f)
                        ) + fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.96f, animationSpec = tween(250))
                    }
                },
                popExitTransition = {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val isTabSwitch = initialRoute in bottomTabRoutes && targetRoute in bottomTabRoutes

                    if (isTabSwitch) {
                        fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.97f, animationSpec = tween(180))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.88f)
                        ) + fadeOut(animationSpec = tween(200))
                    }
                }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                        onNavigateToMedicines = { navController.navigate(Screen.Medicines.route) },
                        onNavigateToReminders = { navController.navigate(Screen.Calendar.route) },
                        onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                        onNavigateToHealthInsights = { navController.navigate(Screen.HealthDashboard.route) },
                        onMedicineClick = { id -> navController.navigate(Screen.MedicineDetail.createRoute(id)) }
                    )
                }

                composable(Screen.Medicines.route) {
                    MyMedicinesScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { id -> navController.navigate(Screen.MedicineDetail.createRoute(id)) },
                        onNavigateToScan = { navController.navigate(Screen.Scan.route) }
                    )
                }

                composable(Screen.Scan.route) {
                    ScanMedicineScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onContinueToReminder = { id, name ->
                            navController.navigate(Screen.CreateReminder.createRoute(id, name))
                        },
                        onNavigateToMedicineDetail = { id ->
                            navController.navigate(Screen.MedicineDetail.createRoute(id))
                        }
                    )
                }

                composable(Screen.Calendar.route) {
                    RemindersScreen(
                        viewModel = viewModel,
                        onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                        onNavigateToScheduler = { id, name ->
                            navController.navigate(Screen.CreateReminder.createRoute(id, name))
                        }
                    )
                }

                composable(Screen.HealthDashboard.route) {
                    HealthDashboardScreen(viewModel = viewModel)
                }

                composable(Screen.AiAssistant.route) {
                    AiAssistantScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToScanHistory = { navController.navigate(Screen.ScanHistory.route) },
                        onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) }
                    )
                }

                composable(Screen.ScanHistory.route) {
                    ScanHistoryScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.MedicineDetail.route,
                    arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: 0L
                    MedicineDetailScreen(
                        medicineId = medicineId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onCreateReminder = { id, name ->
                            navController.navigate(Screen.CreateReminder.createRoute(id, name))
                        },
                        onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                        onNavigateToScan = { navController.navigate(Screen.Scan.route) }
                    )
                }

                composable(
                    route = Screen.CreateReminder.route,
                    arguments = listOf(
                        navArgument("medicineId") { type = NavType.LongType },
                        navArgument("medicineName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: 0L
                    val medicineName = backStackEntry.arguments?.getString("medicineName") ?: ""
                    CreateReminderScreen(
                        medicineId = medicineId,
                        medicineName = medicineName,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}


