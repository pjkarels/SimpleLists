package com.playground.navigationwithtabs.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.playground.navigationwithtabs.db.TaskDao
import com.playground.navigationwithtabs.db.TaskType
import com.playground.navigationwithtabs.db.TaskTypeDao

class TaskRepository(private val taskDao: TaskDao, private val taskTypeDao: TaskTypeDao) {

    val taskTypes: LiveData<List<String>> =
        taskTypeDao.taskTypes.map { taskTypes ->
            taskTypes.map { taskType ->
                taskType.name
            }
        }

    suspend fun getTasksForType(type: TaskType) =
        taskDao.getTasks().filter { task ->
            task.type == type.name
        }

    suspend fun upsertTaskType(name: String) {
        taskTypeDao.insertTaskTypes(TaskType(name))
    }

    fun deleteTaskType(name: String) {
        taskTypeDao.deleteTaskType(name)
    }
}