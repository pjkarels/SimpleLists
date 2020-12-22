package com.playground.navigationwithtabs.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.playground.navigationwithtabs.R

class ViewPagerFragment : Fragment(), TabLayoutMediator.TabConfigurationStrategy {

    private var tabTitles: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.app_menu)

        val vm = ViewModelProvider(this).get(PagerViewModel::class.java)
        val adapter = PagerAdapter(this)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = adapter

        vm.tabs.observe(viewLifecycleOwner, { tabs ->
            tabs?.let {
                tabTitles = tabs
                adapter.setTabs(tabs)

                val tabMediator = TabLayoutMediator(tabLayout, viewPager, this)
                tabMediator.detach()
                tabMediator.attach()
            }
        })
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (tabTitles == null) {
            return
        }
        tab.text = tabTitles?.get(position)
    }
}