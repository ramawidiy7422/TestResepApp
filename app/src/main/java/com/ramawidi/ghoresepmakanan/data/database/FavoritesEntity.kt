package com.ramawidi.ghoresepmakanan.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ramawidi.ghoresepmakanan.data.models.ResultRecipe
import com.ramawidi.ghoresepmakanan.data.utils.Constants.Companion.FAVORITES_TABLE

@Entity(tableName = FAVORITES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: ResultRecipe
)