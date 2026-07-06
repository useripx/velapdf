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
import com.njagakneai.velapdf.ui.screen.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            SplashScreen(
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Permissions.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            PermissionsScreen(
                onPermissionsGranted = {
                    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                    val destination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Dashboard.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.History.route, Screen.Settings.route -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(300))
                    Screen.ImageToPdf.route, Screen.MergePdf.route, Screen.PdfToImage.route -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
                    else -> fadeIn(animationSpec = tween(300))
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.History.route, Screen.Settings.route -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300))
                    Screen.ImageToPdf.route, Screen.MergePdf.route, Screen.PdfToImage.route -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
                    else -> fadeOut(animationSpec = tween(300))
                }
            }
        ) {
            DashboardScreen(
                onNavigateToImageToPdf = {
                    navController.navigate(Screen.ImageToPdf.route)
                },
                onNavigateToMergePdf = {
                    navController.navigate(Screen.MergePdf.route)
                },
                onNavigateToPdfToImage = {
                    navController.navigate(Screen.PdfToImage.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToEditImage = { imageUri ->
                    navController.navigate(Screen.EditImage.createRoute(Uri.encode(imageUri.toString())))
                }
            )
        }

        composable(
            route = Screen.ImageToPdf.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) }
        ) { backStackEntry ->
            val updatedOriginalUriStr = backStackEntry.savedStateHandle.get<String>("updated_uri_original")
            val updatedNewUriStr = backStackEntry.savedStateHandle.get<String>("updated_uri_new")
            val updatedImage = if (updatedOriginalUriStr != null && updatedNewUriStr != null) {
                Pair(Uri.parse(updatedOriginalUriStr), Uri.parse(updatedNewUriStr))
            } else null

            // Clear the saved state after reading it to avoid stale updates
            if (updatedImage != null) {
                backStackEntry.savedStateHandle.remove<String>("updated_uri_original")
                backStackEntry.savedStateHandle.remove<String>("updated_uri_new")
            }

            com.njagakneai.velapdf.ui.screen.ImageToPdfScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConversionSuccess = { encodedUri ->
                    navController.navigate(Screen.Success.createRoute(encodedUri)) {
                        popUpTo(Screen.ImageToPdf.route) { inclusive = true }
                    }
                },
                onNavigateToEdit = { imageUri ->
                    navController.navigate(Screen.EditImage.createRoute(Uri.encode(imageUri.toString())))
                },
                updatedImage = updatedImage
            )
        }

        composable(
            route = Screen.MergePdf.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) }
        ) {
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

        composable(
            route = Screen.PdfToImage.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) }
        ) {
            com.njagakneai.velapdf.ui.screen.PdfToImageScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConvertSuccess = { encodedUri ->
                    navController.navigate(Screen.Success.createRoute(encodedUri)) {
                        popUpTo(Screen.PdfToImage.route) { inclusive = true }
                    }
                }
            )
        }


        composable(
            route = Screen.History.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(300)) }
        ) {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(300)) }
        ) {
            SettingsScreen(
                onNavigateBack = {
                    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                    if (!isLoggedIn) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Screen.Converter.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
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
            route = Screen.EditImage.route,
            arguments = listOf(androidx.navigation.navArgument("imageUri") { type = androidx.navigation.NavType.StringType }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(300)) }
        ) { backStackEntry ->
            val imageUriStr = backStackEntry.arguments?.getString("imageUri") ?: ""
            val imageUri = Uri.parse(Uri.decode(imageUriStr))
            com.njagakneai.velapdf.ui.screen.EditImageScreen(
                imageUri = imageUri,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { originalUri, newUri ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("updated_uri_original", originalUri.toString())
                    navController.previousBackStackEntry?.savedStateHandle?.set("updated_uri_new", newUri.toString())
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Success.route,
            arguments = listOf(androidx.navigation.navArgument("uri") { type = androidx.navigation.NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
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
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object ImageToPdf : Screen("image_to_pdf")
    object MergePdf : Screen("merge_pdf")
    object PdfToImage : Screen("pdf_to_image")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Converter : Screen("converter")
    object EditImage : Screen("edit_image/{imageUri}") {
        fun createRoute(imageUri: String) = "edit_image/$imageUri"
    }
    object Success : Screen("success/{uri}") {
        fun createRoute(uri: String) = "success/$uri"
    }
}
