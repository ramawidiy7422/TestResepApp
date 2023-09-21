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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.databinding.FragmentFavRecipesBinding
import com.ramawidi.ghoresepmakanan.view.adapters.FavoritesAdapter
import com.ramawidi.ghoresepmakanan.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavRecipesFragment : Fragment() {
    private var _bindingFavorites: FragmentFavRecipesBinding? = null
    private val bindingFavorites get() = _bindingFavorites!!
    //private lateinit var mainViewModel: MainViewModel
    //private lateinit var favAdapter: FavoritesAdapter
    private val mainViewModel: MainViewModel by viewModels()
    private val favAdapter: FavoritesAdapter by lazy { FavoritesAdapter(requireActivity(), mainViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _bindingFavorites = FragmentFavRecipesBinding.inflate(inflater, container, false)
        return bindingFavorites.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mainViewModel = (activity as MainActivity).mainViewModel
        //favAdapter = FavoritesAdapter(requireActivity(), mainViewModel)
        setupOptionsMenu()

        bindingFavorites.rvFavorites.adapter = favAdapter
        bindingFavorites.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        mainViewModel.readFavorites.observe(viewLifecycleOwner) {
                favoritesList ->
            Log.d("FavoritesFragment", "Table data: ${favoritesList.size}")
            if (favoritesList.isEmpty()) {
                bindingFavorites.rvFavorites.visibility = View.GONE
                bindingFavorites.ivFavoritesEmpty.visibility = View.VISIBLE
                bindingFavorites.tvFavoritesNoData.visibility = View.VISIBLE
            }
            else {
                bindingFavorites.rvFavorites.visibility = View.VISIBLE
                bindingFavorites.ivFavoritesEmpty.visibility = View.GONE
                bindingFavorites.tvFavoritesNoData.visibility = View.GONE
                favAdapter.setDataToAdapter(favoritesList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bindingFavorites = null
        // Limpiamos el ActionMode al salir del Fragment
        favAdapter.clearActionMode()
    }

    private fun setupOptionsMenu() {
        val menuProvider: MenuProvider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.favorites_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.action_delete_all -> {
                        deleteAllDialog()
                        true
                    }
                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun deleteAllDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.alert_message_title))
        builder.setMessage(resources.getString(R.string.alert_message_delete_all))
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Yes") {
                dialogInterface, _ ->
            mainViewModel.deleteAllFavorites()
            showSnackBar("All favorites recipes removed!")
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") {
                dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.decorView?.setBackgroundResource(R.drawable.windows_dialog_bg)
        dialog.show()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(bindingFavorites.root, message, Snackbar.LENGTH_SHORT).setAction("Ok") {}.show()
    }

}