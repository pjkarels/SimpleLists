package com.bitsandbogs.simplelists.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bitsandbogs.simplelists.R

// the fragment initialization parameters
private const val ARG_CATEGORIES = "categories"

/**
 * A confirmation dialog when deleting items
 */
class DeleteConfirmFragment : DialogFragment() {
    private lateinit var categories: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            categories = bundle.getStringArray(ARG_CATEGORIES) as Array<String>
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(DeleteConfirmViewModel::class.java)

        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.list_title_delete)
                .setMessage(getString(R.string.list_delete_message, formatData()))
                .setPositiveButton(R.string.common_delete) { _, _ ->
                    vm.deleteSelectedCategories(categories.toList())
                }
                .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
            .create()
    }

    private fun formatData(): String {
        val builder = StringBuilder(categories.first())
        for (i in 1 until categories.size) {
            builder.append(",\n${categories[i]}")
        }

        return builder.toString()
    }
}