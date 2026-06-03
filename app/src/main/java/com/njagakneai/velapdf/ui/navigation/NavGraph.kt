package com.njagakneai.velapdf.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njagakneai.velapdf.ui.screen.DashboardScreen
import com.njagakneai.velapdf.ui.screen.MergePdfScreen
import com.njagakneai.velapdf.ui.screen.PermissionsScreen
import com.njagakneai.velapdf.ui.screen.SplashScreen
import com.njagakneai.velapdf.ui.screen.HistoryScreen
import com.njagakneai.velapdf.ui.screen.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionsScreen(
                onPermissionsGranted = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToImageToPdf = {
                    navController.navigate(Screen.ImageToPdf.route)
                },
                onNavigateToMergePdf = {
                    navController.navigate(Screen.MergePdf.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.ImageToPdf.route) {
            com.njagakneai.velapdf.ui.screen.ImageToPdfScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConversionSuccess = { encodedUri ->
                    navController.navigate(Screen.Success.createRoute(encodedUri)) {
                        popUpTo(Screen.ImageToPdf.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MergePdf.route) {
            MergePdfScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMergeSuccess = { encodedUri ->
                    navController.navigate(Screen.Success.createRoute(encodedUri)) {
                        popUpTo(Screen.MergePdf.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                historyList = emptyList(), // In a real app this would come from ViewModel/Database
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Converter.route) {
            // In a real implementation, you'd pass the list of SelectedImages via a shared ViewModel
            com.njagakneai.velapdf.ui.screen.ConverterScreen(
                images = emptyList(), // Placeholder
                outputFileName = "VelaPDF_${System.currentTimeMillis()}",
                onSuccess = { uriString ->
                    navController.navigate(Screen.Success.createRoute(Uri.encode(uriString))) {
                        popUpTo(Screen.Converter.route) { inclusive = true }
                    }
                },
                onError = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Success.route,
            arguments = listOf(androidx.navigation.navArgument("uri") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri") ?: ""
            com.njagakneai.velapdf.ui.screen.SuccessScreen(
                pdfUriString = uri,
                onBackToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Permissions : Screen("permissions")
    object Dashboard : Screen("dashboard")
    object ImageToPdf : Screen("image_to_pdf")
    object MergePdf : Screen("merge_pdf")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Converter : Screen("converter")
    object Success : Screen("success/{uri}") {
        fun createRoute(uri: String) = "success/$uri"
    }
}
