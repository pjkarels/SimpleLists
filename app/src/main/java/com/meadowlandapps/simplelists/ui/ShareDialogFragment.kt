package com.meadowlandapps.simplelists.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CATEGORY = "category"

class ShareDialogFragment : DialogFragment() {
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(ARG_CATEGORY)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle("Share List $category?")
                .setPositiveButton("Share") { _,_ ->
                    // prepare and show picker
                }
                .setNegativeButton("Cancel") { dialog,_ ->
                    dialog.dismiss()
                }
                .create()
    }
}