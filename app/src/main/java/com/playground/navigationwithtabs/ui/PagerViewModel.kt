package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.repository.TaskRepository

class PagerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)

        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    val tabs = repository.taskTypes
}