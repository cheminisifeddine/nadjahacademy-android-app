package dz.nadjahacademy.app.navigation

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dz.nadjahacademy.feature.auth.ui.ForgotPasswordScreen
import dz.nadjahacademy.feature.auth.ui.LoginScreen
import dz.nadjahacademy.feature.auth.ui.OnboardingScreen
import dz.nadjahacademy.feature.auth.ui.RegisterScreen
import dz.nadjahacademy.feature.auth.ui.EmailVerificationScreen
import dz.nadjahacademy.feature.auth.ui.ResetPasswordScreen
import dz.nadjahacademy.feature.blog.ui.BlogDetailScreen
import dz.nadjahacademy.feature.blog.ui.BlogListScreen
import dz.nadjahacademy.feature.course.ui.CourseDetailScreen
import dz.nadjahacademy.feature.discussion.ui.DiscussionScreen
import dz.nadjahacademy.feature.explore.ui.ExploreScreen
import dz.nadjahacademy.feature.home.ui.HomeScreen
import dz.nadjahacademy.feature.instructor.ui.InstructorProfileScreen
import dz.nadjahacademy.feature.lesson.ui.LessonPlayerScreen
import dz.nadjahacademy.feature.mylearning.ui.CertificateViewerScreen
import dz.nadjahacademy.feature.mylearning.ui.MyLearningScreen
import dz.nadjahacademy.feature.notifications.ui.NotificationsScreen
import dz.nadjahacademy.feature.payment.ui.CheckoutScreen
import dz.nadjahacademy.feature.payment.ui.PaymentSuccessScreen
import dz.nadjahacademy.feature.profile.ui.AchievementsScreen
import dz.nadjahacademy.feature.profile.ui.EditProfileScreen
import dz.nadjahacademy.feature.profile.ui.ProfileScreen
import dz.nadjahacademy.feature.quiz.ui.QuizScreen
import dz.nadjahacademy.feature.quiz.ui.QuizResultScreen
import dz.nadjahacademy.feature.search.ui.SearchScreen
import dz.nadjahacademy.feature.settings.ui.HelpSupportScreen
import dz.nadjahacademy.feature.settings.ui.SettingsScreen
import dz.nadjahacademy.feature.settings.ui.WebViewScreen

sealed class Screen(val route: String) {
    // Auth
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password/{token}") {
        fun createRoute(token: String) = "reset_password/$token"
    }
    object EmailVerification : Screen("email_verification")

    // Main (bottom nav)
    object Main : Screen("main")
    object Home : Screen("home")
    object Explore : Screen("explore")
    object MyLearning : Screen("my_learning")
    object Blog : Screen("blog")
    object Profile : Screen("profile")

    // Course
    object CourseDetail : Screen("course/{slug}") {
        fun createRoute(slug: String) = "course/$slug"
    }
    object LessonPlayer : Screen("lesson/{lessonId}?courseId={courseId}") {
        fun createRoute(lessonId: String, courseId: String = "") = "lesson/$lessonId?courseId=$courseId"
    }
    object Quiz : Screen("quiz/{quizId}") {
        fun createRoute(quizId: String) = "quiz/$quizId"
    }
    object QuizResult : Screen("quiz_result/{attemptId}") {
        fun createRoute(attemptId: String) = "quiz_result/$attemptId"
    }

    // Blog
    object BlogDetail : Screen("blog/{slug}") {
        fun createRoute(slug: String) = "blog/$slug"
    }

    // Profile
    object EditProfile : Screen("edit_profile")
    object Achievements : Screen("achievements")
    object CertificateViewer : Screen("certificate/{certId}") {
        fun createRoute(certId: String) = "certificate/$certId"
    }

    // Instructor
    object InstructorProfile : Screen("instructor/{slug}") {
        fun createRoute(slug: String) = "instructor/$slug"
    }

    // Search
    object Search : Screen("search?q={query}") {
        fun createRoute(query: String = "") = "search?q=$query"
    }

    // Payment
    object Checkout : Screen("checkout/{courseIds}") {
        fun createRoute(courseIds: String) = "checkout/$courseIds"
    }
    object PaymentSuccess : Screen("payment_success/{orderId}") {
        fun createRoute(orderId: String) = "payment_success/$orderId"
    }

    // Notifications
    object Notifications : Screen("notifications")

    // Settings & pages
    object Settings : Screen("settings")
    object HelpSupport : Screen("help_support")
    object Discussion : Screen("discussion/{courseId}?lessonId={lessonId}") {
        fun createRoute(courseId: String, lessonId: String = "") = "discussion/$courseId?lessonId=$lessonId"
    }
    object WebView : Screen("webview/{url}") {
        fun createRoute(url: String) = "webview/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
}

@Composable
fun NadjahNavHost(
    startDestination: String = Screen.Onboarding.route,
    deepLinkIntent: Intent? = null,
    navController: NavHostController = rememberNavController(),
) {
    val slideEnter: EnterTransition = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300))
    val slideExit: ExitTransition = slideOutHorizontally(targetOffsetX = { -it / 4 }, animationSpec = tween(300)) + fadeOut(tween(300))
    val slidePopEnter: EnterTransition = slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(300)) + fadeIn(tween(300))
    val slidePopExit: ExitTransition = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { slideEnter },
        exitTransition = { slideExit },
        popEnterTransition = { slidePopEnter },
        popExitTransition = { slidePopExit },
    ) {
        // ─── Auth ─────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = { navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onEmailSent = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) {
            ResetPasswordScreen(
                token = it.arguments?.getString("token") ?: "",
                onResetSuccess = { navController.navigate(Screen.Login.route) { popUpTo(0) } },
            )
        }
        composable(Screen.EmailVerification.route) {
            EmailVerificationScreen(
                onVerified = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
                onSkip = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
            )
        }

        // ─── Main (bottom nav shell) ──────────────────
        composable(Screen.Main.route) {
            MainScaffold(
                navController = navController,
                onNavigateToCourse = { slug -> navController.navigate(Screen.CourseDetail.createRoute(slug)) },
                onNavigateToBlog = { slug -> navController.navigate(Screen.BlogDetail.createRoute(slug)) },
                onNavigateToSearch = { navController.navigate(Screen.Search.createRoute()) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToInstructor = { slug -> navController.navigate(Screen.InstructorProfile.createRoute(slug)) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToLesson = { lessonId, courseId -> navController.navigate(Screen.LessonPlayer.createRoute(lessonId, courseId)) },
                onNavigateToCheckout = { courseIds -> navController.navigate(Screen.Checkout.createRoute(courseIds)) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToCertificate = { certId -> navController.navigate(Screen.CertificateViewer.createRoute(certId)) },
            )
        }

        // ─── Course ───────────────────────────────────
        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "https://www.nadjahacademy.com/courses/{slug}" }),
        ) {
            CourseDetailScreen(
                slug = it.arguments?.getString("slug") ?: "",
                onBack = { navController.popBackStack() },
                onNavigateToLesson = { lessonId, courseId -> navController.navigate(Screen.LessonPlayer.createRoute(lessonId, courseId)) },
                onNavigateToInstructor = { slug -> navController.navigate(Screen.InstructorProfile.createRoute(slug)) },
                onNavigateToCheckout = { courseIds -> navController.navigate(Screen.Checkout.createRoute(courseIds)) },
                onNavigateToDiscussion = { courseId -> navController.navigate(Screen.Discussion.createRoute(courseId)) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
            )
        }

        composable(
            route = Screen.LessonPlayer.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("courseId") { type = NavType.StringType; defaultValue = "" },
            ),
        ) {
            LessonPlayerScreen(
                lessonId = it.arguments?.getString("lessonId") ?: "",
                courseId = it.arguments?.getString("courseId") ?: "",
                onBack = { navController.popBackStack() },
                onNavigateToQuiz = { quizId -> navController.navigate(Screen.Quiz.createRoute(quizId)) },
                onNavigateToDiscussion = { courseId -> navController.navigate(Screen.Discussion.createRoute(courseId)) },
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType }),
        ) {
            QuizScreen(
                quizId = it.arguments?.getString("quizId") ?: "",
                onBack = { navController.popBackStack() },
                onViewResults = { attemptId -> navController.navigate(Screen.QuizResult.createRoute(attemptId)) },
            )
        }

        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(navArgument("attemptId") { type = NavType.StringType }),
        ) {
            QuizResultScreen(
                attemptId = it.arguments?.getString("attemptId") ?: "",
                onBack = { navController.popBackStack() },
                onRetry = { navController.popBackStack(Screen.Quiz.route, inclusive = false) },
            )
        }

        // ─── Blog ─────────────────────────────────────
        composable(
            route = Screen.BlogDetail.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "https://www.nadjahacademy.com/blog/{slug}" }),
        ) {
            BlogDetailScreen(
                slug = it.arguments?.getString("slug") ?: "",
                onBack = { navController.popBackStack() },
                onNavigateToBlog = { inSlug -> navController.navigate(Screen.BlogDetail.createRoute(inSlug)) },
            )
        }

        // ─── Profile ──────────────────────────────────
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToCertificate = { certId -> navController.navigate(Screen.CertificateViewer.createRoute(certId)) },
                onNavigateToHelpSupport = { navController.navigate(Screen.HelpSupport.route) },
                onLogout = { navController.navigate(Screen.Onboarding.route) { popUpTo(0) } },
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToWebView = { url -> navController.navigate(Screen.WebView.createRoute(url)) },
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.CertificateViewer.route,
            arguments = listOf(navArgument("certId") { type = NavType.StringType }),
        ) {
            CertificateViewerScreen(
                certId = it.arguments?.getString("certId") ?: "",
                onBack = { navController.popBackStack() },
            )
        }

        // ─── Instructor ───────────────────────────────
        composable(
            route = Screen.InstructorProfile.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "https://www.nadjahacademy.com/instructors/{slug}" }),
        ) {
            InstructorProfileScreen(
                slug = it.arguments?.getString("slug") ?: "",
                onBack = { navController.popBackStack() },
                onNavigateToCourse = { slug -> navController.navigate(Screen.CourseDetail.createRoute(slug)) },
            )
        }

        // ─── Search ───────────────────────────────────
        composable(
            route = Screen.Search.route,
            arguments = listOf(navArgument("query") { type = NavType.StringType; defaultValue = "" }),
        ) {
            SearchScreen(
                initialQuery = it.arguments?.getString("query") ?: "",
                onBack = { navController.popBackStack() },
                onNavigateToCourse = { slug -> navController.navigate(Screen.CourseDetail.createRoute(slug)) },
                onNavigateToBlog = { slug -> navController.navigate(Screen.BlogDetail.createRoute(slug)) },
                onNavigateToInstructor = { slug -> navController.navigate(Screen.InstructorProfile.createRoute(slug)) },
            )
        }

        // ─── Payment ──────────────────────────────────
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("courseIds") { type = NavType.StringType }),
        ) {
            CheckoutScreen(
                courseIds = it.arguments?.getString("courseIds") ?: "",
                onBack = { navController.popBackStack() },
                onSuccess = { orderId -> navController.navigate(Screen.PaymentSuccess.createRoute(orderId)) { popUpTo(Screen.Main.route) } },
            )
        }

        composable(
            route = Screen.PaymentSuccess.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
        ) {
            PaymentSuccessScreen(
                orderId = it.arguments?.getString("orderId") ?: "",
                onGoToMyLearning = { navController.navigate(Screen.Main.route) { popUpTo(0) } },
            )
        }

        // ─── Notifications ────────────────────────────
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCourse = { slug -> navController.navigate(Screen.CourseDetail.createRoute(slug)) },
                onNavigateToBlog = { slug -> navController.navigate(Screen.BlogDetail.createRoute(slug)) },
            )
        }

        // ─── Settings & Help ──────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToWebView = { url -> navController.navigate(Screen.WebView.createRoute(url)) },
                onNavigateToHelpSupport = { navController.navigate(Screen.HelpSupport.route) },
                onLogout = { navController.navigate(Screen.Onboarding.route) { popUpTo(0) } },
            )
        }

        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Discussion.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType; defaultValue = "" },
            ),
        ) {
            DiscussionScreen(
                courseId = it.arguments?.getString("courseId") ?: "",
                lessonId = it.arguments?.getString("lessonId"),
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.WebView.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType }),
        ) {
            WebViewScreen(
                url = java.net.URLDecoder.decode(it.arguments?.getString("url") ?: "", "UTF-8"),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
