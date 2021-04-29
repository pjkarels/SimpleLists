package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder

class ShareDialogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)

        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    fun getTasks(taskType: String): String {
        var returnString: String
        runBlocking {
            val items =  repository.getItemsForList(taskType)
            val builder = StringBuilder(items.first().name)
            for (i in 1 until items.size) {
                builder.append(",\n${items[i].name}")
            }
            returnString = builder.toString()
        }

        return returnString
    }
}