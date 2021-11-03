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

        /**
         * The first thing to do is to get th e arguments from the pager adapter
         * So when we open the fragmnt overview we se thsat we have val args and getting the parcelable from the pager adapater
         * or from our detailsActivity
         */

        val args = arguments
        //get the data from the bundle using the key. Mi bundle que sera de tipo Result pq es la clase que me viene
        //y le decimos que obtengamos esos datos del bundle con la clave definida
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setUpRecyclerView(view)
        /**
         * here we call the set data funciton from ingredients adapter so we can actually send a new data using
         * the bundle
         */
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }

        return view
    }

    private fun setUpRecyclerView(view: View){
        view.ingredients_recyclerview.adapter = mAdapter
        view.ingredients_recyclerview.layoutManager = LinearLayoutManager(requireContext())

    }


}