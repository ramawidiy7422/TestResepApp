package com.ramawidi.ghoresepmakanan.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ramawidi.ghoresepmakanan.data.repositories.DataStoreRepository
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.API_KEY
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_DIET
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_INGREDIENTS
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_KEY
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_NUMBER
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_REC_INFORMATION
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_SEARCH
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor (application: Application,
                                            private val dataStoreRepository: DataStoreRepository): AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE
    val readMealAndDietTypes = dataStoreRepository.readMealAndDietType
    //var networkStatus = false
    var networkStatus: Boolean? = null
    var backOnlineState = false
    val readBackOnlineState = dataStoreRepository.readBackOnlineState.asLiveData()

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        viewModelScope.launch {
            // Use the suspend "collect" for Flow
            readMealAndDietTypes.collect {
                    value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        queries[QUERY_NUMBER] = "25"
        queries[QUERY_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_REC_INFORMATION] = "true"
        queries[QUERY_INGREDIENTS] = "true"
        return queries
    }

    fun applySearchQuery(search: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = search
        queries[QUERY_NUMBER] = "25"
        queries[QUERY_KEY] = API_KEY
        queries[QUERY_REC_INFORMATION] = "true"
        queries[QUERY_INGREDIENTS] = "true"
        return queries
    }

    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }
    }

    fun saveBackOnlineState(backOnline: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveOnlineState(backOnline)
    }

    fun checkNetworkStatus() {
        if (networkStatus == false) {
            Toast.makeText(getApplication(), "No Internet connection!", Toast.LENGTH_SHORT).show()
            saveBackOnlineState(true)
        }
        else {
            Toast.makeText(getApplication(), "Internet connection is available!", Toast.LENGTH_SHORT).show()
            saveBackOnlineState(false)
        }
    }

}