package com.robertconstantindinescu.recipeaplication.bindingadapters

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipeItem
import com.robertconstantindinescu.recipeaplication.ui.fragments.personalizedRcipe.PersonalizedFoodRecipeFragment
import com.robertconstantindinescu.recipeaplication.ui.fragments.personalizedRcipe.PersonalizedFoodRecipeFragmentDirections
import com.robertconstantindinescu.recipeaplication.ui.fragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup
import java.lang.Exception


/**
 * Esta clase se utiliza para el databinding con los layouts.
 */
class RecipesRowBinding {
    //create a companaion object to acces the data from everywhere even from the layout
    companion object {
        /**
         *
         * @JvmStatic--- con esta anotacion le indicamos al compilador que haga la función estática
         * para que pueda ser accedida desde cualquier lugar.
         */
        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: Result) {
            recipeRowLayout.setOnClickListener {
                try {
                    //here we store int the action the result
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                    //haces la navegacion dede cualquier punto del contraintLayout usando la action que hemos definido
                    recipeRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d("onRecipeClickListener", e.toString())
                }
            }
        }

        /**
         * Función que gestiona la navegación cuadno clicamos un elemntos del recycler. La navegación
         * se realiza al fragmento que muestra los detalles.
         * El primer parametros es el padre, un LinearLayout y el objeto personalizedFoodRecipeItem
         */
        @BindingAdapter("onPersonalizedRecipeClickListener")
        @JvmStatic
        fun onPersonalizedRecipeClickListener(
            personalizedRecipeLayout: LinearLayout,
            personalizedFoodRecipeItem: PersonalizedFoodRecipeItem
        ) {

            personalizedRecipeLayout.setOnClickListener {
                try {
                    val action = PersonalizedFoodRecipeFragmentDirections.actionFoodJokeFragmentToPersonalizedRecipeDetails2(personalizedFoodRecipeItem)
                    personalizedRecipeLayout.findNavController().navigate(action)

                }catch (e: Exception){

                }

            }



        }

        /**
         * Función que setea el número de likes en el textview determinado. Para ello pasamos
         * desde el layout como argumentos el texview y el numero de likes que si nos vamos
         * al layout determiando veremos que pasamos el numero de likes del objeto.
         * Simplemente con esos datos, asignamos el número con el texto.
         */
        @BindingAdapter("setNumberOfLikes")
        @JvmStatic
        fun setNumberOfLikes(textView: TextView, likes: Int) {
            textView.text = likes.toString()
        }

        /**
         * ídem
         */
        @BindingAdapter("setNumberOfMinutes")
        @JvmStatic
        fun setNumberOfMinutes(textView: TextView, minutes: Int) {
            textView.text = minutes.toString()
        }

        /**
         * En este caso usaremos este método para cambiar el color de dos vistas, un textview y un
         * imageview. Por eso se utiliza view y no se especifica una vista determinada.
         * Si resulta que el estado de la propiedad vegan del objeto que estamos pasando, es true
         * entonces determinamos con un when el tipo de vista y asignamos el color verde.
         * De lo contrario lo dejamos por defecto.
         */
        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        //set the text color. the constext needed is getting from the view.
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }
        /**
         * Este método va a realizar la carga de la iamgen que pasamos desde el layout.
         * Para ello usamos la libreria coil a la que le pasamos la url y nos genera la imagen.
         * en caso contrario nos pone un icono de error.
         * Además nos ofrece opciones de diseño como crossfade.
         */
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            //Use coil library to get the image and display it inside the imageview.
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder) //we use this to display a placeholder whenever the iamgen is not fetched from database or api
                //only those images that were catched correctluy when there were internet conection will display correctly and the other no
            }
        }
        /**
         * Este método realiza el parseo del HTML que obtenemos. Cuando obtenemos un texto del servidor,
         * resulta que se nos muestran las etiquetas html. Con la libreria Jsoup parseamos la descripcion
         * y generamos un texto normal sin  etiquetas.
         */

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?) {
            if (description != null) {
                //parse html tags
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }
    }
}