package com.meadowlandapps.simplelists.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.db.TaskDao
import com.meadowlandapps.simplelists.db.TaskType
import com.meadowlandapps.simplelists.db.TaskTypeDao
import com.meadowlandapps.simplelists.model.CategoryModel

class TaskRepository(private val taskDao: TaskDao, private val taskTypeDao: TaskTypeDao) {

    val taskTypes: LiveData<List<CategoryModel>> =
            taskTypeDao.taskTypes.map { taskTypes ->
                taskTypes.map { taskType ->
                    mapTaskTypeToCategory(taskType)
                }
            }

    val removedItems: LiveData<List<Task>> = taskDao.removedTasks()

    suspend fun insertTaskType(category: TaskType) {
        taskTypeDao.insertTaskTypes(category)
    }

    suspend fun updateTaskType(category: TaskType) {
        taskTypeDao.updateTaskTypes(category)
    }

    suspend fun getCategory(id: Int) = taskTypeDao.getCategory(id).firstOrNull()

    fun tasksForType(id: Int) = taskDao.tasksForType(id)

    suspend fun getItemsForList(id: Int) = taskDao.getItemsForList(id)

    suspend fun getTask(taskId: Int) = taskDao.getTask(taskId).firstOrNull()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }


    suspend fun insertItems(items: List<Task>) {
        taskDao.insertTasks(items)
    }

    suspend fun updateItems(items: List<Task>) {
        taskDao.updateTasks(items)
    }

    fun deleteLists(listNames: List<String>) {
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

    private fun mapTaskTypeToCategory(type: TaskType) = CategoryModel(type.id, type.name, false)
}