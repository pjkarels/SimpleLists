package com.meadowlandapps.simplelists.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.db.TaskDao
import com.meadowlandapps.simplelists.db.TaskType
import com.meadowlandapps.simplelists.db.TaskTypeDao
import com.meadowlandapps.simplelists.model.CategoryModel

class TaskRepository(private val taskDao: TaskDao, private val taskTypeDao: TaskTypeDao) {

    val listTitles: LiveData<List<String>> =
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

    val removedItems: LiveData<List<Task>> = taskDao.removedTasks()

    suspend fun upsertTaskType(name: String) {
        taskTypeDao.insertTaskTypes(TaskType(name))
    }

    fun tasksForType(type: String) = taskDao.tasksForType(type)

    suspend fun getItemsForList(listName: String) = taskDao.getItemsForList(listName)

    suspend fun getTask(taskId: Int) = taskDao.getTask(taskId).firstOrNull()

    suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    suspend fun upsertItems(items: List<Task>) {
        taskDao.upsertTasks(items)
    }

    suspend fun deleteLists(listNames: List<String>) {
        for (name in listNames) {
            taskTypeDao.deleteCategory(name)
        }
    }

    suspend fun deleteCategories(categories: List<TaskType>) {
        taskTypeDao.deleteCategories(categories)
    }

    suspend fun deleteItems(items: List<Task>) {
        taskDao.deleteItems(items)
    }

    private fun mapTaskTypeToCategory(type: TaskType) = CategoryModel(type.name, false)
}