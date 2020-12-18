package com.playground.navigationwithtabs.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var tabs = emptyList<String>()

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int): Fragment {
        return TabContentFragment.newInstance()
    }

    internal fun setTabs(tabs: List<String>) {
        this.tabs = tabs;
        notifyDataSetChanged()
    }
}