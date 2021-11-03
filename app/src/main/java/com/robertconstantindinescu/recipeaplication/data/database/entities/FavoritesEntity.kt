package com.robertconstantindinescu.recipeaplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)
