package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.model.ItemModel
import com.meadowlandapps.simplelists.model.NotificationModel
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _nameErrorMsg = MutableLiveData<String>()
    val nameErrorMsg: LiveData<String> get() = _nameErrorMsg

    lateinit var itemModel: ItemModel
    private val _taskLiveData = MutableLiveData<ItemModel>()
    val taskLiveData: LiveData<ItemModel> get() = _taskLiveData

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao(), db.notificationDao())
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
                taskFromRepo.typeId = taskType
            }
            itemModel = taskFromRepo
            _taskLiveData.value = itemModel
        }
    }

    fun deleteTask() {
        itemModel.removed = true
        viewModelScope.launch {
            upsertTask()
        }
    }

    fun updateTaskCompleteness() {
        itemModel.completed = !itemModel.completed
        viewModelScope.launch {
            upsertTask()
            getTask(itemModel.id, itemModel.typeId)
        }
    }

    fun addReminder(reminder: NotificationModel) {
        itemModel.notifications.add(reminder)

        updateLiveData()
    }

    fun updateReminder(reminder: NotificationModel) {
        val index = itemModel.notifications.indexOfFirst { notification ->
            reminder.id == notification.id
        }
        itemModel.notifications[index] = reminder
    }

    fun removeReminder(reminder: NotificationModel) {
        itemModel.notifications.remove(reminder)
    }

    private fun updateLiveData() {
        _taskLiveData.value = itemModel
    }
}