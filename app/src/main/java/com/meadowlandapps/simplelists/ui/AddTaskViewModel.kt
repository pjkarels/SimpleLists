package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.model.ItemModel
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
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun upsertTask(): Boolean {
        if (itemModel.name.isEmpty() || itemModel.name.isBlank()) {
            _nameErrorMsg.value = getApplication<Application>().getString(R.string.error_msg_name_required)
            return false
        }
        viewModelScope.launch {
            if (itemModel.id == 0L) {
                repository.insertTask(itemModel)
            } else {
                repository.updateTask(itemModel)
            }
        }

        return true
    }

    fun getTask(taskId: Long, taskType: Long) {
        viewModelScope.launch {
            val taskFromRepo = repository.getTask(taskId)
            // doesn't exist yet so create new
            // id of 0 tells Room it's an 'Insert'
            if (taskFromRepo.id == 0L) {
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
}