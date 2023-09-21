package com.ramawidi.ghoresepmakanan.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramawidi.ghoresepmakanan.data.models.FoodRecipe
import com.ramawidi.ghoresepmakanan.data.models.ResultRecipe

class TypeConverterForDB {
    var gson = Gson()

    // For table: Recipes
    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }

    // For table: Favorites
    @TypeConverter
    fun resultRecipeToString(resultRecipe: ResultRecipe): String {
        return gson.toJson(resultRecipe)
    }

    @TypeConverter
    fun stringToResultRecipe(data: String): ResultRecipe {
        val listType = object : TypeToken<ResultRecipe>() {}.type
        return gson.fromJson(data, listType)
    }

}