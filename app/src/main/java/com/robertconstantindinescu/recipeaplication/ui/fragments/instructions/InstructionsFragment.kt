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

        //Obtenemos los argumentos
        val args = arguments
        //obtenemos los datos de esos argumetnos usando la clave
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        //Nos cremoa una clase anónima de WebViewClient para mostrar la web usadno la url que
        //nos viene por bunle.
        view.instructions_webView.webViewClient = object : WebViewClient(){}
        val websiteUrl: String = myBundle!!.sourceUrl!!
        view.instructions_webView.loadUrl(websiteUrl)
        return view
    }


}