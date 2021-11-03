package com.robertconstantindinescu.recipeaplication.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_over_view.view.*
import org.jsoup.Jsoup


class OverViewFragment : Fragment() {


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
        /**We will need this view reference*/
        val view = inflater.inflate(R.layout.fragment_over_view, container, false)
        /**
         * Create a args val to get the argumetns from the bundle
         * arguments is a tyoe of bundle which holds the data from our recipe
         */
        val args = arguments
        //get the data from the bundle using the key. Mi bundle que sera de tipo Result pq es la clase que me viene
        //y le decimos que obtengamos esos datos del bundle con la clave definida
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        //accedemos al iamge view del fragmnt
        view.main_imageView.load(myBundle?.image)
        view.title_textView.text = myBundle?.title
        view.likes_textView.text = myBundle?.aggregateLikes.toString()
        view.time_textView.text = myBundle?.readyInMinutes.toString()
        //view.summary_textView.text = myBundle?.summary
        myBundle?.summary.let {
            var summary = Jsoup.parse(it).text()
            view.summary_textView.text = summary
        }

        if(myBundle?.vegetarian == true){
            view.vegetarian_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.vegetarian_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        }

        if(myBundle?.vegan == true){
            view.vegan_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.vegan_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        }

        if(myBundle?.glutenFree == true){
            view.gluten_free_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.gluten_free_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        }

        if(myBundle?.dairyFree == true){
            view.dairy_free_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.dairy_free_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        }

        if(myBundle?.veryHealthy == true){
            view.healthy_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.healthy_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        }


        return  view
    }

}