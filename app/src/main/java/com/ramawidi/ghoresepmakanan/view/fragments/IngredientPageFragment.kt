package com.ramawidi.ghoresepmakanan.view.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramawidi.ghoresepmakanan.data.models.ResultRecipe
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.ARGS_KEY
import com.ramawidi.ghoresepmakanan.databinding.FragmentIngredientPageBinding
import com.ramawidi.ghoresepmakanan.view.adapters.IngredientAdapter


class IngredientPageFragment : Fragment() {
    private var _bindingIngredient: FragmentIngredientPageBinding? = null
    private val bindingIngredient get() = _bindingIngredient!!
    private val ingredientAdapter: IngredientAdapter by lazy { IngredientAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _bindingIngredient = FragmentIngredientPageBinding.inflate(inflater, container, false)
        return bindingIngredient.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val myBundle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args?.getParcelable(ARGS_KEY, ResultRecipe::class.java)
        }
        else {
            args?.getParcelable(ARGS_KEY) as ResultRecipe?
        }

        bindingIngredient.rvIngredients.adapter = ingredientAdapter
        bindingIngredient.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        myBundle?.extendedIngredients?.let {
                list ->
            ingredientAdapter.setIngredientsData(list)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // For avoid memory leaks
        _bindingIngredient = null
    }

}