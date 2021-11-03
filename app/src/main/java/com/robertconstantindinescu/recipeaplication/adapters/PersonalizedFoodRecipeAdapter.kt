package com.robertconstantindinescu.recipeaplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.recipeaplication.databinding.FragmentPersonalizedRecipeBinding
import com.robertconstantindinescu.recipeaplication.databinding.PersonalizedRecipeRowBinding
import com.robertconstantindinescu.recipeaplication.databinding.RecipesRowLayoutBinding
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipeItem
import com.robertconstantindinescu.recipeaplication.util.RecipesDiffUtil

class PersonalizedFoodRecipeAdapter :
    RecyclerView.Adapter<PersonalizedFoodRecipeAdapter.MyViewHolder>() {
    private var personalizedRecipes = emptyList<PersonalizedFoodRecipeItem>()

    fun setData(newData: PersonalizedFoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(personalizedRecipes, newData/*.results*/)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)

        personalizedRecipes = newData/*.results*/
        diffUtilResult.dispatchUpdatesTo(this)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent) //el padre que alberga los hijos.
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPersonalizedFoodRecipeItem = personalizedRecipes[position]
        holder.bind(currentPersonalizedFoodRecipeItem)

    }

    override fun getItemCount(): Int {

        return personalizedRecipes.size

    }

     class MyViewHolder(private val binding: PersonalizedRecipeRowBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bind(personalizedRecipeItem: PersonalizedFoodRecipeItem){
                binding.personalizedFoodRecipeItem = personalizedRecipeItem
                binding.executePendingBindings()
            }

        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PersonalizedRecipeRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }



    }
}
