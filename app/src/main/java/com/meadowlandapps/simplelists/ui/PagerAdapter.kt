package com.meadowlandapps.simplelists.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var tabs = emptyList<String>()

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int): Fragment {
        return TabContentFragment.newInstance(tabs[position])
    }

    internal fun setTabs(tabs: List<String>) {
        this.tabs = tabs;
        notifyDataSetChanged()
    }
}