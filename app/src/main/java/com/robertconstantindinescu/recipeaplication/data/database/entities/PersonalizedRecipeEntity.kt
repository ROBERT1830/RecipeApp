package com.robertconstantindinescu.recipeaplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import com.robertconstantindinescu.recipeaplication.ui.fragments.personalizedRcipe.PersonalizedFoodRecipeFragment
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PERSONALIZED_RECIPES_TABLE

/**
 * Otra clase marcada con la etiqueta @Entity.
 */
@Entity(tableName = PERSONALIZED_RECIPES_TABLE)
class PersonalizedRecipeEntity(var personalizedRecipe: PersonalizedFoodRecipe) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

}