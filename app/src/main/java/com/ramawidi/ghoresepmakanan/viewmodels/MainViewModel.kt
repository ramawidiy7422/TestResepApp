package com.ramawidi.ghoresepmakanan.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ramawidi.ghoresepmakanan.data.database.FavoritesEntity
import com.ramawidi.ghoresepmakanan.data.database.FoodJokeEntity
import com.ramawidi.ghoresepmakanan.data.database.RecipesEntity
import com.ramawidi.ghoresepmakanan.data.models.FoodJoke
import com.ramawidi.ghoresepmakanan.data.models.FoodRecipe
import com.ramawidi.ghoresepmakanan.data.repositories.Repository
import com.ramawidi.ghoresepmakanan.data.utils.NetworkStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val repository: Repository,
                                         application: Application): AndroidViewModel(application) {

    /** Retrofit */
    val recipesResponse: MutableLiveData<NetworkStates<FoodRecipe>> = MutableLiveData()
    val recipesLiveData: LiveData<NetworkStates<FoodRecipe>> get() = recipesResponse
    private val searchRecipesResponse: MutableLiveData<NetworkStates<FoodRecipe>> = MutableLiveData()
    val searchRecipeLiveData: LiveData<NetworkStates<FoodRecipe>> get() = searchRecipesResponse
    private val foodJokeResponse: MutableLiveData<NetworkStates<FoodJoke>> = MutableLiveData()
    val fooJokeLiveData: LiveData<NetworkStates<FoodJoke>> get() = foodJokeResponse

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.postValue(NetworkStates.Loading())
        if (verifyInternetConnection()) {
            try {
                val response = repository.remoteData.getRecipes(queries)
                Log.d("MainViewModel", "Response: $response")
                recipesResponse.postValue(handleResponse(response))
                val foodRecipe = response.body()
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            }
            catch (e: Exception) {
                recipesResponse.postValue(NetworkStates.Error("Response exception: ${e.message}"))
            }
        }
        else {
            recipesResponse.postValue(NetworkStates.Error("No Internet connection"))
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.postValue(NetworkStates.Loading())
        if (verifyInternetConnection()) {
            try {
                val response = repository.remoteData.searchRecipes(searchQuery)
                Log.d("MainViewModel", "Response: $response")
                searchRecipesResponse.postValue(handleResponse(response))
            }
            catch (e: Exception) {
                searchRecipesResponse.postValue(NetworkStates.Error("Response exception: ${e.message}"))
            }
        }
        else {
            searchRecipesResponse.postValue(NetworkStates.Error("No Internet connection"))
        }
    }

    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.postValue(NetworkStates.Loading())
        if (verifyInternetConnection()) {
            try {
                val response = repository.remoteData.getFoodJoke(apiKey)
                Log.d("MainViewModel", "Response: $response")
                foodJokeResponse.postValue(handleJokeResponse(response))
                val foodJoke = response.body()
                if (foodJoke != null) {
                    offlineCacheFoodJoke(foodJoke)
                }
            }
            catch (e: Exception) {
                foodJokeResponse.postValue(NetworkStates.Error("Response exception: ${e.message}"))
            }
        }
        else {
            foodJokeResponse.postValue(NetworkStates.Error("No Internet connection"))
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }

    private fun handleResponse(response: Response<FoodRecipe>): NetworkStates<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkStates.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkStates.Error("API key limited")
            }
            response.body()!!.resultsRecipe.isNullOrEmpty() -> {
                return NetworkStates.Error("Recipes not found")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkStates.Success(foodRecipes!!)
            }
            else -> {
                return NetworkStates.Error(response.message())
            }
        }
    }

    private fun handleJokeResponse(response: Response<FoodJoke>): NetworkStates<FoodJoke>? {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkStates.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkStates.Error("API key limited")
            }
            response.isSuccessful -> {
                val joke = response.body()
                NetworkStates.Success(joke!!)
            }
            else -> {
                NetworkStates.Error(response.message())
            }
        }
    }

    private fun verifyInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    /** Room Database */
    val readRecipes: LiveData<List<RecipesEntity>> = repository.localData.readRecipes().asLiveData()
    val readFavorites: LiveData<List<FavoritesEntity>> = repository.localData.readFavorites().asLiveData()
    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.localData.readFoodJoke().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localData.insertRecipes(recipesEntity)
        }
    }

    fun insertFavorite(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localData.insertFavorite(favoritesEntity)
        }
    }

    fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localData.insertFoodJoke(foodJokeEntity)
        }
    }

    fun deleteFavorite(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localData.deleteFavorite(favoritesEntity)
        }
    }

    fun deleteAllFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localData.deleteAllFavorites()
        }
    }

}