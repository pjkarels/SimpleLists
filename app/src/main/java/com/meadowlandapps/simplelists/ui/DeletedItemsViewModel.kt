package com.meadowlandapps.simplelists.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.model.ItemModel
import com.meadowlandapps.simplelists.repository.TaskRepository
import kotlinx.coroutines.launch

class DeletedItemsViewModel(application: Application): AndroidViewModel(application) {
    private val _repository: TaskRepository

    init {
        val db = AppDatabase.getDatabase(application)
        _repository = TaskRepository(db.taskDao(), db.taskTypeDao())
    }

    val selectedItems = mutableListOf<ItemModel>()

    val removedItems = _repository.removedItems

    private val _enableButtons = MutableLiveData(false)
    val enableButtons: LiveData<Boolean> get() = _enableButtons

    fun restoreSelectedItems() {
        for (item in selectedItems) {
            item.removed = false
        }
        viewModelScope.launch {
            _repository.updateItems(selectedItems)
        }
    }

    fun checkedChanged(isChecked: Boolean, item: ItemModel) {
        if (isChecked) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }

        onCheckedChanged()
    }

    fun onCheckedChanged() {
        _enableButtons.value = selectedItems.size > 0
    }
}