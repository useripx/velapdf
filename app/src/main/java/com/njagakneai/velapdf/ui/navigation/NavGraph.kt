package com.njagakneai.velapdf.ui.navigation

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
}
