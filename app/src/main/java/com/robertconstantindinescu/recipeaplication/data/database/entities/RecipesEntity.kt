package com.robertconstantindinescu.recipeaplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.RECIPES_TABLE


/**
 * Esta clase la marcamos con @Entity para inicarle a room que esta clase y sus propiedades contituirá
 * una tabla en la base de datos.
 */
@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipe) {
    //autoGenerate = false porque queremos tener una única columna con los datos actulizados.
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}