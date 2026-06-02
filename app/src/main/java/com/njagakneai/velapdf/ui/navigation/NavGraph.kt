package com.njagakneai.velapdf.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njagakneai.velapdf.ui.screen.DashboardScreen
import com.njagakneai.velapdf.ui.screen.PermissionsScreen
import com.njagakneai.velapdf.ui.screen.SplashScreen

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
    object Converter : Screen("converter")
    object Success : Screen("success/{uri}") {
        fun createRoute(uri: String) = "success/$uri"
    }
}
