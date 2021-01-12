package com.meadowlandapps.simplelists.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.CategoryModel

class DeleteCategoriesFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delete_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val deleteButton: Button = view.findViewById(R.id.deleteCategoriesButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        val categoriesAdapter = CategoryListAdapter(requireContext(), this)
        recyclerView.adapter = categoriesAdapter

        val vm = ViewModelProvider(this).get(DeleteCategoriesViewModel::class.java)
        vm.categoriesLiveData.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                categoriesAdapter.setCategories(categories)
            }
        }

        deleteButton.setOnClickListener {
            vm.deleteSelectedCategories()
            findNavController().popBackStack()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val category = buttonView.tag as CategoryModel
        val vm = ViewModelProvider(this).get(DeleteCategoriesViewModel::class.java)
        if (isChecked) {
            vm.selectedCategoriesToDelete.add(category)
        } else {
            vm.selectedCategoriesToDelete.remove(category)
        }
    }
}