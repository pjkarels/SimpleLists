package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class TabContentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun getTasks(taskType: Int) = repository.tasksForType(taskType)

    fun deleteTask(task: Task) {
        task.removed = true
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTaskCompleteness(task: Task) {
        task.completed = !task.completed
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }
}