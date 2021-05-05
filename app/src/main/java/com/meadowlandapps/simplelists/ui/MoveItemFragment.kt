package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_ITEM
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.CategoryModel

/**
 * A fragment representing a list of Items.
 */
class MoveItemFragment : DialogFragment(), View.OnClickListener {

    private var itemId: Int = 0
    private lateinit var categoryViewAdapter: MoveCategoryViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            itemId = it.getInt(BUNDLE_KEY_ITEM, 0)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(MoveItemViewModel::class.java)
        val view = layoutInflater.inflate(R.layout.fragment_move_item, null, false) as RecyclerView
        vm.getCategories(itemId)
        categoryViewAdapter = MoveCategoryViewAdapter(vm.categories, this)
        view.adapter = categoryViewAdapter

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton(R.string.button_move) { dialog, _ ->
                    vm.moveItem()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
    }

    override fun onClick(v: View?) {
        val vm = ViewModelProvider(this).get(MoveItemViewModel::class.java)
        vm.updateSelectedCategory(v?.tag as CategoryModel)
        categoryViewAdapter.setCategories(vm.categories)
    }
}