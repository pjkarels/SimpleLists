package com.meadowlandapps.simplelists.model

import java.util.*

class ItemModel(
        val id: String = UUID.randomUUID().toString(),
        var name: String = "",
        var typeId: Long = 0,
        var category: String = "",
        var completed: Boolean = false,
        var removed: Boolean = false,
        var notifications: MutableList<NotificationModel> = mutableListOf(),
        val isNew: Boolean = true
)
