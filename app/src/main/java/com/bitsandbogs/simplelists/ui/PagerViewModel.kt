package com.bitsandbogs.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bitsandbogs.simplelists.db.AppDatabase
import com.bitsandbogs.simplelists.repository.TaskRepository

class PagerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)

        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    val categories = repository.taskTypes
}