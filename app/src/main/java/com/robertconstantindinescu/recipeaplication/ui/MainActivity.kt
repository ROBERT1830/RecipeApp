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

    /*Al haber utilizado navigation Components, para realizar la navegación entre las diferentes
    * pantallas, usaremos un objeto NavController que gestiona la navegación*/
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /**Cuando iniciamos la aplicación, vamos a realizar directamente la navegación hascia el
         * navHostFragment. En este caso, este es a partir del cual se realizará todas las demás
         * navegaciones por los diferentes fragmentos.*/
        val navController = findNavController(R.id.navHostFragment) //este va a buscar un controlador de navegacion en ese navhost que es el que tiene my_nav


        /**Configuramos los tres fragmentos por los qeu se va a navegar.
         * Con lo cual configuramos la appBar con los tres fragmetnos.
         * Indicamos los destinos a esos fragmentos por su id. */
        val appBarConfiguration = AppBarConfiguration( //esto es una funcion publica que hereda de appbarconfig
            setOf(
                R.id.recipesFragment,
                R.id.favoritesRecipesFragment,
                R.id.foodJokeFragment
            )
        )

        /**Vamos a añadir a la barra de navegación del fondo de la pantalla
         * el controlador de la navegación. para poder navegar desde ella. */
        bottomNavigationView.setupWithNavController(navController)

        /**Ahora seteamos la barra de navegación con todos los fragmentos creados. */
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Método para poder navegar entre los fragmentos. De modo que cuando pasemos de un fragmento a otro
     * tendremos una flecha arriba y podremos volver all fragment inicial.
     */
    override fun onSupportNavigateUp(): Boolean {

        //to navigate betwen fragments
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}