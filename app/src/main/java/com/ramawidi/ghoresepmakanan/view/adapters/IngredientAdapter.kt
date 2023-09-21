package com.ramawidi.ghoresepmakanan.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.models.ExtendedIngredient
import com.ramawidi.ghoresepmakanan.data.utils.AdapterDiffUtil
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.BASE_IMAGE_URL
import com.ramawidi.ghoresepmakanan.databinding.IngredientItemLayoutBinding

class IngredientAdapter: RecyclerView.Adapter<IngredientAdapter.MyViewHolder>() {
    private var ingredientsList = emptyList<ExtendedIngredient>()

    class MyViewHolder(private val binding: IngredientItemLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExtendedIngredient) {
            val url = BASE_IMAGE_URL + item.image
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(binding.ivIngredientItem.context).load(url).placeholder(R.drawable.image_unavailable)
                .transition(DrawableTransitionOptions.withCrossFade(factory)).into(binding.ivIngredientItem)
            binding.tvIngredientName.text = item.name.capitalize()
            binding.tvIngredientAmount.text = item.amount.toString()
            binding.tvIngredientUnit.text = item.unit
            binding.tvIngredientCons.text = item.consistency
            binding.tvIngredientOriginal.text = item.original
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            IngredientItemLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = ingredientsList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun setIngredientsData(newIngredients: List<ExtendedIngredient>) {
        val adapterDiffUtil = AdapterDiffUtil(ingredientsList, newIngredients)
        val diffResult = DiffUtil.calculateDiff(adapterDiffUtil)
        ingredientsList = newIngredients
        diffResult.dispatchUpdatesTo(this)
    }

}