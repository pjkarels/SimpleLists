package com.meadowlandapps.simplelists.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.meadowlandapps.simplelists.R

class AddTabFragment : Fragment() {

//    private val args: AddTabFragmentArgs by navArgs()
//    private val categoryId = args.category

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(this).get(AddTabViewModel::class.java)

        val tabNameEntry: EditText = view.findViewById(R.id.addTab_entry_name)
        val addTabButton: Button = view.findViewById(R.id.addTab_button_add)
        val toolbarTitleView = view.findViewById<TextView>(R.id.title)

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

        vm.categoryLiveData.observe(viewLifecycleOwner) { category ->
            category?.let {
                tabNameEntry.setText(category.name)
                if (category.id < 1) {
                    // new category added
                    addTabButton.text = getString(R.string.common_add)
                    toolbarTitleView.text = getString(R.string.lists_title_add)
                } else {
                    // edit existing
                    addTabButton.text = getString(R.string.common_rename)
                    toolbarTitleView.text = getString(R.string.lists_title_rename, category.name)
                }

                tabNameEntry.requestFocus()
                showKeyboard(tabNameEntry)
            }
        }
        vm.getCategory(0)
    }

    private fun addAndReturn(rootView: View) {
        val vm = ViewModelProvider(this).get(AddTabViewModel::class.java)
        vm.nameErrorMsg.observe(viewLifecycleOwner) { errorMsg ->
            rootView.findViewById<TextInputLayout>(R.id.addTab_entry_layout)?.error = errorMsg
        }

        val tabNameEntry: EditText = rootView.findViewById(R.id.addTab_entry_name)
        vm.category.name = tabNameEntry.text.toString()

        if (vm.upsertCategory()) {
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