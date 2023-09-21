package com.ramawidi.ghoresepmakanan.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.ramawidi.ghoresepmakanan.databinding.FragmentBottomSheetBinding
import com.ramawidi.ghoresepmakanan.view.activities.MainActivity
import com.ramawidi.ghoresepmakanan.viewmodels.RecipesViewModel
import java.lang.Exception

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _bindingBottomSheet: FragmentBottomSheetBinding? = null
    private val bindingBottomSheet get() = _bindingBottomSheet!!
    private lateinit var recipesViewModel: RecipesViewModel
    private var chipMealType = DEFAULT_MEAL_TYPE
    private var chipMealTypeId = 0
    private var chipDietType = DEFAULT_DIET_TYPE
    private var chipDietTypeId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _bindingBottomSheet = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return bindingBottomSheet.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipesViewModel = (activity as MainActivity).recipesViewModel

        recipesViewModel.readMealAndDietTypes.asLiveData().observe(viewLifecycleOwner) {
                value ->
            chipMealType = value.selectedMealType
            chipDietType = value.selectedDietType
            updateChipGroup(value.selectedMealTypeId, bindingBottomSheet.cgMealType)
            updateChipGroup(value.selectedDietTypeId, bindingBottomSheet.cgDietType)
        }

        bindingBottomSheet.cgMealType.setOnCheckedChangeListener {
                group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedMealType = chip.text.toString().lowercase()
            chipMealType = selectedMealType
            chipMealTypeId = checkedId

        }

        bindingBottomSheet.cgDietType.setOnCheckedChangeListener {
                group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedDietType = chip.text.toString().lowercase()
            chipDietType = selectedDietType
            chipDietTypeId = checkedId
        }

        bindingBottomSheet.btnApplySheet.setOnClickListener {
            recipesViewModel.saveMealAndDietType(chipMealType, chipMealTypeId, chipDietType, chipDietTypeId)
            //Kami menetapkan nilai "true" ke argumen yang dibuat dalam RecipesFragment: backFromBottomSheet.
            val action = BottomSheetFragmentDirections.actionBottomSheetFragmentToNavRecipesFragment(true)
            findNavController().navigate(action)
        }

    }

    private fun updateChipGroup(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            }
            catch (e: Exception) {
                Log.d("BottomSheetFragment", "Exception: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // For avoid memory leaks
        _bindingBottomSheet = null
    }

}