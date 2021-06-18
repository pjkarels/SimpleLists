package com.bitsandbogs.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitsandbogs.simplelists.R
import com.bitsandbogs.simplelists.db.AppDatabase
import com.bitsandbogs.simplelists.model.ItemModel
import com.bitsandbogs.simplelists.model.NotificationModel
import com.bitsandbogs.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _nameErrorMsg = MutableLiveData<String>()
    val nameErrorMsg: LiveData<String> get() = _nameErrorMsg

    lateinit var originalName: String
    private lateinit var _itemModel: ItemModel
    val itemModel get() = _itemModel
    private val _taskLiveData = MutableLiveData<ItemModel>()
    val taskLiveData: LiveData<ItemModel> get() = _taskLiveData

    var editingReminder: NotificationModel? = null
    val removedReminders = mutableListOf<String>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun upsertTask(): Boolean {
        if (itemModel.name.isEmpty() || itemModel.name.isBlank()) {
            _nameErrorMsg.value = getApplication<Application>().getString(R.string.error_msg_name_required)
            return false
        }
        viewModelScope.launch {
            if (itemModel.isNew) {
                repository.insertTask(itemModel)
            } else {
                repository.updateTask(itemModel)
            }
        }

        return true
    }

    fun getTask(taskId: String, taskType: Long) {
        viewModelScope.launch {
            val taskFromRepo = repository.getTask(taskId)
            // doesn't exist yet so create new
            // id of 0 tells Room it's an 'Insert'
            if (taskFromRepo.isNew) {
                taskFromRepo.categoryId = taskType
            }
            _itemModel = taskFromRepo
            viewModelScope.launch {
                val type = repository.getCategory(taskType)
                _itemModel.categoryName = type.name
                updateLiveData()
            }
            originalName = _itemModel.name

            updateLiveData()
        }
    }

    fun updateItemName(name: String) {
        _itemModel.name = name
    }

    fun deleteTask() {
        _itemModel.removed = true
        viewModelScope.launch {
            upsertTask()
        }
    }

    fun updateTaskCompleteness() {
        _itemModel.completed = !itemModel.completed
        viewModelScope.launch {
            upsertTask()
            getTask(itemModel.id, itemModel.categoryId)
        }
    }

    fun addReminder(reminder: NotificationModel) {
        _itemModel.notifications.add(reminder)

        updateLiveData()
    }

    fun updateReminder(reminder: NotificationModel) {
        val index = itemModel.notifications.indexOfFirst { notification ->
            reminder.id == notification.id
        }
        if (index >= 0) {
            _itemModel.notifications[index] = reminder
        }

        updateLiveData()
    }

    fun removeReminder(reminder: NotificationModel) {
        // keep track of deleted alarms
        if (!itemModel.isNew) {
            removedReminders.add(reminder.id)
        }
        _itemModel.notifications.remove(reminder)

        updateLiveData()
    }

    fun updateItemListAssociation(id: Long) {
        _itemModel.categoryId = id

        updateLiveData()
    }

    private fun updateLiveData() {
        _taskLiveData.value = itemModel
    }
}