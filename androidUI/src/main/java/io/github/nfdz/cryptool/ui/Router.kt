package io.github.nfdz.cryptool.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.nfdz.cryptool.ui.about.*
import io.github.nfdz.cryptool.ui.encryption.EncryptionScreen
import io.github.nfdz.cryptool.ui.gatekeeper.ChangeAccessCodeScreen
import io.github.nfdz.cryptool.ui.main.MainScreen
import io.github.nfdz.cryptool.ui.password.PasswordScreen

interface Router {
    val startDestination: String
    val isOverlayMode: Boolean
    fun applyConfiguration(navBuilder: NavGraphBuilder)
    fun popBackStack()
    fun popBackStackToRoot()
    fun navigateToPasswords()
    fun navigateToChangeAccessCode()
    fun navigateToReport()
    fun navigateToAlgorithms()
    fun navigateToAbout()
    fun navigateToLibraries()
    fun navigateToChangelog()
    fun navigateToEncryption(encryptionId: String, encryptionName: String)
    suspend fun navigateToOverlayPermission(): Boolean
    fun navigateToOverlayPermissionSettings()
    fun navigateToOverlayBall()
    fun navigateToUrl(url: String)
    fun exitOverlay()
}

fun Router.supportAdvancedFeatures(): Boolean = !this.isOverlayMode

private object RouterPaths {
    const val main = "main"
    const val passwords = "passwords"
    const val changeCode = "change-access"
    const val report = "report"
    const val about = "about"
    const val algorithms = "algorithms"
    const val encryption = "encryption"
    const val libraries = "libraries"
    const val changelog = "changelog"
}

abstract class RouterBase(
    private val navController: NavController,
) : Router {

    override val startDestination: String
        get() = RouterPaths.main

    override fun applyConfiguration(navBuilder: NavGraphBuilder) = navBuilder.run {
        composable(RouterPaths.main) { MainScreen(this@RouterBase) }
        composable("${RouterPaths.encryption}/{id}?name={name}", listOf(
            navArgument("id") { type = NavType.StringType },
            navArgument("name") { defaultValue = "" }
        )) {
            val encryptionId = it.arguments?.getString("id") ?: ""
            val encryptionName = it.arguments?.getString("name") ?: ""
            assert(encryptionId.isNotBlank())
            assert(encryptionName.isNotBlank())
            EncryptionScreen(
                router = this@RouterBase,
                encryptionId = encryptionId,
                encryptionName = encryptionName,
            )
        }
        composable(RouterPaths.passwords) { PasswordScreen(this@RouterBase) }
        composable(RouterPaths.changeCode) { ChangeAccessCodeScreen(this@RouterBase) }
        composable(RouterPaths.report) { ReportScreen(this@RouterBase) }
        composable(RouterPaths.about) { AboutScreen(this@RouterBase) }
        composable(RouterPaths.algorithms) { AlgorithmsScreen(this@RouterBase) }
        composable(RouterPaths.libraries) { LibrariesScreen(this@RouterBase) }
        composable(RouterPaths.changelog) { ChangelogScreen(this@RouterBase) }
    }

    override fun popBackStack() {
        navController.popBackStack()
    }

    override fun popBackStackToRoot() {
        navController.popBackStack(startDestination, inclusive = false)
    }

    override fun navigateToPasswords() {
        navController.navigate(RouterPaths.passwords)
    }

    override fun navigateToChangeAccessCode() {
        navController.navigate(RouterPaths.changeCode)
    }

    override fun navigateToReport() {
        navController.navigate(RouterPaths.report)
    }

    override fun navigateToAlgorithms() {
        navController.navigate(RouterPaths.algorithms)
    }

    override fun navigateToAbout() {
        navController.navigate(RouterPaths.about)
    }

    override fun navigateToLibraries() {
        navController.navigate(RouterPaths.libraries)
    }

    override fun navigateToChangelog() {
        navController.navigate(RouterPaths.changelog)
    }

    override fun navigateToEncryption(encryptionId: String, encryptionName: String) {
        navController.navigate("${RouterPaths.encryption}/$encryptionId?name=$encryptionName")
    }
}

object EmptyRouter : Router {
    override val startDestination: String = ""
    override val isOverlayMode: Boolean = false
    override fun applyConfiguration(navBuilder: NavGraphBuilder) {}
    override fun popBackStack() {}
    override fun popBackStackToRoot() {}
    override fun navigateToPasswords() {}
    override fun navigateToChangeAccessCode() {}
    override fun navigateToReport() {}
    override fun navigateToAlgorithms() {}
    override fun navigateToAbout() {}
    override fun navigateToLibraries() {}
    override fun navigateToChangelog() {}
    override fun navigateToEncryption(encryptionId: String, encryptionName: String) {}
    override suspend fun navigateToOverlayPermission(): Boolean = false
    override fun navigateToOverlayPermissionSettings() {}
    override fun navigateToOverlayBall() {}
    override fun navigateToUrl(url: String) {}
    override fun exitOverlay() {}
}