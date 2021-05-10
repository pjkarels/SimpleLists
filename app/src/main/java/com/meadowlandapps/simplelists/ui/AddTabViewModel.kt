package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.db.TaskType
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTabViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _nameErrorMsg = MutableLiveData<String>()
    val nameErrorMsg: LiveData<String> get() = _nameErrorMsg

    lateinit var category: TaskType
    private val _categoryLiveData = MutableLiveData<TaskType>()
    val categoryLiveData: LiveData<TaskType> get() = _categoryLiveData

    init {
        val db = AppDatabase.getDatabase(application)
        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    fun upsertCategory(): Boolean {
        if (category.name.isEmpty() || category.name.isBlank()) {
            _nameErrorMsg.value = getApplication<Application>().getString(R.string.error_msg_name_required)
            return false
        }
        viewModelScope.launch {
            if (category.id == 0L) {
                repository.insertTaskType(category)
            } else {
                repository.updateTaskType(category)
            }
        }

        return true
    }

    fun getCategory(id: Long) {
        viewModelScope.launch {
            var categoryFromRepo = repository.getCategory(id)
            if (categoryFromRepo == null) {
                // doesn't exist yet so create new
                // id of 0 tells Room it's an 'Insert'
                categoryFromRepo = TaskType(0, "")
            }
            category = categoryFromRepo
            _categoryLiveData.value = category
        }
    }
}