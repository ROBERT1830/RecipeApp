package com.robertconstantindinescu.recipeaplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.recipeaplication.databinding.RecipesRowLayoutBinding
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    //nos declaramos una variable que tendra una lista de los objetos result
    private var recipes = emptyList<Result>()


    fun setData(newData: FoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult  = DiffUtil.calculateDiff(recipesDiffUtil)

        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)

    }

    override fun getItemCount(): Int {
        return  recipes.size
    }

    /**
     * Because we used daba Binding here in the viewholder we need to add in the constructor the
     * binding and the type is the name of the layput + Binding. This class is generated when you convert
     * the layout to databinding
     * And we are going to use this binding to actually bind our Api data with these recipes row layout
     */
     class MyViewHolder(private val binding: RecipesRowLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {


        fun bind(result: Result){
            binding.result = result
            binding.executePendingBindings()

        }


        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context) //preparas el recylcer para aceptar el inlflado
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false) //inflas el layout
                return MyViewHolder(binding)
            }
        }

    }


}