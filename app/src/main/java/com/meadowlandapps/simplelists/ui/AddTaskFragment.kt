package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_DATE
import BUNDLE_KEY_TIME
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

        addReminderButton.setOnClickListener {
            vm.addReminder(NotificationModel())
        }

        setTimePickerObserver()

        vm.taskLiveData.observe(viewLifecycleOwner) { task ->
            task?.let {
                nameEntry.setText(task.name)
                updateMenuItemsVisibility()
                if (task.isNew) {
                    toolbarTitleView.text = getString(R.string.lists_title_add)
                } else {
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
            R.id.menu_item_move -> {
                navigateMoveItem()
            }
            R.id.menu_item_save -> {
                addItemAndGoBack()
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
            vm.itemModel.notifications.forEach { reminder ->
                addReminder(reminder.time.timeInMillis)
            }
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

    private fun updateMenuItemsVisibility() {
        val vm = ViewModelProvider(this).get(AddTaskViewModel::class.java)
        menu?.forEach { item ->
            when (item.itemId) {
                R.id.menu_item_mark_complete -> {
                    item.isVisible = !vm.itemModel.isNew
                }
                R.id.menu_item_delete_item -> {
                    item.isVisible = !vm.itemModel.isNew
                }
                R.id.menu_item_move -> {
                    item.isVisible = !vm.itemModel.isNew
                }
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

    private fun addReminder(time: Long) {
        val alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), MainActivity::class.java).let { intent ->
            PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        }

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent)
    }

    private fun updateReminder() {
        val alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}