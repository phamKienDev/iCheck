package vn.icheck.android.screen.user.contact

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.databinding.ActivityContactBinding
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.*

class ContactActivity : BaseActivityMVVM() {
    private lateinit var binding: ActivityContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TrackingAllHelper.trackContactViewed()
        binding.header.tvTitle simpleText "Liên hệ và hỗ trợ"
        binding.header.icBack.setOnClickListener {
            finish()
        }
        SettingHelper.getSystemSetting(null, "app-contact", object : ISettingListener {
            override fun onRequestError(error: String) {
                logDebug(error)
            }

            override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                logDebug(list.toString())
                if (list != null) {
                    for (item in list) {
                        when (item.key) {
                            "app-contact.fb" -> {
                                binding.fb simpleText item.value
                                binding.fb.setOnClickListener {
                                    if (job == null) {
                                        job = lifecycleScope.launch {
                                            openFb(item.value)
                                            delay(200)
                                        }
                                    } else if (job?.isActive == false) {
                                        job = lifecycleScope.launch {
                                            openFb(item.value)
                                            delay(200)
                                        }
                                    }
                                }
                            }
                            "app-contact.mail" -> {
                                binding.email simpleText item.value
                                binding.email.setOnClickListener {
                                    if (job == null) {
                                        job = lifecycleScope.launch {
                                            item.value?.startSentEmail()
                                            delay(200)
                                        }
                                    } else if (job?.isActive == false) {
                                        job = lifecycleScope.launch {
                                            item.value?.startSentEmail()
                                            delay(200)
                                        }
                                    }
                                }
                            }
                            "app-contact.phone" -> {
                                binding.phone simpleText item.value
                                binding.phone.setOnClickListener {
                                    if (job == null) {
                                        job = lifecycleScope.launch {
                                            makeCall(item.value)
                                            delay(200)
                                        }
                                    } else if (job?.isActive == false) {
                                        job = lifecycleScope.launch {
                                            makeCall(item.value)
                                            delay(200)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    binding.fb simpleText "fb://page/1156658901065065"
                    binding.fb.setOnClickListener {
                        if (job == null) {
                            job = lifecycleScope.launch {
                                openFb("fb://page/1156658901065065")
                                delay(200)
                            }
                        } else if (job?.isActive == false) {
                            job = lifecycleScope.launch {
                                openFb("fb://page/1156658901065065")
                                delay(200)
                            }
                        }
                    }
                    binding.email simpleText "cskh@icheck.vn"
                    binding.email.setOnClickListener {
                        if (job == null) {
                            job = lifecycleScope.launch {
                                "cskh@icheck.vn".startSentEmail()
                                delay(200)
                            }
                        } else if (job?.isActive == false) {
                            job = lifecycleScope.launch {
                                "cskh@icheck.vn".startSentEmail()
                                delay(200)
                            }
                        }
                    }
                    binding.phone simpleText "0902195488"
                    binding.phone.setOnClickListener {
                        if (job == null) {
                            job = lifecycleScope.launch {
                                makeCall("0902195488")
                                delay(200)
                            }
                        } else if (job?.isActive == false) {
                            job = lifecycleScope.launch {
                                makeCall("0902195488")
                                delay(200)
                            }
                        }
                    }
                }
            }
        })
    }
}