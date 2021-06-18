package com.bitsandbogs.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bitsandbogs.simplelists.db.AppDatabase
import com.bitsandbogs.simplelists.model.CategoryModel
import com.bitsandbogs.simplelists.repository.TaskRepository

class EditCategoriesViewModel(application: Application) : AndroidViewModel(application) {

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

    val selectedCategories = mutableListOf<CategoryModel>()

    fun checkedChanged(isChecked: Boolean, category: CategoryModel) {
        if (isChecked) {
            selectedCategories.add(category)
        } else {
            selectedCategories.remove(category)
        }
        onSelectedCategoriesChanged()
    }

    fun onSelectedCategoriesChanged() {
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
}