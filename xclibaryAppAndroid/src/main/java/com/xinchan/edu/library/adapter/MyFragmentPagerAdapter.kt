package com.xinchan.edu.library.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class MyFragmentPagerAdapter(fm: FragmentManager, tabList: MutableList<String>, fragmentList: MutableList<Fragment>) : FragmentPagerAdapter(fm) {
    private var tabList: MutableList<String> = ArrayList()
    private var fragmentList: MutableList<Fragment> = ArrayList()

    init {
        this.tabList = tabList
        this.fragmentList = fragmentList
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabList[position]
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}