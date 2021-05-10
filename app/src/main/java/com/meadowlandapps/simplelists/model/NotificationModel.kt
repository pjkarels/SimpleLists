package com.meadowlandapps.simplelists.model

import java.util.*

class NotificationModel(
        val id: String = UUID.randomUUID().toString(),
        var time: Calendar = Calendar.getInstance()
)