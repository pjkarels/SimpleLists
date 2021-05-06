package com.meadowlandapps.simplelists.db

import androidx.room.DatabaseView

@DatabaseView("SELECT task.id, task.name, task.typeId, task.completed, task.removed, task_type_table.name AS typeString" +
        " FROM task_table AS task" +
        " INNER JOIN task_type_table ON task.typeId = task_type_table.id")
data class TaskDetail(
        val id: Int,
        val name: String = "",
        val typeId: Int = 0,
        val typeString: String = "",
        val completed: Boolean = false,
        val removed: Boolean = false

)
