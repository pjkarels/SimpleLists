package com.playground.navigationwithtabs.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            v.findNavController().popBackStack()
        }
    }
}