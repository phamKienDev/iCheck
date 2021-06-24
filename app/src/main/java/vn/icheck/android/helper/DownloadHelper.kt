package vn.icheck.android.helper

import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import java.io.File


class DownloadHelper(private val downloadManager: DownloadManager, val activity: Activity, val callback: DownloadHelperCallback) {

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            callback.downloadSuccess()
        }
    }


    private var onNotificationClick: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, context.getString(R.string.the_download_notification_was_clicked), Toast.LENGTH_LONG).show()
        }
    }

    fun register() {
        activity.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        activity.registerReceiver(onNotificationClick, IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED))
    }

    fun startDownload(url: String?): Long {
        if (url.isNullOrEmpty()) {
            return -1
        }

        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)


        if (folder.exists()) {
            folder.mkdirs()
        }

        val fileName = getImageName()
        val file = File(folder, fileName)

        if (file.exists()) {
            return -1
        }

        val request = DownloadManager.Request(Uri.parse(url))

                // Title of the Download Notification
                .setTitle( getString(R.string.app_name))

                // Description of the Download Notification
                .setDescription( getString(R.string.dang_tai_tep))

                // Visibility of the download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

                // Set the local destination for the downloaded file to a path
                // within the application's external files directory
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        // Enqueue download and save into referenceId-
        return downloadManager.enqueue(request)
    }

    private fun getImageName(): String {
        return System.currentTimeMillis().toString() + ".jpg"
    }

    fun getStatusMessage(downloadId: Long): String {

        val query = DownloadManager.Query()
        // set the query filter to our previously Enqueued download
        query.setFilterById(downloadId)

        // Query the download manager about downloads that have been requested.
        val cursor = downloadManager.query(query)
        if (cursor?.moveToFirst() == true) {
            return downloadStatus(cursor)
        }
        return "NO_STATUS_INFO"
    }

    private fun downloadStatus(cursor: Cursor): String {

        // column for download  status
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnIndex)
        // column for reason code if the download failed or paused
        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        val reason = cursor.getInt(columnReason)

        var statusText = ""
        var reasonText = ""

        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
                callback.downloadError()
                when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> reasonText = "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> reasonText = "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> reasonText = "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> reasonText = "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> reasonText = "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> reasonText = "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> reasonText = "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> reasonText = "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> reasonText = "ERROR_UNKNOWN"
                }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> reasonText = "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> reasonText = "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> reasonText = "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> reasonText = "PAUSED_WAITING_TO_RETRY"
                }
            }
            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> statusText = "STATUS_SUCCESSFUL"
        }

        return "Download Status: $statusText, $reasonText"
    }

    fun destroyActivity() {
        activity.unregisterReceiver(onComplete)
        activity.unregisterReceiver(onNotificationClick)
    }

    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }

    interface DownloadHelperCallback {
        fun downloadSuccess()
        fun downloadError()
    }
}