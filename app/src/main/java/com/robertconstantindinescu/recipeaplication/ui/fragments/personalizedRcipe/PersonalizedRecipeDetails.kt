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


    private val args by navArgs<PersonalizedRecipeDetailsArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPersonalizedRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //setSupportActionBar(toolbar)
        //toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val personalizedRecipeId = args.personalizedFoodRecipeItem.id
        requestApiData(personalizedRecipeId)


    }




    private fun requestApiData( personalizedRecipeId: Int){
        Log.d("RecipesFragment", "requestApiData called!")

        mainViewModel.getRecipesById(personalizedRecipeId ,recipesViewModel.applyQueryById()) //--> use the RecipeViewModel eich contains the endpoint queries. We use thar for not havving the applyquery function here and make it clenear.
        mainViewModel.recipeResponseById.observe(this, Observer { response ->
            when(response){
                is NetworkResult.Succes ->{
                    //hide shimmer response becase we getdata

                    //get acces to the food recipe
                    //accedes a alos datos de NetworkResult que es una clase foodRecipe qeu tiene una lista de result y le pasas esa lsita al adapter.
                    response.data?.let {


                        Glide.with(this) //si no ponemos una inner clas no nos permite poner el context. Pq una inner clas lo qeu hace es permitirnos tambien usar los atributos del construcitr princiapl que es el MainAdapter.
                            .load(it.image)
                            .centerCrop()
                            .into(mBinding.mainImageView)
//                        mBinding.mainImageView.load(BASE_IMAGE_URL +  it.image)
//                        {
//                            crossfade(600)
//                            kotlin.error(R.drawable.ic_error_placeholder)
//                        }


                        mBinding.titleTextView.text = it.title
                        mBinding.summaryTextView.text = it.summary

                    }
                }



            }
            Unit
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish() //close details activity
            /**20---Favorites*/
        }

        return super.onOptionsItemSelected(item)
    }





}