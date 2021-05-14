package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_CATEGORY_ID
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.meadowlandapps.simplelists.R

class ShareDialogFragment : DialogFragment() {
    private var categoryId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getLong(BUNDLE_KEY_CATEGORY_ID, 0)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(ShareDialogViewModel::class.java)
        val data = vm.getTasks(categoryId)
        return AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.list_title_share, data.second))
                .setPositiveButton(R.string.button_list_share) { _, _ ->
                    // prepare and show picker
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, data.first)
                        type = "text/plain"
                    }

                    startActivity(intent)
                }
                .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
    }
}