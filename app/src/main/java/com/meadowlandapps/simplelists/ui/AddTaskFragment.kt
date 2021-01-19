package com.meadowlandapps.simplelists.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.meadowlandapps.simplelists.R

class AddTaskFragment: Fragment() {

    private val args: AddTaskFragmentArgs by navArgs()

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.item_menu)

        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        val nameEntry = view.findViewById<EditText>(R.id.addTask_entry_name)
        val addButton = view.findViewById<Button>(R.id.addTask_button_add)
        val toolbarTitleView = view.findViewById<TextView>(R.id.title)

        nameEntry.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    addItemAndGoBack()
                    true
                }
                else -> false
            }
        }
        addButton.setOnClickListener {
            addItemAndGoBack()
        }

        vm.taskLiveData.observe(viewLifecycleOwner) { task ->
            task?.let {
                nameEntry.setText(task.name)
                if (task.id < 1) {
                    addButton.text = getString(R.string.common_add)
                    toolbarTitleView.text = getString(R.string.lists_title_add)
                    hideMenuItems()
                }
                else {
                    addButton.text = getString(R.string.common_rename)
                    toolbarTitleView.text = getString(R.string.lists_title_rename, task.name)
                    // update Menu check mark
                    val icon = if (task.completed) {
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_uncomplete_24,
                            requireContext().theme
                        )
                    }
                    else {
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_check_24,
                            requireContext().theme
                        )
                    }
                    icon?.setTint(requireContext().getColor(R.color.white))
                    menu?.findItem(R.id.menu_item_mark_complete)?.icon = icon
                }

                nameEntry.requestFocus()
                showKeyboard(nameEntry)
            }
        }
        vm.getTask(args.task, args.type)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.item_menu, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        when (item.itemId) {
            R.id.menu_item_mark_complete -> {
                vm.updateTaskCompleteness()
            }
            R.id.menu_item_delete_item -> {
                vm.deleteTask()
                goBack()
            }
        }
        return true
    }

    private fun addItemAndGoBack() {
        val rootView = requireView()
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        vm.nameErrorMsg.observe(viewLifecycleOwner) { errorMsg ->
            rootView.findViewById<TextInputLayout>(R.id.addTask_entry_layout).error = errorMsg
        }
        val nameEntry = rootView.findViewById<EditText>(R.id.addTask_entry_name)

        vm.task.name = nameEntry.text.toString()
        if (vm.upsertTask()) {
            goBack()
        }
    }

    private fun goBack() {
        val rootView = requireView()
        val nameEntry = rootView.findViewById<EditText>(R.id.addTask_entry_name)
        hideKeyboard(nameEntry)
        rootView.findNavController().popBackStack()
    }

    private fun hideKeyboard(v: View) {
        val imm = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun showKeyboard(v: View) {
        val imm = v.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, 0)
    }

    private fun hideMenuItems() {
        menu?.forEach { item ->
            item.isVisible = false
        }
    }
}