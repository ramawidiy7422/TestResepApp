package com.ramawidi.ghoresepmakanan.view.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.ramawidi.ghoresepmakanan.R
import com.ramawidi.ghoresepmakanan.data.models.ResultRecipe
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.ARGS_KEY
import com.ramawidi.ghoresepmakanan.databinding.FragmentOverviewPageBinding
import org.jsoup.Jsoup


class OverviewPageFragment : Fragment() {
    private var _bindingOverview: FragmentOverviewPageBinding? = null
    private val bindingOverview get() = _bindingOverview!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _bindingOverview = FragmentOverviewPageBinding.inflate(inflater, container, false)
        return bindingOverview.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  membuat variabel untuk mendapatkan objek dari Arguments.
        val args = arguments
        //val myBundle: ResultRecipe? = args?.getParcelable("RecipeBundle") // DEPRECATED
        val myBundle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args?.getParcelable(ARGS_KEY, ResultRecipe::class.java)
        } else {
            args?.getParcelable(ARGS_KEY) as ResultRecipe?
        }
        // meneruskan data objek ke setiap komponen View.
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(requireContext()).load(myBundle?.image).placeholder(R.drawable.image_unavailable)
            .transition(DrawableTransitionOptions.withCrossFade(factory)).centerCrop().into(bindingOverview.ivOverview)
        bindingOverview.tvOverviewTitle.text = myBundle?.title
        bindingOverview.tvOverviewLikes.text = myBundle?.aggregateLikes.toString()
        bindingOverview.tvOverviewTime.text = myBundle?.readyInMinutes.toString()
        myBundle?.summary?.let {
            val summary = Jsoup.parse(it).text()
            bindingOverview.tvOverviewSummary.text = summary
        }
        if (myBundle?.vegetarian == true) {
            bindingOverview.ivOverviewVegetarian.setColorFilter(ContextCompat
                .getColor(requireContext(), R.color.green))
            bindingOverview.tvOverviewVegetarian.setTextColor(ContextCompat
                .getColor(requireContext(), R.color.green))
        }
        if (myBundle?.vegan == true) {
            bindingOverview.ivOverviewVegan.setColorFilter(ContextCompat
                .getColor(requireContext(), R.color.green))
            bindingOverview.tvOverviewVegan.setTextColor(ContextCompat
                .getColor(requireContext(), R.color.green))
        }
        if (myBundle?.glutenFree == true) {
            bindingOverview.ivOverviewGluten.setColorFilter(ContextCompat
                .getColor(requireContext(), R.color.green))
            bindingOverview.tvOverviewGluten.setTextColor(ContextCompat
                .getColor(requireContext(), R.color.green))
        }
        if (myBundle?.dairyFree == true) {
            bindingOverview.ivOverviewDairy.setColorFilter(ContextCompat
                .getColor(requireContext(), R.color.green))
            bindingOverview.tvOverviewDairy.setTextColor(ContextCompat
                .getColor(requireContext(), R.color.green))
        }
        if (myBundle?.veryHealthy == true) {
            bindingOverview.ivOverviewHealthy.setColorFilter(ContextCompat
                .getColor(requireContext(), R.color.green))
            bindingOverview.tvOverviewHealthy.setTextColor(ContextCompat
                .getColor(requireContext(), R.color.green))
        }
        if (myBundle?.cheap == true) {
            bindingOverview.ivOverviewCheap.setColorFilter(ContextCompat
                .getColor(requireContext(), R.color.green))
            bindingOverview.tvOverviewCheap.setTextColor(ContextCompat
                .getColor(requireContext(), R.color.green))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // For avoid memory leaks
        _bindingOverview = null
    }

}