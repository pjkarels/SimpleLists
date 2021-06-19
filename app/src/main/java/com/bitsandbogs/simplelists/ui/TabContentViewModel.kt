package com.bitsandbogs.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bitsandbogs.simplelists.db.AppDatabase
import com.bitsandbogs.simplelists.model.ItemModel
import com.bitsandbogs.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class TabContentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun getTasks(categoryId: Long) = repository.tasksForType(categoryId)

    fun deleteTask(item: ItemModel) {
        item.removed = true
        viewModelScope.launch {
            repository.updateTask(item)
        }
    }

    fun updateTaskCompleteness(item: ItemModel) {
        item.completed = !item.completed
        viewModelScope.launch {
            repository.updateTask(item)
        }
    }
}