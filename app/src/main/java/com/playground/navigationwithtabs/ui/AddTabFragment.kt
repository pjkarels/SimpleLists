package com.playground.navigationwithtabs.ui

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import com.playground.navigationwithtabs.R

class AddTabFragment : DialogFragment() {

    private lateinit var viewModel: AddTabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTabViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabNameEntry: EditText = view.findViewById(R.id.addTab_entry_name)
        val addTabButton: Button = view.findViewById(R.id.addTab_button_add)

        addTabButton.setOnClickListener { v ->
            viewModel.addTab(tabNameEntry.text.toString())
            hideKeyboard(tabNameEntry)
            v.findNavController().popBackStack()
        }

        tabNameEntry.requestFocus()
    }

    private fun hideKeyboard(v: View) {
        val imm = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}