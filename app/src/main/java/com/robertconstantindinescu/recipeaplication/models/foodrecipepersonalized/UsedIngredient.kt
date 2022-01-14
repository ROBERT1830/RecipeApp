package com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Data class que contiene informacion acerca de los ingredientes
 */
@Parcelize
data class UsedIngredient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("original")
    val original: String,
):Parcelable
