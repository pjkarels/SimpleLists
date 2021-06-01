package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_CATEGORY_ID
import BUNDLE_KEY_CATEGORY_NAME
import BUNDLE_KEY_DATE
import BUNDLE_KEY_ITEM_ID
import BUNDLE_KEY_ITEM_NAME
import BUNDLE_KEY_TIME
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.fragment.app.viewModels
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
import com.meadowlandapps.simplelists.receiver.AlarmReceiver
import java.util.*

class AddTaskFragment : Fragment(), View.OnClickListener, TextWatcher {

    private val vm: AddTaskViewModel by viewModels()

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
                nameEntry.removeTextChangedListener(this)
                nameEntry.setText(task.name)
                nameEntry.addTextChangedListener(this)

                updateMenuItemsVisibility()
                if (task.isNew) {
                    toolbarTitleView.text = getString(R.string.lists_title_add)
                } else {
                    toolbarTitleView.text = getString(R.string.lists_title_edit, vm.originalName)
                    // update Menu check mark
                    val icon = if (task.completed) {
                        ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_uncomplete_24,
                                requireContext().theme
                        )
                    } else {
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
                view.findViewById<EditText>(R.id.addTask_entry_name)?.setSelection(vm.itemModel.name.length)
            }
        }
        vm.getTask(args.itemId, args.categoryId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.item_menu, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        vm.nameErrorMsg.observe(viewLifecycleOwner) { errorMsg ->
            rootView.findViewById<TextInputLayout>(R.id.addTask_entry_layout).error = errorMsg
        }
        val nameEntry = rootView.findViewById<EditText>(R.id.addTask_entry_name)

        vm.updateItemName(nameEntry.text.toString())
        if (vm.upsertTask()) {
            removeAlarms(vm.removedReminders)
            vm.itemModel.notifications.forEach { reminder ->
                addAlarm(reminder.time.timeInMillis, reminder.hashCode())
            }

            goBack()
        }
    }

    private fun navigateMoveItem() {
        val rootView = requireView()
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
            R.id.reminder_button_remove -> vm.removeReminder(reminder)
            R.id.reminder_date -> showDatePicker(reminder)
            R.id.reminder_time -> showTimePicker(reminder)
        }
    }

    private fun showDatePicker(reminder: NotificationModel) {
        vm.editingReminder = reminder
        val action = AddTaskFragmentDirections.actionAddTaskFragmentToDateDialog(reminder.time.timeInMillis)
        findNavController().navigate(action)
    }

    private fun showTimePicker(reminder: NotificationModel) {
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
                val time = navBackStackEntry.savedStateHandle.get<Long>(BUNDLE_KEY_DATE)
                time?.let {
                    vm.editingReminder?.time?.timeInMillis = time
                    vm.editingReminder?.let { vm.updateReminder(it) }
                }
                vm.editingReminder = null
            }
        }

        val moveEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                    && navBackStackEntry.savedStateHandle.contains(BUNDLE_KEY_CATEGORY_ID)) {
                val listId = navBackStackEntry.savedStateHandle.get<Long>(BUNDLE_KEY_CATEGORY_ID)
                listId?.let {
                    vm.updateItemListAssociation(listId)
                }
            }
        }

        navBackStackEntry.lifecycle.addObserver(timeEventObserver)
        navBackStackEntry.lifecycle.addObserver(dateEventObserver)
        navBackStackEntry.lifecycle.addObserver(moveEventObserver)

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(timeEventObserver)
                navBackStackEntry.lifecycle.removeObserver(dateEventObserver)
                navBackStackEntry.lifecycle.removeObserver(moveEventObserver)
            }
        })
    }

    private fun addAlarm(time: Long, reminderId: Int) {
        val item = ViewModelProvider(this).get(AddTaskViewModel::class.java).itemModel
        val alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).let { intent ->
            intent.putExtra(BUNDLE_KEY_CATEGORY_ID, item.categoryId)
            intent.putExtra(BUNDLE_KEY_CATEGORY_NAME, item.categoryName)
            intent.putExtra(BUNDLE_KEY_ITEM_ID, item.id)
            intent.putExtra(BUNDLE_KEY_ITEM_NAME, item.name)
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            PendingIntent.getBroadcast(
                requireContext(),
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmIntent)
        } else {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent)
        }
    }

    private fun removeAlarms(reminderIds: List<Int>) {
        for (id in reminderIds) {
            val alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).let { intent ->
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                PendingIntent.getBroadcast(
                    requireContext(),
                    id,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }
            alarmMgr.cancel(alarmIntent)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // ignore
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // ignore
    }

    override fun afterTextChanged(s: Editable?) {
        vm.updateItemName(s.toString())
        view?.findViewById<EditText>(R.id.addTask_entry_name)?.setSelection(vm.itemModel.name.length)
    }
}