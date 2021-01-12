package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _nameErrorMsg = MutableLiveData<String>()
    val nameErrorMsg: LiveData<String> get() = _nameErrorMsg

    lateinit var task: Task
    private val _taskLiveData = MutableLiveData<Task>()
    val taskLiveData: LiveData<Task> get() = _taskLiveData

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun upsertTask(): Boolean {
        if (task.name.isEmpty() || task.name.isBlank()) {
            _nameErrorMsg.value = getApplication<Application>().getString(R.string.error_msg_name_required)
            return false
        }
        viewModelScope.launch {
            repository.upsertTask(task)
        }

        return true
    }

    fun getTask(taskId: Int, taskType: String) {
        viewModelScope.launch {
            var taskFromRepo = repository.getTask(taskId)
            if (taskFromRepo == null) {
                // doesn't exist yet so create new
                taskFromRepo = Task(0, "", taskType, false)
            }
            task = taskFromRepo
            _taskLiveData.value = task
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTaskCompleteness() {
        task.completed = !task.completed
        viewModelScope.launch {
            upsertTask()
            getTask(task.id, task.type)
        }
    }
}