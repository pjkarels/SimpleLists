package com.meadowlandapps.simplelists.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.Intent.createChooser
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CATEGORY = "category"

class ShareDialogFragment : DialogFragment() {
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(ARG_CATEGORY, "")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(ShareDialogViewModel::class.java)
        val data = vm.getTasks(category)
        return AlertDialog.Builder(requireContext())
                .setTitle("Share $category List?")
                .setPositiveButton("Share") { _,_ ->
                    // prepare and show picker
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, data)
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