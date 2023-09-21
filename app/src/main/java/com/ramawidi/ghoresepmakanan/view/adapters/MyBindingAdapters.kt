package com.ramawidi.ghoresepmakanan.view.adapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.database.RecipesEntity
import com.ramawidi.ghoresepmakanan.data.models.FoodRecipe
import com.ramawidi.ghoresepmakanan.data.models.ResultRecipe
import com.ramawidi.ghoresepmakanan.data.utils.NetworkStates
import com.ramawidi.ghoresepmakanan.view.fragments.RecipesFragmentDirections
import org.jsoup.Jsoup
import java.lang.Exception

class MyBindingAdapters {

    companion object {

        /** BindingAdapters for XML: fragment_overview_page && fragment_recipes */

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtmlText(textView: TextView, description: String?) {
            if (description != null) {
                val parseText = Jsoup.parse(description).text()
                textView.text = parseText
            }
        }

        /** BindingAdapters for XML: fragment_recipes */

        @BindingAdapter("onClickListener")
        @JvmStatic
        fun recipeClickListener(rowLayout: ConstraintLayout, resultRecipe: ResultRecipe) {
            Log.d("RecipeClickListener", "Result: $resultRecipe")
            rowLayout.setOnClickListener {
                try {
                    val action = RecipesFragmentDirections.actionNavRecipesFragmentToNavDetailsActivity(resultRecipe)
                    rowLayout.findNavController().navigate(action)
                }
                catch (e: Exception) {
                    Log.d("RecipeClickListener", "Exception: ${e.message}")
                }
            }
        }

        @BindingAdapter("getApiResponse", "getDatabase", requireAll = true)
        @JvmStatic
        fun errorViewVisibility(imageView: ImageView, apiResponse: NetworkStates<FoodRecipe>?,
                                database: List<RecipesEntity>?) {
            if (apiResponse is NetworkStates.Error && database.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
            }
            else if (apiResponse is NetworkStates.Loading) {
                imageView.visibility = View.GONE
            }
            else if (apiResponse is NetworkStates.Success) {
                imageView.visibility = View.GONE
            }
        }

        @BindingAdapter("getApiResponse2", "getDatabase2", requireAll = true)
        @JvmStatic
        fun errorTextVisibility(textView: TextView, apiResponse: NetworkStates<FoodRecipe>?,
                                database: List<RecipesEntity>?) {
            if (apiResponse is NetworkStates.Error && database.isNullOrEmpty()) {
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString()
            }
            else if (apiResponse is NetworkStates.Loading) {
                textView.visibility = View.GONE
            }
            else if (apiResponse is NetworkStates.Success) {
                textView.visibility = View.GONE
            }
        }

        /** BindingAdapters for XML: recipe_item_layout */

        @BindingAdapter("loadRecipeImage")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(imageView.context).load(imageUrl).placeholder(R.drawable.image_unavailable)
                .transition(DrawableTransitionOptions.withCrossFade(factory)).centerCrop().into(imageView)
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun changeVeganColor(view: View, value: Boolean) {
            if (value) {
                when(view) {
                    is TextView -> {
                        view.setTextColor(ContextCompat.getColor(view.context, R.color.green))
                    }
                    is ImageView -> {
                        view.setColorFilter(ContextCompat.getColor(view.context, R.color.green))
                    }
                }
            }
        }

    }

}