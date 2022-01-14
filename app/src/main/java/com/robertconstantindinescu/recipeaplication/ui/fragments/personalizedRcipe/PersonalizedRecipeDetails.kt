package com.robertconstantindinescu.recipeaplication.ui.fragments.personalizedRcipe

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import coil.load
import com.bumptech.glide.Glide
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.databinding.ActivityPersonalizedRecipeDetailsBinding

import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipeItem
import com.robertconstantindinescu.recipeaplication.ui.DetailsActivityArgs
import com.robertconstantindinescu.recipeaplication.util.Constants
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.BASE_IMAGE_URL
import com.robertconstantindinescu.recipeaplication.util.NetworkResult
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import com.robertconstantindinescu.recipeaplication.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.ingredients_row_layout.view.*

@AndroidEntryPoint
class PersonalizedRecipeDetails : AppCompatActivity() {



    private lateinit var mBinding: ActivityPersonalizedRecipeDetailsBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val  recipesViewModel: RecipesViewModel by viewModels()

    /**Argumentos que se pasan desde PersonalizedFoodRecipeFragment al realizar la
     * petición y hacer click sobre la receta del recyclerView. */
    private val args by navArgs<PersonalizedRecipeDetailsArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPersonalizedRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //nos guardamos en una variable el id de la receta elegida.
        val personalizedRecipeId = args.personalizedFoodRecipeItem.id
        //hacemos la petición al servidor de los detalles de la receta con el id determinado.
        requestApiData(personalizedRecipeId)


    }

    /**
     * Este método realiza la peticion al servidor utilizando la query devuelta por el
     * método applyQueryById. Al llamar a getRecipesById se efectuara una petición al servidor
     * y la variable livedata recipeResponseById se actulizara. Desde aquí, se observa ese cambio
     * y en función del tipo de respuesta realizaremos una acción u otra.
     * En este caso cuadno la respuesta sea de tipo Succes, accederemos a los datos y si no son
     * nulos vamos a cargar la imagen correspondiente, el título y una descripción.
     */
    private fun requestApiData( personalizedRecipeId: Int){
        Log.d("RecipesFragment", "requestApiData called!")

        mainViewModel.getRecipesById(personalizedRecipeId ,recipesViewModel.applyQueryById()) //--> use the RecipeViewModel eich contains the endpoint queries. We use thar for not havving the applyquery function here and make it clenear.
        mainViewModel.recipeResponseById.observe(this, Observer { response ->
            when(response){
                is NetworkResult.Succes ->{
                    response.data?.let {


                        Glide.with(this)
                            .load(it.image)
                            .centerCrop()
                            .into(mBinding.mainImageView)
                        mBinding.titleTextView.text = it.title
                        mBinding.summaryTextView.text = it.summary

                    }
                }
            }
            Unit
        })

    }

    /**
     * Método encargado de terminar la activity.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish() //close details activity

        }

        return super.onOptionsItemSelected(item)
    }





}