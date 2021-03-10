package vn.icheck.android.screen.user.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.network.base.SettingManager
import java.io.File

class HomeViewModel : BaseViewModel() {

    fun downloadTheme() {
        SettingManager.themeSetting?.theme?.let { theme ->
            val path = FileHelper.getPath(ICheckApplication.getInstance())

            if (FileHelper.isThemeDownloaded) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_SET_THEME))
            }

            viewModelScope.launch {
                val arrAsync = arrayListOf<Deferred<Any>>()
                var homeBackgroundImage: File? = null
                var homeHeaderImage: File? = null
                var homeIcon: File? = null
                var newsIcon: File? = null
                var historyIcon: File? = null
                var messageIcon: File? = null
                var scanIcon: File? = null

                if (!theme.homeBackgroundImage.isNullOrEmpty()) {
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            homeBackgroundImage = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.homeBackgroundImage)
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
                if (!theme.homeHeaderImage.isNullOrEmpty()) {
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            homeHeaderImage = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.homeHeaderImage)
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }
                if (theme.bottomBarSelectedIcons?.size ?: 0 > 4) {
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            homeIcon = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.bottomBarSelectedIcons!![0])
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            newsIcon = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.bottomBarSelectedIcons!![1])
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            scanIcon = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.bottomBarSelectedIcons!![2])
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            historyIcon = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.bottomBarSelectedIcons!![3])
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    arrAsync.add(async(Dispatchers.IO) {
                        try {
                            messageIcon = Glide.with(ICheckApplication.getInstance())
                                    .asFile()
                                    .timeout(30000)
                                    .load(theme.bottomBarSelectedIcons!![4])
                                    .submit()
                                    .get()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }

                arrAsync.awaitAll()

                if (homeBackgroundImage?.exists() == true ||
                        homeHeaderImage?.exists() == true ||
                        homeIcon?.exists() == true ||
                        newsIcon?.exists() == true ||
                        scanIcon?.exists() == true ||
                        historyIcon?.exists() == true ||
                        messageIcon?.exists() == true) {
                    if (FileHelper.copyFile(homeBackgroundImage!!, File(path + FileHelper.homeBackgroundImage)) ||
                            FileHelper.copyFile(homeHeaderImage!!, File(path + FileHelper.homeHeaderImage)) ||
                            FileHelper.copyFile(homeIcon!!, File(path + FileHelper.homeIcon)) ||
                            FileHelper.copyFile(newsIcon!!, File(path + FileHelper.newsIcon)) ||
                            FileHelper.copyFile(scanIcon!!, File(path + FileHelper.scanIcon)) ||
                            FileHelper.copyFile(historyIcon!!, File(path + FileHelper.historyIcon)) ||
                            FileHelper.copyFile(messageIcon!!, File(path + FileHelper.messageIcon))
                    ) {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_SET_THEME))
                    }
                }
            }
        }
    }
}