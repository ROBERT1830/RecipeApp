package com.robertconstantindinescu.recipeaplication.adapters

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.databinding.FavoriteRecipesRowLayoutBinding
import com.robertconstantindinescu.recipeaplication.ui.fragments.favorites.FavoritesRecipesFragmentDirections
import com.robertconstantindinescu.recipeaplication.util.RecipesDiffUtil
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.favorite_recipes_row_layout.view.*

class FavoriteRecipesAdapter (
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
        ): RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback {

    private lateinit var mActionMode: ActionMode //we need that variable as global to can get out from contextual action mode dinamucally.  and now go to oncreateActionMode
    private lateinit var rootView: View

    private var multiSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()

    private var favoriteRecipes = emptyList<FavoritesEntity>()

    fun setData(newFavoriteRecipes: List<FavoritesEntity> ){
        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: FavoriteRecipesRowLayoutBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(favoritesEntity: FavoritesEntity){
                binding.favoritesEntity = favoritesEntity
                binding.executePendingBindings()
            }

        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)


        holder.itemView.favoriteRecipesRowLayout.setOnClickListener {
            if (multiSelection){
                applySelection(holder, currentRecipe)
            }else{
                val action =
                    FavoritesRecipesFragmentDirections.actionFavoritesRecipesFragmentToDetailsActivity(currentRecipe.result)
                holder.itemView.findNavController().navigate(action)
            }


        }
        holder.itemView.favoriteRecipesRowLayout.setOnLongClickListener {
            if (!multiSelection){
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentRecipe)
                true
            }else{
                multiSelection = false
                false
            }

        }


    }




    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoritesEntity){
        if(selectedRecipes.contains(currentRecipe)){
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        }else{
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun applyActionModeTitle(){
        when(selectedRecipes.size){
            0 -> {
                mActionMode.finish()
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"


            }
            //when we have more than one recipe selected
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }

        }
    }

    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int){
        holder.itemView.favoriteRecipesRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.itemView.favorite_row_cardView.strokeColor = ContextCompat.getColor(requireActivity, strokeColor)
    }






    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)

        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
       return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.delete_favorite_recipe_menu){
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")
            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)

    }

    private fun applyStatusBarColor(color:Int){
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)

    }

    private fun showSnackBar(message: String){
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).setAction("OK"){}.show()
    }

    fun clearContextualAcitonMode(){
        if(this::mActionMode.isInitialized){
            mActionMode.finish()
        }

    }
}