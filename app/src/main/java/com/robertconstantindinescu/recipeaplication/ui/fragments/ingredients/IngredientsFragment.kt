package com.robertconstantindinescu.recipeaplication.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.adapters.IngredientsAdapter
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_ingredients.view.*


class IngredientsFragment : Fragment() {

    private val mAdapter: IngredientsAdapter by lazy {IngredientsAdapter()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)
        //cogemos los argumentos
        val args = arguments
        //obtenemos los datos con la clave
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)
        setUpRecyclerView(view)
        //Si resulta que tenemos datos en extendedIngredients, setearemos el adapter con los datos
        //de cada uno de los ingredientes.
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }

        return view
    }
    //seteamos el recyclerview.
    private fun setUpRecyclerView(view: View){
        view.ingredients_recyclerview.adapter = mAdapter
        view.ingredients_recyclerview.layoutManager = LinearLayoutManager(requireContext())

    }


}