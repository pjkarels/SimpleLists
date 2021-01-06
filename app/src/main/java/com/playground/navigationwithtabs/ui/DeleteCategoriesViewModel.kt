package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.db.TaskType
import com.playground.navigationwithtabs.repository.TaskRepository
import kotlinx.coroutines.launch

class DeleteCategoriesViewModel(application: Application) : AndroidViewModel(application) {

    val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    val categories = repository.taskTypes

    suspend fun deleteCategories(categories: List<TaskType>) {
        viewModelScope.launch {
            repository.deleteCategories(categories)
        }
    }
}