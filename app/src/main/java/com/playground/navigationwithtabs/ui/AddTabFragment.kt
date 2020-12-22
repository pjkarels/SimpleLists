package com.playground.navigationwithtabs.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.playground.navigationwithtabs.R

class AddTabFragment : DialogFragment() {

    private lateinit var viewModel: AddTabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTabViewModel::class.java)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView = layoutInflater.inflate(R.layout.fragment_add_tab, null, false)
        val tabNameEntry: EditText = rootView.findViewById(R.id.addTab_entry_name)
        return AlertDialog.Builder(requireActivity())
                .setTitle(R.string.menu_title_add)
                .setPositiveButton(R.string.addTab_add) { _: DialogInterface, _: Int ->
                    viewModel.addTab(tabNameEntry.text.toString())
                }
                .setNegativeButton(R.string.addTab_cancel) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .setView(rootView)
                .create()
    }
}