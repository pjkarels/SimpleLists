package com.meadowlandapps.simplelists.model

class ItemModel(
        val id: Long = 0,
        var name: String = "",
        var typeId: Long = 0,
        var category: String = "",
        var completed: Boolean = false,
        var removed: Boolean = false,
        var notification: List<NotificationModel> = listOf()
)
