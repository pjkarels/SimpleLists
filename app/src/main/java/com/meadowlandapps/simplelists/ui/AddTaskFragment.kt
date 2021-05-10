package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_DATE
import BUNDLE_KEY_TIME
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.ItemModel
import com.meadowlandapps.simplelists.model.NotificationModel

class AddTaskFragment : Fragment(), View.OnClickListener {

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
        val reminderRecyclerView = view.findViewById<RecyclerView>(R.id.addItem_recyclerView_notifications)
        val remindersAdapter = ReminderListAdapter(this)
        reminderRecyclerView.adapter = remindersAdapter
        val addReminderButton: View = view.findViewById(R.id.addItem_reminders_add)

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

        addReminderButton.setOnClickListener {
            vm.addReminder(NotificationModel())
        }

        setTimePickerObserver()

        vm.taskLiveData.observe(viewLifecycleOwner) { task ->
            task?.let {
                nameEntry.setText(task.name)
                if (task.isNew) {
                    addButton.text = getString(R.string.common_add)
                    toolbarTitleView.text = getString(R.string.lists_title_add)
                    hideMenuItems()
                } else {
                    addButton.text = getString(R.string.common_rename)
                    toolbarTitleView.text = getString(R.string.lists_title_edit, task.name)
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

                remindersAdapter.setReminders(task.notifications)

                updateButtonVisibility(task)
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

        vm.itemModel.name = nameEntry.text.toString()
        if (vm.upsertTask()) {
            goBack()
        }
    }

    private fun navigateMoveItem() {
        val rootView = requireView()
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        val action = AddTaskFragmentDirections.actionAddTaskFragmentToMoveItemFragment(vm.itemModel.id)
        rootView.findNavController().navigate(action)
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

    private fun updateButtonVisibility(item: ItemModel) {
        val moveButton = requireView().findViewById<Button>(R.id.editTask_button_move)
        if (item.isNew) {
            moveButton.visibility = View.GONE
        } else {
            moveButton.visibility = View.VISIBLE
            moveButton.setOnClickListener {
                navigateMoveItem()
            }
        }
    }

    override fun onClick(v: View) {
        val reminder = v.tag as NotificationModel

        when (v.id) {
            R.id.reminder_button_remove -> removeReminder(reminder)
            R.id.reminder_date -> showDatePicker(reminder)
            R.id.reminder_time -> showTimePicker(reminder)
        }
    }

    private fun removeReminder(reminder: NotificationModel) {
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        vm.removeReminder(reminder)
    }

    private fun showDatePicker(reminder: NotificationModel) {
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        vm.editingReminder = reminder
        val action = AddTaskFragmentDirections.actionAddTaskFragmentToDateDialog(reminder.time.timeInMillis)
        findNavController().navigate(action)
    }

    private fun showTimePicker(reminder: NotificationModel) {
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        vm.editingReminder = reminder
        val action = AddTaskFragmentDirections.actionAddTaskFragmentToTimeDialog(reminder.time.timeInMillis)
        findNavController().navigate(action)
    }

    private fun setTimePickerObserver() {
        val navController = findNavController()
        // After a configuration change or process death, the currentBackStackEntry
        // points to the dialog destination, so you must use getBackStackEntry()
        // with the specific ID of your destination to ensure we always
        // get the right NavBackStackEntry
        val navBackStackEntry = navController.getBackStackEntry(R.id.addTaskFragment)

        // Create our observer and add it to the NavBackStackEntry's lifecycle
        val timeEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                    && navBackStackEntry.savedStateHandle.contains(BUNDLE_KEY_TIME)) {
                val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
                val time = navBackStackEntry.savedStateHandle.get<Long>(BUNDLE_KEY_TIME)
                time?.let {
                    vm.editingReminder?.time?.timeInMillis = time
                    vm.editingReminder?.let { vm.updateReminder(it) }
                }
                vm.editingReminder = null
            }
        }

        val dateEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                    && navBackStackEntry.savedStateHandle.contains(BUNDLE_KEY_DATE)) {
                val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
                val time = navBackStackEntry.savedStateHandle.get<Long>(BUNDLE_KEY_DATE)
                time?.let {
                    vm.editingReminder?.time?.timeInMillis = time
                    vm.editingReminder?.let { vm.updateReminder(it) }
                }
                vm.editingReminder = null
            }
        }

        navBackStackEntry.lifecycle.addObserver(timeEventObserver)
        navBackStackEntry.lifecycle.addObserver(dateEventObserver)

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(timeEventObserver)
                navBackStackEntry.lifecycle.removeObserver(dateEventObserver)
            }
        })
    }
}