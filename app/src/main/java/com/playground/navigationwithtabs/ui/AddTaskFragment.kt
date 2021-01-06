package com.playground.navigationwithtabs.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
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
        val toolbarTitleView = view.findViewById<TextView>(R.id.title)

        addButton.setOnClickListener { v ->
            viewModel.task?.name = nameEntry.text.toString()
            viewModel.upsertTask()
            hideKeyboard(nameEntry)
            v.findNavController().popBackStack()
        }

        viewModel.taskLiveData.observe(viewLifecycleOwner) { task ->
            task?.let {
                nameEntry.setText(task.name)
                if (task.id < 1) {
                    addButton.text = getString(R.string.common_add)
                    toolbarTitleView.text = getString(R.string.tasks_title_add)
                } else {
                    addButton.text = getString(R.string.common_rename)
                    toolbarTitleView.text = getString(R.string.tasks_title_rename, task.name)
                }

                nameEntry.requestFocus()
            }
        }
        viewModel.getTask(args.task, args.type)
    }

    private fun hideKeyboard(v: View) {
        val imm = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}