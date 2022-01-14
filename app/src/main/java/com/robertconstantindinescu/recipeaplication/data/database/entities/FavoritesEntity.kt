package com.robertconstantindinescu.recipeaplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.FAVORITE_RECIPES_TABLE

/**
 * ÍDEM
 */
@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result,
    var name: String,
    var readyInMinutes: Int,
    var aggregateLikes: Int
)
