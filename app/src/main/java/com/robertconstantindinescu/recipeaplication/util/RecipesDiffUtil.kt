package com.robertconstantindinescu.recipeaplication.util

import androidx.recyclerview.widget.DiffUtil
import com.robertconstantindinescu.recipeaplication.models.Result


/**
 *
 * This calss take 2 parameters the old list and it type and the second paramenter is a new list
 * of the same type.
 * This class extends Diffutil class
 * DiffUtil is a utility class that calculates the difference between two lists and outputs a list of update operations that converts the first list into the second one.
It can be used to calculate updates for a RecyclerView Adapter.
 */
class RecipesDiffUtil<T>(
   /* private val oldList: List<Result>,
    private val newList: List<Result>*/
    private val oldList: List<T>,
    private val newList: List<T>
): DiffUtil.Callback() {

    /**
     * returns the size of the old list
     */
    override fun getOldListSize(): Int {
        return oldList.size
    }

    /**
     * returns the size of the new list
     */
    override fun getNewListSize(): Int {
        return newList.size
    }

    /**
     * this function is autocalled to decide wheret two objects represent the same item in the old and new list.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //we use === to se if the two objects are identicall the ssame
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    /**
     * Checks wheter two items have the same data. This methos is called by diffutil only if areitemsthesame returns true.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}