package com.playground.navigationwithtabs.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.playground.navigationwithtabs.R

class DeleteCategoriesFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delete_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView: RecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        val categoriesAdapter = CategoryListAdapter(requireContext(), this)
        recyclerView.adapter = categoriesAdapter

        val vm = ViewModelProvider(this).get(DeleteCategoriesViewModel::class.java)
        vm.categories.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                categoriesAdapter.setCategories(categories)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

    }
}