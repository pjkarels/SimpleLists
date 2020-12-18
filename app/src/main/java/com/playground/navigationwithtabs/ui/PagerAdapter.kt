package com.playground.navigationwithtabs.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var tabs = emptyList<String>()

    override fun getItemCount() = tabs.size + 1 // append the "add new tab" tab

    override fun createFragment(position: Int): Fragment {
        return if (position == tabs.size) {
            AddTabFragment.newInstance()
        } else {
            TabContentFragment.newInstance()
        }
    }

    internal fun setTabs(tabs: List<String>) {
        this.tabs = tabs;
        notifyDataSetChanged()
    }
}