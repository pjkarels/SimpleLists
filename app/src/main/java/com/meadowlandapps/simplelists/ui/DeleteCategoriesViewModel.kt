package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _isEditButtonEnabled = MutableLiveData(false)
    val isEditButtonEnabled: LiveData<Boolean> = _isEditButtonEnabled

    private val _isDeleteButtonEnabled = MutableLiveData(false)
    val isDeleteButtonEnabled: LiveData<Boolean> = _isDeleteButtonEnabled

    val categoriesLiveData = repository.taskTypes

    private val selectedCategories = mutableListOf<CategoryModel>()

    fun checkedChanged(isChecked: Boolean, category: CategoryModel) {
        if (isChecked) {
            selectedCategories.add(category)
        } else {
            selectedCategories.remove(category)
        }
        when (selectedCategories.size) {
            0 -> {
                _isEditButtonEnabled.value = false
                _isDeleteButtonEnabled.value = false
            }
            1 -> {
                _isEditButtonEnabled.value = true
                _isDeleteButtonEnabled.value = true
            }
            else -> {
                _isDeleteButtonEnabled.value = true
                _isEditButtonEnabled.value = false
            }
        }
    }

    fun deleteSelectedCategories() {
        viewModelScope.launch {
            val tasksToDelete = selectedCategories.map { categoryModel ->
                mapCategoryModelToTaskType(categoryModel)
            }
            repository.deleteCategories(tasksToDelete)
        }
    }

    private fun mapCategoryModelToTaskType(category: CategoryModel) = TaskType(category.name)
}