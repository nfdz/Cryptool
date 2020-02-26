package io.github.nfdz.cryptool.screens.main

import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.*
import io.github.nfdz.cryptool.services.BallService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*
import timber.log.Timber

/**
 * Main activity of the application. This is the entry point activity.
 */
class MainActivity : AppCompatActivity(), OverlayPermissionHelper.Callback {

    companion object {
        @JvmStatic
        fun startNewActivity(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) })
        }
    }

    private val prefs by lazy { PreferencesHelper(this) }
    private var pagerAdapter: MainPagerAdapter? = null
    private val permissionHelper: OverlayPermissionHelper by lazy {
        OverlayPermissionHelper(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        );
        resolveTheme()
        setupView()
        BallService.stop(this)
        askCodeIfNeeded()
        showWelcomeIfNeeded()
    }

    override fun onStart() {
        super.onStart()
        unscheduleStopApp()
        enableAutoStopApp()
    }

    override fun onStop() {
        if (hasPinCode()) {
            scheduleStopApp(getString(R.string.cb_label), getClipboard())
        }
        super.onStop()
    }

    private fun askCodeIfNeeded() {
        if (hasPinCode() && !CODE_ASKED_ONCE) {
            PinCodeDialog.show(this, createPinMode = false) {
                onCodeSet()
            }
        }
    }

    private fun showWelcomeIfNeeded() {
        if (showWelcome(this)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.welcome_app_title)
                .setMessage(R.string.welcome_app_content)
                .setPositiveButton(R.string.welcome_app_pin) { dialog, _ ->
                    PinCodeDialog.show(this, createPinMode = true) {
                        onCodeSet()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.welcome_app_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun onCodeSet() {
        CODE_ASKED_ONCE = true
        val currentPage = main_view_pager.currentItem
        pagerAdapter = MainPagerAdapter(supportFragmentManager)
        main_view_pager.adapter = pagerAdapter
        main_view_pager.setCurrentItem(currentPage, false)
    }

    private fun hasPinCode(): Boolean = prefs.hasCode()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHelper.onActivityResult(requestCode)
    }

    override fun onBackPressed() {
        stopApp(getString(R.string.cb_label), getClipboard())
        super.onBackPressed()
    }

    private fun resolveTheme() {
        val customThemeNightMode = prefs.getThemeNightMode()
        if (customThemeNightMode != null) {
            if (isNightUiMode() != customThemeNightMode) {
                recreate()
            }
            if (customThemeNightMode) {
                setTheme(R.style.AppThemeDark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                setTheme(R.style.AppThemeLight)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            if (isNightUiMode() == true) {
                setTheme(R.style.AppThemeDark)
            } else {
                setTheme(R.style.AppThemeLight)
            }
        }
    }

    private fun setupView() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        setupViewPagerWithNav()
        main_fab_ball.setOnClickListener {
            disableAutoStopApp()
            permissionHelper.request()
        }
    }

    private fun setupViewPagerWithNav() {
        val initialTab = prefs.getLastTab()
        pagerAdapter = MainPagerAdapter(supportFragmentManager)
        main_view_pager.adapter = pagerAdapter
        main_nav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_nav_cipher -> {
                    main_view_pager.currentItem = 0
                    prefs.setLastTab(0)
                    true
                }
                R.id.main_nav_hash -> {
                    main_view_pager.currentItem = 1
                    prefs.setLastTab(1)
                    true
                }
                R.id.main_nav_keys -> {
                    main_view_pager.currentItem = 2
                    prefs.setLastTab(2)
                    true
                }
                else -> false
            }
        }
        when (initialTab) {
            2 -> {
                main_view_pager.setCurrentItem(2, false)
                main_nav.selectedItemId = R.id.main_nav_keys
            }
            1 -> {
                main_view_pager.setCurrentItem(1, false)
                main_nav.selectedItemId = R.id.main_nav_hash
            }
            else -> {
                main_view_pager.setCurrentItem(0, false)
                main_nav.selectedItemId = R.id.main_nav_cipher
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_settings -> {
                try {
                    disableAutoStopApp()
                    permissionHelper.navigateToSettings()
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.error_no_settings)
                }
                true
            }
            R.id.main_menu_pin_code -> {
                if (hasPinCode()) {
                    askManagePin()
                } else {
                    askCreatePin()
                }
                true
            }
            R.id.main_menu_clipboard_clear -> {
                ClipboardHelper.clearClipboard(this)
                true
            }
            R.id.main_menu_import -> {

                true
            }
            R.id.main_menu_export -> {
                // TODO
                true
            }
            R.id.main_menu_rate_suggestions -> {
                showSuggestionsDialog()
                true
            }
            R.id.main_menu_about -> {
                showAboutDialog()
                true
            }
            R.id.main_menu_toggle_theme -> {
                askThemeDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun askCreatePin() {
        AlertDialog.Builder(this)
            .setTitle(R.string.pin_create_title)
            .setMessage(R.string.pin_create_content)
            .setPositiveButton(R.string.pin_create_btn) { dialog, _ ->
                val migrationHelper = MigrationHelper(prefs)
                PinCodeDialog.show(this, createPinMode = true) {
                    migrationHelper.deployData()
                    onCodeSet()
                    toast(R.string.pin_created_success)
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun askManagePin() {
        AlertDialog.Builder(this)
            .setTitle(R.string.pin_manage_title)
            .setPositiveButton(R.string.pin_modify_btn) { dialog, _ ->
                val migrationHelper = MigrationHelper(prefs)
                PinCodeDialog.show(this, createPinMode = true) {
                    migrationHelper.deployData()
                    onCodeSet()
                    toast(R.string.pin_modified_success)
                }
                dialog.dismiss()
            }
            .setNeutralButton(R.string.pin_delete_btn) { dialog, _ ->
                val migrationHelper = MigrationHelper(prefs)
                CODE = DEFAULT_CODE
                prefs.deleteCode()
                migrationHelper.deployData()
                toast(R.string.pin_deleted_success)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_app_title)
            .setMessage(R.string.about_app_content)
            .setPositiveButton(R.string.about_app_btn) { dialog, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)))
                } catch (e: Exception) {
                    Timber.e(e)
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun showSuggestionsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.rate_app_title)
            .setMessage(R.string.rate_app_content)
            .setPositiveButton(R.string.rate_app_btn) { dialog, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(STORE_URL)))
                } catch (e: Exception) {
                    Timber.e(e)
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun askThemeDialog() {
        val customThemeNightMode = prefs.getThemeNightMode()
        val optionsTitles = arrayOf(
            getText(R.string.theme_light),
            getText(R.string.theme_dark),
            getText(R.string.theme_system)
        )
        val selectedOption = when (customThemeNightMode) {
            false -> 0
            true -> 1
            null -> 2
        }
        AlertDialog.Builder(this)
            .setSingleChoiceItems(
                optionsTitles,
                selectedOption
            )
            { dialog, which ->
                when (which) {
                    0 -> {
                        prefs.setThemeNightMode(false)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    1 -> {
                        prefs.setThemeNightMode(true)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    2 -> {
                        prefs.setThemeNightMode(null)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private inner class MainPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int) = when (position) {
            0 -> if (hideContent()) Fragment() else CipherFragment.newInstance()
            1 -> if (hideContent()) Fragment() else HashFragment.newInstance()
            2 -> if (hideContent()) Fragment() else KeysFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid tab position=$position")
        }

        override fun getCount() = 3

        private fun hideContent(): Boolean = hasPinCode() && !CODE_ASKED_ONCE

    }

    override fun onPermissionGranted() {
        val action = when (main_view_pager.currentItem) {
            2 -> OPEN_KEYS_BALL_ACTION
            1 -> OPEN_HASH_BALL_ACTION
            else -> OPEN_CIPHER_BALL_ACTION
        }
        BallService.start(this, action)
        finish()
    }

    override fun onPermissionDenied() {
        toast(R.string.permission_denied)
    }
}
