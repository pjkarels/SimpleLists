package com.playground.navigationwithtabs.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.playground.navigationwithtabs.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_add -> addTab()
            R.id.menu_item_delete -> deleteTabs()
        }
        return true
    }

    private fun addTab() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.addTabFragment)
    }

    private fun deleteTabs() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.deleteCategoriesFragment)
    }
}