package com.meadowlandapps.simplelists.model

class ItemModel(
        val id: Int,
        var name: String = "",
        var typeId: Int = 0,
        var category: String = "",
        var completed: Boolean,
        var removed: Boolean)
