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

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    fun getTasks(id: Int): Pair<String, String> {
        var itemsString = ""
        var listTitle: String
        runBlocking {
            listTitle = repository.getCategory(id)?.name.toString()
            val items = repository.getItemsForList(id)
            if (items.isNotEmpty()) {
                val builder = StringBuilder(items.first().name)
                for (i in 1 until items.size) {
                    builder.append(",\n${items[i].name}")
                }
                itemsString = builder.toString()
            }
        }

        return Pair(itemsString, listTitle)
    }
}