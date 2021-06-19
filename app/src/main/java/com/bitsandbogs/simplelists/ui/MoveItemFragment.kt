package com.bitsandbogs.simplelists.ui

import BUNDLE_KEY_CATEGORY_ID
import BUNDLE_KEY_ITEM_ID
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bitsandbogs.simplelists.R
import com.bitsandbogs.simplelists.model.CategoryModel

/**
 * A fragment representing a list of Items.
 */
class MoveItemFragment : DialogFragment(), View.OnClickListener {

    private var itemId: String = ""
    private lateinit var categoryViewAdapter: MoveCategoryViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            itemId = it.getString(BUNDLE_KEY_ITEM_ID, "")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(MoveItemViewModel::class.java)
        val view = layoutInflater.inflate(R.layout.fragment_move_item, null, false) as RecyclerView
        vm.getCategories(itemId)
        categoryViewAdapter = MoveCategoryViewAdapter(vm.categories, this)
        view.adapter = categoryViewAdapter

        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.item_title_move)
                .setMessage(R.string.item_move_message)
                .setView(view)
                .setPositiveButton(R.string.button_text_move) { dialog, _ ->
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(BUNDLE_KEY_CATEGORY_ID, vm.categories.first { it.selected }.id)
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