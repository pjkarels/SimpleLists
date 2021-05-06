package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteItemsConfirmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun deleteSelectedItems(itemIds: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItemsByIds(itemIds)
        }
    }
}