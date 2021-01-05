package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.db.Task
import com.playground.navigationwithtabs.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TaskRepository

    var task: Task? = null
    private val _taskLiveData = MutableLiveData<Task>()
    val taskLiveData: LiveData<Task> get() = _taskLiveData

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun upsertTask() {
        viewModelScope.launch {
            task?.let {
                repository.upsertTask(it)
            }
        }
    }

    fun getTask(taskId: Int, taskType: String) {
        viewModelScope.launch {
            task = repository.getTask(taskId)
            if (task == null) {
                // doesn't exist yet so create new
                task = Task(0, "", taskType, false)
            }
            _taskLiveData.value = task
        }
    }
}