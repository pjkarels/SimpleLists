package com.meadowlandapps.simplelists.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.meadowlandapps.simplelists.R

class ViewPagerFragment : Fragment(), TabLayoutMediator.TabConfigurationStrategy, TabLayout.OnTabSelectedListener {

    private var tabTitles: List<String>? = null
    private var currentIndex = 0

    /**
     * Used to determine when the user selects the tab,
     * not when it's autoselected when the Tab Layout is created.
     */
    private var isCreatingView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCreatingView = true
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.app_menu)

        val drawerLayout: DrawerLayout = view.findViewById(R.id.drawer_layout)
        val navController = view.findNavController()

        val navView = view.findViewById<NavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, drawerLayout)

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
                tabLayout.selectTab(tabLayout.getTabAt(currentIndex))
                viewPager.setCurrentItem(currentIndex, false)
            }
        })

        tabLayout.addOnTabSelectedListener(this)

        setupDrawerSelection(navView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add -> addTab()
            R.id.menu_item_delete -> deleteTabs()
        }
        return true
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (tabTitles == null) {
            return
        }
        tab.text = tabTitles?.get(position)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (isCreatingView) {
            isCreatingView = false
            return
        }

        // we want to do this only when the user selects the tab, not when it's created
        val tabLayout: TabLayout? = view?.findViewById(R.id.tab_layout)
        currentIndex = tabLayout?.selectedTabPosition ?: 0
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    private fun setupDrawerSelection(navigationView: NavigationView) {
        val drawerLayout: DrawerLayout = requireView().findViewById(R.id.drawer_layout)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.menu_drawer_deletedItems -> viewDeletedItems()
            }
            true
        }
    }

    private fun addTab() {
        requireView().findNavController().navigate(R.id.addTabFragment)
    }

    private fun deleteTabs() {
        requireView().findNavController().navigate(R.id.deleteCategoriesFragment)
    }

    private fun viewDeletedItems() {
        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToDeletedItemsFragment()
        requireView().findNavController().navigate(action)
    }
}