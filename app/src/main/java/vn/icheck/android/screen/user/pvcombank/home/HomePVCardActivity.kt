package vn.icheck.android.screen.user.pvcombank.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_detail_pvcard.*
import kotlinx.android.synthetic.main.toolbar_pvcombank.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.Status
import vn.icheck.android.screen.user.contact.ContactActivity
import vn.icheck.android.screen.user.newslistv2.NewsListV2Activity
import vn.icheck.android.screen.user.pvcombank.cardhistory.HistoryPVCardActivity
import vn.icheck.android.screen.user.pvcombank.listcard.ListPVCardActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class HomePVCardActivity : BaseActivityMVVM(), View.OnClickListener {
    private val pvCardPreferences = "pvCardPreference"
    private val keyPVCardPreferences = "keypvCardPreference"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    lateinit var viewModel: HomePVCardViewModel
    private var avlBalance = ""

    companion object {
        var redirectUrl: String? = null
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pvcard)

        viewModel = ViewModelProvider(this).get(HomePVCardViewModel::class.java)
        initView()
        initData()
        getDetailCard()
    }

    @SuppressLint("CommitPrefEdits")
    private fun initView() {
        sharedPreferences = getSharedPreferences(pvCardPreferences, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        WidgetUtils.setClickListener(this, imgBack, tvEye, tvNapTien, tvDanhSachThe, tvSuaTaiKhoan, btnSpecialOffers, btnUsagePolicy, btnContact, btnHistory, btnKyc)
    }

    private fun initData() {
        viewModel.onUrl.observe(this, Observer {
            WebViewActivity.start(this, it.description)
        })

        viewModel.onState.observe(this, Observer {
            when (it.type) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })
    }

    private fun getDetailCard() {
        viewModel.getDetailCardV2().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    viewModel.onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))
                }
                Status.ERROR_NETWORK -> {
                    viewModel.onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                    viewModel.onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
                }
                Status.ERROR_REQUEST -> {
                    viewModel.onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                    viewModel.onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getError(it.message)))
                }
                Status.SUCCESS -> {
                    viewModel.onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                    avlBalance = it.data?.data?.rows?.firstOrNull()?.avlBalance ?: "0"
                    setTvMoney()
                }
            }
        })
    }

    private fun setTvMoney() {
        if (sharedPreferences.getBoolean(keyPVCardPreferences, true)) {
            tvEye.setImageResource(R.drawable.ic_eye_on_white_24px)
            tvMoney.setText(R.string.s_space_d, TextHelper.formatMoney(avlBalance))
        } else {
            tvEye.setImageResource(R.drawable.ic_eye_off_white_24px)
            tvMoney.text = "*****"
        }
    }

    private fun getKyc() {
        viewModel.getKyc().observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    DialogHelper.showLoading(this@HomePVCardActivity)
                }
                Status.ERROR_NETWORK -> {
                    DialogHelper.closeLoading(this@HomePVCardActivity)
                    showLongError(ICheckApplication.getError(it.message))
                }
                Status.ERROR_REQUEST -> {
                    DialogHelper.closeLoading(this@HomePVCardActivity)
                    showLongError(ICheckApplication.getError(it.message))
                }
                Status.SUCCESS -> {
                    DialogHelper.closeLoading(this@HomePVCardActivity)
                    if (!it.data?.data?.kycUrl.isNullOrEmpty()) {
                        redirectUrl = it.data?.data?.redirectUrl
                        WebViewActivity.start(this, it.data?.data?.kycUrl)
                    } else {
                        showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.tvNapTien -> {
                ToastUtils.showShortError(this, R.string.tinh_nang_dang_phat_trien)
            }
            R.id.tvDanhSachThe -> {
                startActivity<ListPVCardActivity>()
            }
            R.id.tvSuaTaiKhoan -> {
                ToastUtils.showShortError(this, R.string.tinh_nang_dang_phat_trien)
            }
            R.id.tvTaoThemThe -> {
                ToastUtils.showShortError(this, R.string.tinh_nang_dang_phat_trien)
            }
            R.id.btnSpecialOffers -> {
                startActivity<NewsListV2Activity>()
            }
            R.id.btnUsagePolicy -> {
                viewModel.getUrlUsagePolicy()
            }
            R.id.btnContact -> {
                startActivity<ContactActivity>()
            }
            R.id.btnHistory -> {
                startActivity<HistoryPVCardActivity>()
            }
            R.id.btnKyc -> {
                getKyc()
            }
            R.id.tvEye -> {
                editor.putBoolean(keyPVCardPreferences, !sharedPreferences.getBoolean(keyPVCardPreferences, true))
                editor.commit()
                setTvMoney()
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            ICMessageEvent.Type.ON_KYC_SUCCESS -> {

            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_DESTROY_PVCOMBANK))
    }
}