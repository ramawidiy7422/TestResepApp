package com.ramawidi.ghoresepmakanan.data.utils

class Constants {

    companion object {
        // Webpage: https://spoonacular.com/food-api/docs
        const val BASE_URL = "https://api.spoonacular.com/"
        // API KEY from Spoonacular
        const val API_KEY: String = "418976d98d0d46a6aec60b8a76eb1389"
        // Link base for Images
        const val BASE_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/"

        // Queries for API
        const val QUERY_NUMBER = "number"
        const val QUERY_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_REC_INFORMATION = "addRecipeInformation"
        const val QUERY_INGREDIENTS = "fillIngredients"
        const val QUERY_SEARCH = "query"

        // Room database
        const val DATABASE_NAME = "recipes_database"
        const val RECIPES_TABLE = "recipes_table"
        const val FAVORITES_TABLE = "favorites_table"
        const val FOOD_JOKE_TABLE = "food_joke_table"

        // Sheet Preferences
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"

        // DataStore Preferences Keys
        const val APP_DATASTORE = "FoodRecipesDatastore"
        const val DATASTORE_PREFS_MEAL_TYPE = "mealType"
        const val DATASTORE_PREFS_MEAL_TYPE_ID = "mealTypeId"
        const val DATASTORE_PREFS_DIET_TYPE = "dietType"
        const val DATASTORE_PREFS_DIET_TYPE_ID = "dietTypeId"
        const val DATASTORE_PREFS_BACK_ONLINE = "backOnlineState"

        // Arguments key to Parcel
        const val ARGS_KEY = "RecipeBundle"
    }

}