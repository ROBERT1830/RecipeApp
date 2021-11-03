package com.robertconstantindinescu.recipeaplication.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**We will use this bundle to pass the data from the details activity to our fragments
 * next parameter we ahve is a lsit of fragments. como vamos a tener diferentes fragmentos en la detailactivity pues tenemos que usar un array de fragenets
 * list of titltes
 * and a fragment amnager
 *
 * All of them are used to pass the data from activitydetail to other framtnes.
 *
 * This class will extend the framget pagr adapter
 *
 * NEHAVIOUR ---> Indicates that only the current fragment will be in the Lifecycle.State.RESUMED state. All other Fragments are capped at Lifecycle.State.STARTED
 * */



class PagerAdapter(

    private val resultBundle: Bundle,
    private val fragments: ArrayList<Fragment>,
    private val title: ArrayList<String>,
    private val fm: FragmentManager
):FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * This will retn the number of framgnts. three framgnets
     */
    override fun getCount(): Int {
        return  fragments.size
    }

    /**
     * pass all the result from our Recipe to those fragments
     * retunr the framgtnts and the actual position
     */
    override fun getItem(position: Int): Fragment {
        fragments[position].arguments = resultBundle
        return fragments[position]
    }

    /**
     * Return the title of those framgents.
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}