package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.model.CategoryModel
import com.meadowlandapps.simplelists.model.ItemModel
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MoveItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    private lateinit var itemToMove: ItemModel

    lateinit var categories: List<CategoryModel>

    fun getCategories(itemId: String) {
        categories = listOf()
        runBlocking {
            val item = repository.getTask(itemId)
            if (item != null) {
                itemToMove = item
                categories = repository.getCategories().toList()
                categories.first { categoryModel ->
                    item.categoryId == categoryModel.id
                }.selected = true
            }
        }
    }

    fun updateSelectedCategory(category: CategoryModel) {
        // deselect all
        categories.forEach { categoryModel ->
            categoryModel.selected = false
        }
        // select the selected
        categories.first { categoryModel ->
            category.id == categoryModel.id
        }.selected = true
    }

    fun moveItem() {
        val selectedCategory = categories.first { categoryModel ->
            categoryModel.selected
        }
        itemToMove.categoryId = selectedCategory.id
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(itemToMove)
        }
    }
}