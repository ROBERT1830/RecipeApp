package com.robertconstantindinescu.recipeaplication.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.recipeaplication.adapters.FavoriteRecipesAdapter
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity

/**
 * 21
 */
class FavoriteRecipesBinding {
    companion object {
        /**Here as firts aprameter we will have view as a afirst parameter becasue
         * we will use that with different views like image and text. so a fgeneric view will work
         *
         * viewVisibility refers to the favoritesEnttiry
         * setData to adapter*/
        @BindingAdapter("viewVisibility", "setData", requireAll = false) //no se requieren ambos datos para desencadenar la funcoin
        @JvmStatic
        fun setDataAndViewVisibility(
            view: View, //that means that this binding adapater wil be used in different viewws
            favoritesEntity: List<FavoritesEntity>?,
            mapAdapter: FavoriteRecipesAdapter?
        ){
            //check if the recipesFav table is empty and if there is that menasn tath we want to show
            //the background images and set the visitibily of the recyclar to invisible
            if (favoritesEntity.isNullOrEmpty()){
                when(view){
                    is ImageView -> {view.visibility = View.VISIBLE}
                    is TextView -> {view.visibility = View.VISIBLE}
                    is RecyclerView -> {view.visibility = View.INVISIBLE}
                }
            }else{ // if the favorite table is not empty show the recycler and pass the info to the adapter
                when(view){
                    is ImageView -> {view.visibility = View.INVISIBLE}
                    is TextView -> {view.visibility = View.INVISIBLE}
                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        mapAdapter?.setData(favoritesEntity)
                    }
                }
            }
        }
    }
}