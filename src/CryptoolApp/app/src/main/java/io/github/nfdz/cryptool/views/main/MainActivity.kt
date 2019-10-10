package io.github.nfdz.cryptool.views.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.BroadcastHelper
import io.github.nfdz.cryptool.common.utils.OverlayPermissionHelper
import io.github.nfdz.cryptool.views.cypher.CypherFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*


class MainActivity : AppCompatActivity(), OverlayPermissionHelper.Callback {

    companion object {
        const val OPEN_CYPHER_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_TOOL_BALL"
        const val OPEN_HASH_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_HASH_BALL"
        const val OPEN_KEYS_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_KEYS_BALL"

        @JvmStatic
        fun startActivity(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) })
        }
    }

    private var pagerAdapter: MainPagerAdapter? = null
    private val permissionHelper: OverlayPermissionHelper by lazy {
        OverlayPermissionHelper(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        BroadcastHelper.sendCloseFloatingWindowsBroadcast(this)
        handleIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHelper.onActivityResult(requestCode, resultCode, data!!)
    }

    private fun handleIntent(): Boolean {
        val action = intent?.action
        return if (action?.isNotEmpty() == true && permissionHelper.hasPermission()) {
            return when (action) {
                OPEN_CYPHER_BALL_ACTION -> true
                OPEN_HASH_BALL_ACTION -> true
                OPEN_KEYS_BALL_ACTION -> true
                else -> false
            }
        } else {
            false
        }
    }

    private fun setupView() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        pagerAdapter = MainPagerAdapter(this, supportFragmentManager)
        main_view_pager.adapter = pagerAdapter
        main_nav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_nav_cipher -> {
                    main_view_pager.currentItem = 0; true
                }
                R.id.main_nav_hash -> {
                    main_view_pager.currentItem = 1; true
                }
                R.id.main_nav_keys -> {
                    main_view_pager.currentItem = 2; true
                }
                else -> false
            }
        }
        main_nav.selectedItemId = R.id.main_nav_cipher
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_settings -> {
                permissionHelper.navigateToSettings(); true
            }
//            R.id.main_menu_rate_suggestions -> { navigateToClub(); true }
//            R.id.main_menu_about -> { navigateToPlaylist(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class MainPagerAdapter(val context: Context, fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int) = when (position) {
            0 -> CypherFragment.newInstance()
            1 -> CypherFragment.newInstance()
            2 -> CypherFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid tab position=$position")
        }

        override fun getCount() = 3

    }

    override fun onPermissionGranted() {
//        ToolBallService.start(this);
//        finish();
    }

    override fun onPermissionDenied() {
//        Toast.makeText(this,
//            "Draw over other app permission not available.",
//            Toast.LENGTH_SHORT).show();
    }
}
