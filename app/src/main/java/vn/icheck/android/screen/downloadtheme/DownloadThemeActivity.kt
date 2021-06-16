package vn.icheck.android.screen.downloadtheme

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_download_theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.screen.user.welcome.WelcomeActivity
import vn.icheck.android.util.kotlin.StatusBarUtils
import java.io.File

/**
 * Created by VuLCL on 3/5/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class DownloadThemeActivity : BaseActivityMVVM() {
    private var timeUpdate = 0L
    private lateinit var path: String
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_theme)
        onInitView()
    }

    fun onInitView() {
        StatusBarUtils.setOverStatusBarLight(this)

        val link = intent?.getStringExtra(Constant.DATA_1) ?: ""
        timeUpdate = intent?.getLongExtra(Constant.DATA_2, 0) ?: 0L
        path = FileHelper.getPath(applicationContext)

        seekBar.setPadding(0, 0, 0, 0)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                tvCount.text = ("$progress%")

                val percent = seekBar.width / 100
                val margin = progress * percent

                viewPosition.setPadding(margin + (tvCount.width / 2), 0, 0, 0)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        tvSkip.setOnClickListener {
//            SettingManager.setThemeUpdateTime(timeUpdate)
//            SettingManager.setSettingTheme(null)
            startActivityAndFinish<WelcomeActivity>()
        }

        downloadFileFromUrl(link, timeUpdate)
    }

    private fun downloadFileFromUrl(url: String, timeUpdate: Long) {
        val folder = File(path)

        if (folder.isDirectory) {
            deleteFolder(folder)
        } else {
            folder.delete()
        }

        val file = File(path + FileHelper.iCheckThemeName)

        val request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN) // Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file)) // Uri of the destination file
                .setTitle(FileHelper.iCheckThemeName) // Title of the Download Notification
                .setDescription("Downloading") // Description of the Download Notification
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true) // Set if download is allowed on roaming network


        seekBar.max = 100
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request) // enqueue puts the download request in the queue.
        checkDownload(downloadID)

//        lifecycleScope.launch {
//            delay(200)
//
//            seekBar.max = 100
//            var isSuccess = false
//            var finishDownload = false
//
//            while (!finishDownload) {
//                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
//                if (cursor.moveToFirst()) {
//                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
//                        DownloadManager.STATUS_FAILED -> {
//                            finishDownload = true
//                            isSuccess = false
//                        }
//                        DownloadManager.STATUS_RUNNING -> {
//                            val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                            if (total >= 0) {
//                                delay(10)
//                                val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                                seekBar.progress = (downloaded * 100 / total).toInt()
//                            }
//                        }
//                        DownloadManager.STATUS_SUCCESSFUL -> {
//                            seekBar.progress = 100
//                            finishDownload = true
//                            isSuccess = true
//                        }
//                        else -> {
//                            finishDownload = true
//                        }
//                    }
//                }
//            }
//
//            finishDownload(isSuccess)
//        }
    }

    private fun checkDownload(downloadID: Long) {
        lifecycleScope.launch {
            delay(50)

            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload(false)
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            seekBar.progress = (downloaded * 100 / total).toInt()
                        }
                        checkDownload(downloadID)
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        seekBar.progress = 100
                        finishDownload(true)
                    }
                    else -> {
                        checkDownload(downloadID)
                    }
                }
            }
        }
    }

    private fun finishDownload(isSuccess: Boolean) {
        lifecycleScope.launch {
            if (isSuccess) {
                val isUnzipSuccess = FileHelper.unzipFile(path, FileHelper.iCheckThemeName)

                if (isUnzipSuccess) {
//                    SettingManager.setThemeUpdateTime(timeUpdate)
//                    val settingTheme = FileHelper.getTextFromFile(path, "style.json")
//                    SettingManager.setSettingTheme(settingTheme)
                }
            }

            delay(400)
            startActivityAndFinish<WelcomeActivity>()
        }
    }

    private fun deleteFolder(folder: File) {
        val list = folder.listFiles() ?: arrayOf<File>()

        for (i in list.size - 1 downTo 0) {
            if (list[i].isDirectory) {
                deleteFolder(list[i])
            } else {
                list[i].delete()
            }
        }

        folder.delete()
    }
}