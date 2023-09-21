package com.ramawidi.ghoresepmakanan.view.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.database.FavoritesEntity
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.ARGS_KEY
import com.ramawidi.ghoresepmakanan.databinding.ActivityDetailsBinding
import com.ramawidi.ghoresepmakanan.view.adapters.ViewPagerAdapter
import com.ramawidi.ghoresepmakanan.view.fragments.IngredientPageFragment
import com.ramawidi.ghoresepmakanan.view.fragments.InstructionPageFragment
import com.ramawidi.ghoresepmakanan.view.fragments.OverviewPageFragment
import com.ramawidi.ghoresepmakanan.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private lateinit var bindingDetails: ActivityDetailsBinding
    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var menuItem: MenuItem
    private var isRecipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingDetails = DataBindingUtil.setContentView(this, R.layout.activity_details)

        setSupportActionBar(bindingDetails.toolbarDetails)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        tabLayout =  bindingDetails.tabLayoutDetails
        viewPager2 = bindingDetails.viewPager2Details
        // Fragment list for ViewPager adapter
        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewPageFragment())
        fragments.add(IngredientPageFragment())
        fragments.add(InstructionPageFragment())
        // Create a Bundle object
        val resultBundle = Bundle()
        resultBundle.putParcelable(ARGS_KEY, args.resultRecipe)
        // Initialize the adapter
        adapter = ViewPagerAdapter(resultBundle, fragments, this)
        viewPager2.adapter = adapter
        // List titles for tabs
        val tabTitles = ArrayList<String>()
        tabTitles.add("Overview")
        tabTitles.add("Ingredients")
        tabTitles.add("Instructions")

        TabLayoutMediator(tabLayout, viewPager2) {
                tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        // Restore the white color for Star icon (favorite) when we close this Activity
        changeFavoriteIconColor(menuItem, R.color.white)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        // Kami mencari ikon Favorit  (star)
        menuItem = menu!!.findItem(R.id.action_save_favorite)
        checkFavoritesRecipe(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_save_favorite -> {
                if (!isRecipeSaved) {
                    saveToFavorites(item)
                }
                else {
                    removeFromFavorites(item)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkFavoritesRecipe(menuItem: MenuItem) {
        mainViewModel.readFavorites.observe(this) {
                favoritesList ->
            try {
                //  mencari di tabel Favorit apakah ID dari resep yang ditampilkan ada.
                for (favorite in favoritesList) {
                    if (favorite.result.id == args.resultRecipe.id) {
                        changeFavoriteIconColor(menuItem, R.color.yellow)
                        savedRecipeId = favorite.id
                        isRecipeSaved = true
                    }
                }
            }
            catch (e: Exception) {
                Log.d("DetailsActivity", "Exception: ${e.message.toString()}")
            }
        }
    }

    private fun saveToFavorites(item: MenuItem) {
        val favorite = FavoritesEntity(0, args.resultRecipe)
        mainViewModel.insertFavorite(favorite)
        changeFavoriteIconColor(item, R.color.yellow)
        showSnackBar("Recipe added to Favorites!")
        isRecipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favorite = FavoritesEntity(savedRecipeId, args.resultRecipe)
        mainViewModel.deleteFavorite(favorite)
        changeFavoriteIconColor(item, R.color.black)
        showSnackBar("Recipe deleted!")
        isRecipeSaved = false
    }

    private fun changeFavoriteIconColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(bindingDetails.root, message,
            Snackbar.LENGTH_SHORT).setAction("Ok") {}.show()
    }

}