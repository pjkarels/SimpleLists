package com.meadowlandapps.simplelists.ui

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.meadowlandapps.simplelists.R

class AddTabFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabNameEntry: EditText = view.findViewById(R.id.addTab_entry_name)
        val addTabButton: Button = view.findViewById(R.id.addTab_button_add)

        tabNameEntry.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    addAndReturn(v)
                    true
                }
                else -> false
            }
        }
        addTabButton.setOnClickListener {
            addAndReturn(view)
        }

        tabNameEntry.requestFocus()
        showKeyboard(tabNameEntry)
    }

    private fun addAndReturn(rootView: View) {
        val tabNameEntry: EditText = rootView.findViewById(R.id.addTab_entry_name)

        val vm = ViewModelProvider(this).get(AddTabViewModel::class.java)
        vm.nameErrorMsg.observe(viewLifecycleOwner) { errorMsg ->
            rootView.findViewById<TextInputLayout>(R.id.addTab_entry_layout)?.error = errorMsg
        }
        if (vm.addTab(tabNameEntry.text.toString())) {
            hideKeyboard(tabNameEntry)
            rootView.findNavController().popBackStack()
        }
    }

    private fun hideKeyboard(v: View) {
        val imm = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun showKeyboard(v: View) {
        val imm = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, 0)
    }
}