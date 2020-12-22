package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.db.Task
import com.playground.navigationwithtabs.db.TaskType
import com.playground.navigationwithtabs.repository.TaskRepository
import kotlinx.coroutines.launch

class TabContentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun getTasks(taskType: TaskType) {
        viewModelScope.launch {
            val tasksFromRepo = repository.getTasksForType(taskType)
            _tasks.value = tasksFromRepo
        }
    }
}