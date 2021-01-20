package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class DeletedItemsViewModel(application: Application): AndroidViewModel(application) {
    private val _repository: TaskRepository
    val selectedItems = mutableListOf<Task>()

    init {
        val db = AppDatabase.getDatabase(application)
        _repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    val removedItems = _repository.removedItems

    fun restoreSelectedItems() {
        viewModelScope.launch {
            _repository.upsertItems(selectedItems)
        }
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            _repository.deleteItems(selectedItems)
        }
    }
}