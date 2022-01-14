package com.robertconstantindinescu.recipeaplication.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.util.NetworkResult

class RecipesBinding {
    companion object{

        /**
         * requireAll = true : indica que se necesitan dos argumetnos desde el layout.
         * tanto la respuesta del api como la lectura de la base de datos. Esto es porque
         * debemos de usar ambas para hacer ciertas comprobaciones que se indican más abajo.
         */
        @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)

        /**
         * Método que va a modificar la visibilidad de la imagen de fondo en el recylcerview de
         * RecipesFragment. En función del estado de la respuesta que obtenemos del servidor,
         * modificaremos la visibiliadad de la iamgen. También tenemos en cuenta el estado de la
         * base de datos. Cuano tenemos una respuesta de error y la base de datos está vacía
         * mostraremos la imagen. en los casos restantes no la mostramos.
         */
        @JvmStatic
        fun errorImageViewVisibility(
            imageView: ImageView,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ){
            //cuando hay error en el api y en la base de dato este facia mostramos l aiamgen triste. Con esto modificamos la visibiliadd de la imagen
            if(apiResponse is NetworkResult.Error && database.isNullOrEmpty()){
                imageView.visibility = View.VISIBLE
            }else if(apiResponse is NetworkResult.Loading){
                imageView.visibility = View.INVISIBLE
            }else if (apiResponse is NetworkResult.Succes){
                imageView.visibility = View.INVISIBLE
            }
        }

        /**
         * Idem pero para el texto de fondo cuadno no hay datos.
         */
        @BindingAdapter("readApiResponse2", "readDatabase2", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibility(
            textView: TextView,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ){
            if(apiResponse is NetworkResult.Error && database.isNullOrEmpty()){
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString()
            }else if(apiResponse is NetworkResult.Loading){
                textView.visibility = View.INVISIBLE
            }else if (apiResponse is NetworkResult.Succes){
                textView.visibility = View.INVISIBLE
            }
        }
    }
}