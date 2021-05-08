package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_ITEMS
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.meadowlandapps.simplelists.R

/**
 * A confirmation dialog when deleting items
 */
class DeleteItemsConfirmDialogFragment : DialogFragment() {
    private lateinit var items: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            items = bundle.getStringArray(BUNDLE_KEY_ITEMS) ?: arrayOf()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(DeleteItemsConfirmViewModel::class.java)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.screen_deleteItemsConfirm_title)
            .setMessage(R.string.items_delete_message)
            .setPositiveButton(R.string.common_delete) { _, _ ->
                vm.deleteSelectedItems(items.toList())
            }
            .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}