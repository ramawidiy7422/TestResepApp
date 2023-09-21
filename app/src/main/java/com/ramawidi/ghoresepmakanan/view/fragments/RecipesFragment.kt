package com.ramawidi.ghoresepmakanan.view.fragments

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
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.utils.NetworkMonitor
import com.ramawidi.ghoresepmakanan.data.utils.NetworkStates
import com.ramawidi.ghoresepmakanan.data.utils.observeOnce
import com.ramawidi.ghoresepmakanan.databinding.FragmentRecipesBinding
import com.ramawidi.ghoresepmakanan.view.activities.MainActivity
import com.ramawidi.ghoresepmakanan.view.adapters.RecipesAdapter
import com.ramawidi.ghoresepmakanan.viewmodels.MainViewModel
import com.ramawidi.ghoresepmakanan.viewmodels.RecipesViewModel
import kotlinx.coroutines.launch


class RecipesFragment : Fragment() {
    private var _bindingRecipes: FragmentRecipesBinding? = null
    private val bindingRecipes get() = _bindingRecipes!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    //Variabel "args" memungkinkan akses ke argumen yang dibuat dalam Navigation Component.
    private val args by navArgs<RecipesFragmentArgs>()
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _bindingRecipes = FragmentRecipesBinding.inflate(inflater, container, false)
        return bindingRecipes.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        recipesViewModel = (activity as MainActivity).recipesViewModel
        bindingRecipes.lifecycleOwner = viewLifecycleOwner
        bindingRecipes.viewModel = mainViewModel
        setupOptionsMenu()
        setupRecyclerView()

        networkMonitor = (activity as MainActivity).networkMonitor
        // launchWhenStarted: lee el valor de "isNetworkAvailable" cuando estamos sólo en el fragment Recipes
        lifecycleScope.launchWhenStarted {
            networkMonitor.isNetworkAvailable.collect {
                    status ->
                recipesViewModel.networkStatus = status
                recipesViewModel.checkNetworkStatus()
                readDatabase()
            }
        }

        recipesViewModel.readBackOnlineState.observe(viewLifecycleOwner) {
            recipesViewModel.backOnlineState = it
        }

        bindingRecipes.fabRecipes.setOnClickListener {
            if (recipesViewModel.networkStatus == true) {
                findNavController().navigate(R.id.action_nav_recipesFragment_to_bottomSheetFragment)
            }
            else {
                recipesViewModel.checkNetworkStatus()
            }
        }
    }

    override fun onPause() {
        bindingRecipes.shimmerLayout.stopShimmer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // For avoid memory leaks
        _bindingRecipes = null
    }

    private fun setupOptionsMenu() {
        val menuProvider: MenuProvider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.main_menu, menu)
                // Declaramos ícono "search" para ejecutarse como un SearchView
                val search = menu.findItem(R.id.action_search)
                val searchView = search?.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            searchApiData(query)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }

                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadShimmerEffect() {
        bindingRecipes.shimmerLayout.visibility = View.VISIBLE
        bindingRecipes.shimmerLayout.startShimmer()
    }

    private fun stopShimmerEffect() {
        bindingRecipes.shimmerLayout.stopShimmer()
        bindingRecipes.shimmerLayout.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        bindingRecipes.recyclerView.adapter = mAdapter
        bindingRecipes.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun readDatabase() {
        mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) {
                database ->
            // Verificamos que argumento "backFromBottomSheet" es False (con valor Verdadero hace petición nueva a la API)
            if (database.isNotEmpty() && !args.backFromBottomSheet) {
                Log.d("RecipesFragment", "Database: $database")
                mAdapter.setDataToAdapter(database[0].foodRecipe)
                stopShimmerEffect()
            }
            else {
                requestApiData()
            }
        }
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesLiveData.observe(viewLifecycleOwner) {
                response ->
            Log.d("RecipesFragment", "Response: ${response.data}")
            when(response) {
                is NetworkStates.Success -> {
                    stopShimmerEffect()
                    response.data?.let {
                        Log.d("RecipesFragment", "Success: ${it.resultsRecipe}")
                        mAdapter.setDataToAdapter(it)
                    }
                }
                is NetworkStates.Error -> {
                    stopShimmerEffect()
                    // Cargamos datos de database cuando no haya respuesta del servicio API
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkStates.Loading -> {
                    loadShimmerEffect()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) {
                    database ->
                if (database.isNotEmpty()) {
                    mAdapter.setDataToAdapter(database[0].foodRecipe)
                }
            }
        }
    }

    private fun searchApiData(search: String) {
        loadShimmerEffect()
        val searchQuery = recipesViewModel.applySearchQuery(search)
        mainViewModel.searchRecipes(searchQuery)
        mainViewModel.searchRecipeLiveData.observe(viewLifecycleOwner) {
                response ->
            when(response) {
                is NetworkStates.Success -> {
                    stopShimmerEffect()
                    response.data?.let {
                        mAdapter.setDataToAdapter(it)
                    }
                }
                is NetworkStates.Error -> {
                    stopShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkStates.Loading -> {
                    loadShimmerEffect()
                }
            }
        }
    }

}