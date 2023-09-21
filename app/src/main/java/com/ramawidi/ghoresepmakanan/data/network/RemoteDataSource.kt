package com.ramawidi.ghoresepmakanan.data.network

import com.ramawidi.ghoresepmakanan.data.models.FoodJoke
import com.ramawidi.ghoresepmakanan.data.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val recipeApi: ServiceApi) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return recipeApi.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return recipeApi.searchRecipes(searchQuery)
    }

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return recipeApi.getFoodJoke(apiKey)
    }

}