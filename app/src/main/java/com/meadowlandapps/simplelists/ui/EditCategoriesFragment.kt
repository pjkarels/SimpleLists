package com.meadowlandapps.simplelists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.CategoryModel

class EditCategoriesFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val renameButton: Button = view.findViewById(R.id.editListTitleButton)
        val deleteButton: Button = view.findViewById(R.id.deleteCategoriesButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        val categoriesAdapter = CategoryListAdapter(requireContext(), this)
        recyclerView.adapter = categoriesAdapter

        val vm = ViewModelProvider(this).get(EditCategoriesViewModel::class.java)
        vm.categoriesLiveData.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                categoriesAdapter.setCategories(categories)
                val visibility = if (categories.isNotEmpty()) View.VISIBLE else View.GONE
                renameButton.visibility = visibility
                deleteButton.visibility = visibility
                vm.selectedCategories.clear()
                vm.onSelectedCategoriesChanged()
            }
        }

        vm.isEditButtonEnabled.observe(viewLifecycleOwner) { enabled ->
            renameButton.isEnabled = enabled
        }

        vm.isDeleteButtonEnabled.observe(viewLifecycleOwner) { enabled ->
            deleteButton.isEnabled = enabled
        }

        renameButton.setOnClickListener {
            val action = EditCategoriesFragmentDirections.actionEditCategoriesFragmentToAddTabFragment(vm.selectedCategories.first().id)
            findNavController().navigate(action)
        }

        deleteButton.setOnClickListener {
            val listNames = vm.selectedCategories
            val action =
                    EditCategoriesFragmentDirections.actionEditCategoriesFragmentToDeleteConfirmFragment(
                            listNames.map { categoryModel ->
                                categoryModel.name
                            }.toTypedArray()
                    )
            findNavController().navigate(action)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val category = buttonView.tag as CategoryModel
        val vm = ViewModelProvider(this).get(EditCategoriesViewModel::class.java)
        vm.checkedChanged(isChecked, category)
    }
}