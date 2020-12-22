package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTabViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    fun addTab(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertTaskType(name)
        }
    }
}