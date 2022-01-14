package com.robertconstantindinescu.recipeaplication.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Data class que nos mapea los datos realcionado con los ingredientes de cada una de las
 * recetas.
 */
@Parcelize
data class ExtendedIngredient(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("consistency")
    val consistency: String?,

    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("original")
    val original: String?,
    @SerializedName("unit")
    val unit: String?
): Parcelable