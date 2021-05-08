package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.runBlocking

class ShareDialogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)

        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao, db.notificationDao())
    }

    fun getTasks(id: Long): Pair<String, String> {
        var itemsString = ""
        var listTitle: String
        runBlocking {
            listTitle = repository.getCategory(id).name.toString()
            val names = repository.getItemNamesForListId(id)
            if (names.isNotEmpty()) {
                val builder = StringBuilder(names.first())
                for (i in 1 until names.size) {
                    builder.append(",\n${names[i]}")
                }
                itemsString = builder.toString()
            }
        }

        return Pair(itemsString, listTitle)
    }
}