package com.ramawidi.ghoresepmakanan.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ramawidi.ghoresepmakanan.data.models.FoodRecipe
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity (var foodRecipe: FoodRecipe) {
    @PrimaryKey(autoGenerate = false) // Ditempatkan sebagai 'false' karena data akan ditimpa.
    var id: Int = 0
}
