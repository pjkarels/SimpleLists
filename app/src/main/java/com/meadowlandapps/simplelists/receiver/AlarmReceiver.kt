package com.meadowlandapps.simplelists.receiver

import BUNDLE_KEY_CATEGORY_ID
import BUNDLE_KEY_CATEGORY_NAME
import BUNDLE_KEY_ITEM_ID
import BUNDLE_KEY_ITEM_NAME
import CHANNEL_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.ui.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val categoryId = intent.getLongExtra(BUNDLE_KEY_CATEGORY_ID, 0)
        val categoryName = intent.getStringExtra(BUNDLE_KEY_CATEGORY_NAME)
        val itemId = intent.getStringExtra(BUNDLE_KEY_ITEM_ID)
        val itemName = intent.getStringExtra(BUNDLE_KEY_ITEM_NAME)

        val args = Bundle().apply {
            putLong(BUNDLE_KEY_CATEGORY_ID, categoryId)
            putString(BUNDLE_KEY_ITEM_ID, itemId)
        }

        val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.app_navigation_graph)
                .setDestination(R.id.addTaskFragment)
                .setArguments(args)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check_list_48)
            .setContentTitle(itemName)
            .setContentText(categoryName)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)


        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            // todo: make unique
            notify(99, builder.build())
        }
    }
}