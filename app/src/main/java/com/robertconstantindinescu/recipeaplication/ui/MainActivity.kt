package com.robertconstantindinescu.recipeaplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.robertconstantindinescu.recipeaplication.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Create the navigation
         */
        val navController = findNavController(R.id.navHostFragment) //este va a buscar un controlador de navegacion en ese navhost que es el que tiene my_nav
        //configure the three main fragments to be navgated.We have to
        //indicate the set of destinations by their id.
        val appBarConfiguration = AppBarConfiguration( //esto es una funcion publica que hereda de appbarconfig
            setOf(
                R.id.recipesFragment,
                R.id.favoritesRecipesFragment,
                R.id.foodJokeFragment
            )
        )

        //Add a navcontroller to the bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
        //setup the navigation bar for all the fragments. I mean
        //we will have all the fragments with the same bottom nav.
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {

        //to navigate betwen fragments
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}