package com.ramawidi.ghoresepmakanan.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.utils.NetworkMonitor
import com.ramawidi.ghoresepmakanan.databinding.ActivityMainBinding
import com.ramawidi.ghoresepmakanan.viewmodels.MainViewModel
import com.ramawidi.ghoresepmakanan.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var mainViewModel: MainViewModel
    lateinit var recipesViewModel: RecipesViewModel
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(this)[RecipesViewModel::class.java]
        networkMonitor = NetworkMonitor(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_recipesFragment, R.id.nav_favRecipesFragment, R.id.nav_foodJokeFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bindingMain.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}