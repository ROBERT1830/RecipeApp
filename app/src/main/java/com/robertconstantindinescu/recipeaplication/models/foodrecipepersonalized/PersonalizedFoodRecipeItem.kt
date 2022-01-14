package com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * data class que mapea los atributos de cada una de las recetas personalizadas.
 */
@Parcelize
data class PersonalizedFoodRecipeItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("usedIngredients")
    val usedIngredients: List<UsedIngredient>
): Parcelable
