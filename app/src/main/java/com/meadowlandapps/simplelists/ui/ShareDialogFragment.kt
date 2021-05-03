package com.meadowlandapps.simplelists.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

// the fragment initialization parameters
private const val ARG_CATEGORY = "category"

class ShareDialogFragment : DialogFragment() {
    private var categoryId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt(ARG_CATEGORY, 0)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(ShareDialogViewModel::class.java)
        val data = vm.getTasks(categoryId)
        return AlertDialog.Builder(requireContext())
                .setTitle("Share List: \"${data.second}\"?")
                .setPositiveButton("Share") { _,_ ->
                    // prepare and show picker
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, data.first)
                        type = "text/plain"
                    }

                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog,_ ->
                    dialog.dismiss()
                }
                .create()
    }
}