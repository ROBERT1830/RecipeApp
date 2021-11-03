package com.robertconstantindinescu.recipeaplication.ui.fragments.instructions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.Constants
import kotlinx.android.synthetic.main.fragment_instructions.view.*


class InstructionsFragment : Fragment() {


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
        val view =  inflater.inflate(R.layout.fragment_instructions, container, false)

        /**
         * Get parcelable
         */
        val args = arguments
        //get the data from the bundle using the key. Mi bundle que sera de tipo Result pq es la clase que me viene
        //y le decimos que obtengamos esos datos del bundle con la clave definida
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        //te creas una clase anonima en este putno
        view.instructions_webView.webViewClient = object : WebViewClient(){}
        val websiteUrl: String = myBundle!!.sourceUrl!!
        view.instructions_webView.loadUrl(websiteUrl)


        return view
    }


}