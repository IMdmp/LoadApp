package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var selectedUrl = ""
    private var selectedName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        rg_choices.setOnCheckedChangeListener { radioGroup, checkedId ->
            val item = findViewById<RadioButton>(checkedId)
            Log.d("TAG", "item id: ${item.id}")
            setSelectedUrlBasedOnId(item.id)
        }

        createChannel(
            getString(R.string.load_app_channel_id),
            getString(R.string.load_app_channel_name)
        )
    }

    private fun setSelectedUrlBasedOnId(id: Int) {
        selectedUrl = when (id) {
            R.id.rb_glide -> {
                GLIDE_URL
            }
            R.id.rb_udacity -> {
                GITHUB_URL
            }
            R.id.rb_retrofit -> {
                RETROFIT_URL
            }
            else -> {
                ""
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d("TAG", "got id: $id")
            testNotif()
        }
    }

    private fun testNotif() {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            "Download item complete",
            this,
            selectedName
        )
    }

    private fun download() {
        if (selectedUrl.isEmpty()) {
            showToast("Please select the file to download")
            return
        }
        val request =
            DownloadManager.Request(Uri.parse(selectedUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        setSelectedNameBasedOnUrl(selectedUrl)
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun setSelectedNameBasedOnUrl(selectedUrl: String) {
        selectedName = when(selectedUrl){
            GLIDE_URL->{resources.getString(R.string.glide_image_loading_library_by_bumptech)}
            RETROFIT_URL->{resources.getString(R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc)}
            GITHUB_URL->{resources.getString(R.string.loadapp_current_repository_by_udacity)}
            else->{""}
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val GITHUB_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/v4.11.0.zip"

        private const val RETROFIT_URL =
            "https://search.maven.org/remote_content?g=com.squareup.retrofit2&a=retrofit&v=LATEST"

        private const val CHANNEL_ID = "channelId"
    }

}
