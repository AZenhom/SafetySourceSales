package com.safetysource.appretailer.ui.host

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.safetysource.appretailer.R
import com.safetysource.appretailer.databinding.ActivityHostBinding
import com.safetysource.appretailer.ui.splash.SplashActivity
import com.safetysource.core.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : BaseActivity<ActivityHostBinding, HostViewModel>() {

    override val viewModel: HostViewModel by viewModels()
    override val binding: ActivityHostBinding by viewBinding(ActivityHostBinding::inflate)

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, HostActivity::class.java).apply {
            }
        }
    }

    override fun onActivityCreated() {
        doubleBackToExitPressedOnce = false
        initObservers()
        initViews()
    }

    private fun initObservers() {
        viewModel.onLogOutLiveData.observe(this) {
            val intent = SplashActivity.getIntent(this)
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        //prevent recreate on re-selecting
        binding.bottomNav.setOnItemReselectedListener { println() }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, true).build()
        binding.bottomNav.setOnItemSelectedListener { item: MenuItem ->
            navController.navigate(item.itemId, null, navOptions)
            true
        }
    }

}