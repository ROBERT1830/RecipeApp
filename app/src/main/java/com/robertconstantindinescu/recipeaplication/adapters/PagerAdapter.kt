package com.robertconstantindinescu.recipeaplication.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


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