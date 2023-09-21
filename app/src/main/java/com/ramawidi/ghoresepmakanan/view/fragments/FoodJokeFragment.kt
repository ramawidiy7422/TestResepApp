package com.ramawidi.ghoresepmakanan.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.API_KEY
import com.ramawidi.ghoresepmakanan.data.utils.NetworkStates
import com.ramawidi.ghoresepmakanan.databinding.FragmentFoodJokeBinding
import com.ramawidi.ghoresepmakanan.view.activities.MainActivity
import com.ramawidi.ghoresepmakanan.viewmodels.MainViewModel
import kotlinx.coroutines.launch

class FoodJokeFragment: Fragment() {
    private var _bindingJoke: FragmentFoodJokeBinding? = null
    private val bindingJoke get() = _bindingJoke!!
    private lateinit var mainViewModel: MainViewModel
    private var foodJoke = "No Food Joke"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _bindingJoke = FragmentFoodJokeBinding.inflate(inflater, container, false)
        return bindingJoke.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOptionsMenu()
        mainViewModel = (activity as MainActivity).mainViewModel
        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.fooJokeLiveData.observe(viewLifecycleOwner) {
                response ->
            when (response) {
                is NetworkStates.Loading -> {
                    bindingJoke.progressBarJoke.visibility = View.VISIBLE
                    bindingJoke.ivJokeEmpty.visibility = View.GONE
                    bindingJoke.tvJokeEmpty.visibility = View.GONE
                }
                is NetworkStates.Success -> {
                    bindingJoke.progressBarJoke.visibility = View.GONE
                    bindingJoke.ivJokeEmpty.visibility = View.GONE
                    bindingJoke.tvJokeEmpty.visibility = View.GONE
                    bindingJoke.cardViewJoke.visibility = View.VISIBLE
                    response.data?.let {
                        bindingJoke.tvFoodJoke.text = it.text
                        foodJoke = it.text
                    }
                }
                is NetworkStates.Error -> {
                    bindingJoke.progressBarJoke.visibility = View.GONE
                    bindingJoke.cardViewJoke.visibility = View.GONE
                    loadDataFromCache()
                    Toast.makeText(requireContext(), "Error: ${response.message.toString()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bindingJoke = null
    }

    private fun setupOptionsMenu() {
        val menuProvider: MenuProvider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.food_joke_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.action_share_joke -> {
                        val shareIntent = Intent().apply {
                            this.action = Intent.ACTION_SEND
                            this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                            this.type = "text/plain"
                        }
                        startActivity(shareIntent)
                        true
                    }
                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readFoodJoke.observe(viewLifecycleOwner) {
                    tableFoodJoke ->
                Log.d("JokeFragment", "Table data: ${tableFoodJoke.size}")
                if (tableFoodJoke.isNotEmpty()) {
                    bindingJoke.ivJokeEmpty.visibility = View.GONE
                    bindingJoke.tvJokeEmpty.visibility = View.GONE
                    bindingJoke.cardViewJoke.visibility = View.VISIBLE
                    bindingJoke.tvFoodJoke.text = tableFoodJoke[0].foodJoke.text
                    foodJoke = tableFoodJoke[0].foodJoke.text
                }
            }
        }
    }

}