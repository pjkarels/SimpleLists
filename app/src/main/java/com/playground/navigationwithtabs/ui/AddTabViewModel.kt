package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.playground.navigationwithtabs.R
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTabViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _nameErrorMsg = MutableLiveData<String>()
    val nameErrorMsg: LiveData<String> get() = _nameErrorMsg

    init {
        val db = AppDatabase.getDatabase(application)
        val taskTypeDao = db.taskTypeDao()
        val taskDao = db.taskDao()

        repository = TaskRepository(taskDao, taskTypeDao)
    }

    fun addTab(name: String): Boolean {
        if (name.isEmpty() || name.isBlank()) {
            _nameErrorMsg.value = getApplication<Application>().getString(R.string.error_msg_name_required)
            return false
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertTaskType(name)
        }
        return true
    }
}