package com.playground.navigationwithtabs.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.playground.navigationwithtabs.db.AppDatabase
import com.playground.navigationwithtabs.db.Task
import com.playground.navigationwithtabs.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TaskRepository

    lateinit var task: Task
    private val _taskLiveData = MutableLiveData<Task>()
    val taskLiveData: LiveData<Task> get() = _taskLiveData

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    fun upsertTask() {
        viewModelScope.launch {
            repository.upsertTask(task)
        }
    }

    fun getTask(taskId: Int, taskType: String) {
        viewModelScope.launch {
            var taskFromRepo = repository.getTask(taskId)
            if (taskFromRepo == null) {
                // doesn't exist yet so create new
                taskFromRepo = Task(0, "", taskType, false)
            }
            task = taskFromRepo
            _taskLiveData.value = task
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTaskCompleteness() {
        task.completed = !task.completed
        viewModelScope.launch {
            upsertTask()
            getTask(task.id, task.type)
        }
    }
}