package com.playground.navigationwithtabs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.playground.navigationwithtabs.R

class AddTaskFragment: Fragment() {

    private val args: AddTaskFragmentArgs by navArgs()
    private lateinit var viewModel: AddTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddTaskViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nameEntry = view.findViewById<EditText>(R.id.addTask_entry_name)
        val addButton = view.findViewById<Button>(R.id.addTask_button_add)

        addButton.setOnClickListener { v ->
            val name = nameEntry.text.toString()
            viewModel.addTask(args.type, name)
            v.findNavController().popBackStack()
        }
    }
}