package com.ramawidi.ghoresepmakanan.view.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.database.FavoritesEntity
import com.ramawidi.ghoresepmakanan.data.utils.AdapterDiffUtil
import com.ramawidi.ghoresepmakanan.databinding.FavoriteItemLayoutBinding
import com.ramawidi.ghoresepmakanan.view.fragments.FavRecipesFragmentDirections
import com.ramawidi.ghoresepmakanan.viewmodels.MainViewModel


class FavoritesAdapter(private val fragActivity: FragmentActivity,
                       private val mainViewModel: MainViewModel
):
    RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>(), ActionMode.Callback {

    private var favoritesList = emptyList<FavoritesEntity>()
    private var multipleSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var recipesToRemove = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()
    private lateinit var myActionMode: ActionMode
    private lateinit var rootViewHolder: View
    private var confirmRemove: Boolean? = null

    class MyViewHolder(private val bindingFav: FavoriteItemLayoutBinding):
        RecyclerView.ViewHolder(bindingFav.root) {
        val cardView = bindingFav.cardViewRowFavorite

        fun bind(favoritesEntity: FavoritesEntity) {
            bindingFav.favoriteRecipe = favoritesEntity
            bindingFav.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val binding = FavoriteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        rootViewHolder = holder.itemView.rootView
        myViewHolders.add(holder)
        val recipe = favoritesList[position]
        holder.bind(recipe)
        // Click Listener (navigates to Details Activity)
        holder.itemView.setOnClickListener {
            if (multipleSelection) {
                applySelection(holder, recipe)
            }
            else {
                val action = FavRecipesFragmentDirections
                    .actionNavFavRecipesFragmentToNavDetailsActivity(recipe.result)
                holder.itemView.findNavController().navigate(action)
            }
        }
        // Long click Listener
        holder.itemView.setOnLongClickListener {
            if (!multipleSelection) {
                multipleSelection = true
                fragActivity.startActionMode(this)
                applySelection(holder, recipe)
                true
            }
            else {
                applySelection(holder, recipe)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }

    fun setDataToAdapter(newFavoritesList: List<FavoritesEntity>) {
        val adapterDiffUtil = AdapterDiffUtil(favoritesList, newFavoritesList)
        val diffResult = DiffUtil.calculateDiff(adapterDiffUtil)
        favoritesList = newFavoritesList
        diffResult.dispatchUpdatesTo(this)
    }

    /** Metode yang sesuai dengan "ActionMode.Callback"." */
    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favs_contextual_menu, menu)
        myActionMode = actionMode!!
        changeActionBarColor(R.color.secondary_color)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menuItem: MenuItem?): Boolean {
        if (menuItem?.itemId == R.id.action_delete_fav) {
            recipesToRemove.addAll(selectedRecipes)
            deleteRecipeDialog(recipesToRemove)
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach {
            changeItemStyle(it, R.color.medium_gray)
        }
        multipleSelection = false
        selectedRecipes.clear()
        changeActionBarColor(R.color.viewpage_unselected)
    }

    private fun applySelection(holder: MyViewHolder, recipe: FavoritesEntity) {
        if (selectedRecipes.contains(recipe)) {
            selectedRecipes.remove(recipe)
            changeItemStyle(holder, R.color.medium_gray)
            applyActionModeTitle()
        }
        else {
            selectedRecipes.add(recipe)
            changeItemStyle(holder, R.color.primary_color)
            applyActionModeTitle()
        }
    }

    private fun applyActionModeTitle() {
        when(selectedRecipes.size) {
            0 -> {
                myActionMode.finish()
                multipleSelection = false
            }
            1 -> {
                myActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                myActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    // Mengubah warna pinggiran CardView.
    private fun changeItemStyle(holder: MyViewHolder, strokeColor: Int) {
        holder.cardView.strokeColor = ContextCompat.getColor(fragActivity, strokeColor)
        holder.cardView.strokeWidth = 4
        holder.cardView.elevation = 6.0f
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(rootViewHolder, message, Snackbar.LENGTH_SHORT).setAction("Ok") {}.show()
    }

    // Fungsi yang membersihkan ActionMode ketika kita beralih antara tab Fragment.
    fun clearActionMode() {
        if (this::myActionMode.isInitialized) {
            myActionMode.finish()
        }
    }

    private fun deleteRecipeDialog(recipes: ArrayList<FavoritesEntity>){
        val builder = AlertDialog.Builder(fragActivity)
        builder.setTitle("Confirm delete")
        builder.setMessage("Are you sure to delete the selected item(s)?")
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Yes") {
                dialogInterface, _ ->
            removeSelectedRecipes(recipes)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") {
                dialogInterface, _ ->
            confirmRemove = false
            dialogInterface.dismiss()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.decorView?.setBackgroundResource(R.drawable.windows_dialog_bg)
        dialog.show()
    }

    private fun removeSelectedRecipes(recipes: ArrayList<FavoritesEntity>) {
        recipes.forEach {
            mainViewModel.deleteFavorite(it)
        }
        showSnackBar("Removed ${recipesToRemove.size} recipe(s)")
        multipleSelection = false
        selectedRecipes.clear()
        recipesToRemove.clear()
    }

    /** Fungsi untuk mengubah warna ActionBar saat menampilkan menu kontekstual.*/
    private fun changeActionBarColor(color: Int) {
        fragActivity.window.statusBarColor = ContextCompat.getColor(fragActivity, color)
    }

}