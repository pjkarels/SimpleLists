package com.bitsandbogs.simplelists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.bitsandbogs.simplelists.R
import com.bitsandbogs.simplelists.model.CategoryModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment(), TabLayoutMediator.TabConfigurationStrategy, TabLayout.OnTabSelectedListener {

    private var categories: List<CategoryModel>? = null
    private var currentIndex = 0

    private var menu: Menu? = null

    /**
     * Used to determine when the user selects the tab,
     * not when it's auto-selected as when the Tab Layout is created.
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
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val vm = ViewModelProvider(this).get(PagerViewModel::class.java)
        val adapter = PagerAdapter(this)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager.adapter = adapter

        vm.categories.observe(viewLifecycleOwner, { tabs ->
            tabs?.let {
                categories = tabs
                adapter.setTabs(categories!!)

                val tabMediator = TabLayoutMediator(tabLayout, viewPager, this)
                tabMediator.detach()
                tabMediator.attach()
                tabLayout.selectTab(tabLayout.getTabAt(currentIndex))
                viewPager.setCurrentItem(currentIndex, false)
            }

            updateMenuIcons()
        })

        tabLayout.addOnTabSelectedListener(this)

        setupDrawerSelection(navView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
        this.menu = menu

        updateMenuIcons()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add -> addTab()
            R.id.menu_item_edit -> editTabs()
            R.id.menu_item_share -> share()
        }
        return true
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (categories == null) {
            return
        }
        tab.text = categories?.get(position)?.name
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
            drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.menu_drawer_deletedItems -> viewDeletedItems()
            }
            true
        }
    }

    private fun addTab() {
        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToAddTabFragment(0L)
        requireView().findNavController().navigate(action)
    }

    private fun editTabs() {
        if (categories?.isNotEmpty() == true) {
            requireView().findNavController().navigate(R.id.editCategoriesFragment)
        }
    }

    private fun share() {
        if (categories?.isNotEmpty() == true) {
            val category = categories?.get(currentIndex)
            val id = category?.id ?: 0
            val action = ViewPagerFragmentDirections.actionViewPagerFragmentToShareDialogFragment(id)
            requireView().findNavController().navigate(action)
        }
    }

    private fun viewDeletedItems() {
        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToDeletedItemsFragment()
        requireView().findNavController().navigate(action)
    }

    private fun navigateAbout() {
        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToAboutFragment()
        requireView().findNavController().navigate(action)
    }

    private fun updateMenuIcons() {
        val editItem = menu?.findItem(R.id.menu_item_edit)
        val shareItem = menu?.findItem(R.id.menu_item_share)
        if (categories.isNullOrEmpty()) {
            editItem?.isEnabled = false
            editItem?.icon?.setTint(requireContext().getColor(R.color.grey_light))
            shareItem?.isEnabled = false
            shareItem?.icon?.setTint(requireContext().getColor(R.color.grey_light))
        } else {
            editItem?.isEnabled = true
            editItem?.icon?.setTint(requireContext().getColor(R.color.white))
            shareItem?.isEnabled = true
            shareItem?.icon?.setTint(requireContext().getColor(R.color.white))
        }
    }
}