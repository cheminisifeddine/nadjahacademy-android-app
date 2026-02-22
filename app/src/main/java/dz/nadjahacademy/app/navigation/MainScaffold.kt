package dz.nadjahacademy.app.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dz.nadjahacademy.feature.blog.ui.BlogListScreen
import dz.nadjahacademy.feature.explore.ui.ExploreScreen
import dz.nadjahacademy.feature.home.ui.HomeScreen
import dz.nadjahacademy.feature.mylearning.ui.MyLearningScreen
import dz.nadjahacademy.feature.profile.ui.ProfileScreen

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    object Home : BottomNavItem("bottom_home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Explore : BottomNavItem("bottom_explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object MyLearning : BottomNavItem("bottom_my_learning", "My Learning", Icons.Filled.School, Icons.Outlined.School)
    object Blog : BottomNavItem("bottom_blog", "Blog", Icons.Filled.Article, Icons.Outlined.Article)
    object Profile : BottomNavItem("bottom_profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainScaffold(
    navController: NavController,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToBlog: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToInstructor: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    onNavigateToCheckout: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToCertificate: (String) -> Unit,
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.MyLearning,
        BottomNavItem.Blog,
        BottomNavItem.Profile,
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route,
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onNavigateToCourse = onNavigateToCourse,
                        onNavigateToBlog = onNavigateToBlog,
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToInstructor = onNavigateToInstructor,
                        onNavigateToExplore = {
                            bottomNavController.navigate(BottomNavItem.Explore.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToLesson = onNavigateToLesson,
                    )
                }

                composable(BottomNavItem.Explore.route) {
                    ExploreScreen(
                        onNavigateToCourse = onNavigateToCourse,
                        onNavigateToSearch = onNavigateToSearch,
                    )
                }

                composable(BottomNavItem.MyLearning.route) {
                    MyLearningScreen(
                        onNavigateToCourse = onNavigateToCourse,
                        onNavigateToLesson = onNavigateToLesson,
                        onNavigateToCertificate = onNavigateToCertificate,
                        onNavigateToCheckout = onNavigateToCheckout,
                    )
                }

                composable(BottomNavItem.Blog.route) {
                    BlogListScreen(
                        onNavigateToBlog = onNavigateToBlog,
                    )
                }

                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(
                        onBack = { /* no-op when in bottom nav */ },
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToAchievements = onNavigateToAchievements,
                        onNavigateToCertificate = onNavigateToCertificate,
                        onNavigateToHelpSupport = { navController.navigate("help_support") },
                        onLogout = { navController.navigate("onboarding") { popUpTo(0) } },
                    )
                }
            }
        }
    }
}
