package com.ramawidi.ghoresepmakanan.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramawidi.ghoresepmakanan.data.models.FoodRecipe
import com.ramawidi.ghoresepmakanan.data.models.ResultRecipe
import com.ramawidi.ghoresepmakanan.data.utils.AdapterDiffUtil
import com.ramawidi.ghoresepmakanan.databinding.RecipeItemLayoutBinding

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {
    private var recipeList = emptyList<ResultRecipe>()

    class MyViewHolder(private val bindingAdapter: RecipeItemLayoutBinding):
        RecyclerView.ViewHolder(bindingAdapter.root) {

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipeItemLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

        fun bind(resultRecipe: ResultRecipe) {
            bindingAdapter.result = resultRecipe
            // Updates the layout whenever there is any change inside our Data
            bindingAdapter.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipeList[position]
        // For update the RecyclerView every time that we receive new data from API
        holder.bind(currentRecipe)
    }

    fun setDataToAdapter(newRecipes: FoodRecipe) {
        val recipesDifference = AdapterDiffUtil(recipeList, newRecipes.resultsRecipe)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDifference)
        recipeList = newRecipes.resultsRecipe
        // Update only those items that are not the same (increase performance on RecyclerView/Application)
        diffUtilResult.dispatchUpdatesTo(this)
    }

}