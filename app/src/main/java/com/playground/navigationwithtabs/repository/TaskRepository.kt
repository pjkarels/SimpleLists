package com.playground.navigationwithtabs.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.playground.navigationwithtabs.db.Task
import com.playground.navigationwithtabs.db.TaskDao
import com.playground.navigationwithtabs.db.TaskType
import com.playground.navigationwithtabs.db.TaskTypeDao
import com.playground.navigationwithtabs.model.CategoryModel

class TaskRepository(private val taskDao: TaskDao, private val taskTypeDao: TaskTypeDao) {

    val taskTypeNames: LiveData<List<String>> =
        taskTypeDao.taskTypes.map { taskTypes ->
            taskTypes.map { taskType ->
                taskType.name
            }
        }

    val taskTypes: LiveData<List<CategoryModel>> =
        taskTypeDao.taskTypes.map { taskTypes ->
            taskTypes.map { taskType ->
                mapTaskTypeToCategory(taskType)
            }
        }

    suspend fun upsertTaskType(name: String) {
        taskTypeDao.insertTaskTypes(TaskType(name))
    }

    fun tasksForType(type: String) = taskDao.tasksForType(type)

    suspend fun getTasksForType(type: String) = taskDao.getTasks(type)

    suspend fun getTask(taskId: Int) = taskDao.getTask(taskId).firstOrNull()

    suspend fun upsertTask(task: Task) {
        taskDao.upsertTasks(task)
    }

    suspend fun deleteCategories(categories: List<TaskType>) {
        taskTypeDao.deleteCategories(categories)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    private fun mapTaskTypeToCategory(type: TaskType) = CategoryModel(type.name, false)
}