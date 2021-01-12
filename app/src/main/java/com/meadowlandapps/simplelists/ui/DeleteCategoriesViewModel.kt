package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.db.TaskType
import com.meadowlandapps.simplelists.model.CategoryModel
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class DeleteCategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    val categoriesLiveData = repository.taskTypes
    val selectedCategoriesToDelete = mutableListOf<CategoryModel>()

    fun deleteSelectedCategories() {
        viewModelScope.launch {
            val tasksToDelete = selectedCategoriesToDelete.map { categoryModel ->
                mapCategoryModelToTaskType(categoryModel)
            }
            repository.deleteCategories(tasksToDelete)
        }
    }

    private fun mapCategoryModelToTaskType(category: CategoryModel) = TaskType(category.name)
}