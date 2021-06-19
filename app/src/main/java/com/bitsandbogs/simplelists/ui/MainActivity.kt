package com.bitsandbogs.simplelists.ui

import BUNDLE_KEY_CATEGORY_ID
import BUNDLE_KEY_ITEM_ID
import CHANNEL_ID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bitsandbogs.simplelists.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {
            // serve ads
            val adView = findViewById<AdView>(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        val categoryId = intent.getStringExtra(BUNDLE_KEY_CATEGORY_ID) ?: "0"
        val itemId = intent.getStringExtra(BUNDLE_KEY_ITEM_ID)

        createNotificationChannel()

        if (itemId != null) {
            val action = ViewPagerFragmentDirections.actionTabContentFragmentToEditTaskFragment(
                itemId,
                categoryId.toLong()
            )
            findNavController(R.id.nav_host_fragment).navigate(action)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val navController = findNavController(R.id.nav_host_fragment)
        navController.handleDeepLink(intent)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_title_high)
            val descriptionText = getString(R.string.notification_channel_description_high)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}