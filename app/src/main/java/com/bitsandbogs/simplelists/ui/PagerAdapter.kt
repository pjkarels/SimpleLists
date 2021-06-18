package com.bitsandbogs.simplelists.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bitsandbogs.simplelists.model.CategoryModel

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var tabs = emptyList<CategoryModel>()

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int): Fragment {
        return TabContentFragment.newInstance(tabs[position].id)
    }

    internal fun setTabs(tabs: List<CategoryModel>) {
        this.tabs = tabs
        notifyDataSetChanged()
    }
}