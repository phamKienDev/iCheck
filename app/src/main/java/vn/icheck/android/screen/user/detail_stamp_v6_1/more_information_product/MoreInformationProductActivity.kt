package vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.View
import kotlinx.android.synthetic.main.activity_more_information_product.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_RESP_InformationProduct
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.presenter.MoreInformationProductPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_information_product.view.IMoreInformationProductView
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.GlideImageGetter

class MoreInformationProductActivity : BaseActivityMVVM(),IMoreInformationProductView {

    val presenter = MoreInformationProductPresenter(this@MoreInformationProductActivity)

    private var dataHtml = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_information_product)
        onInitView()
    }

    fun onInitView() {
        if (StampDetailActivity.isVietNamLanguage == false){
            txtTitle rText R.string.information
        } else {
            txtTitle rText R.string.thong_tin
        }

        presenter.getDataIntent(intent)
        listener()
    }

    private fun listener(){
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnAgainError.setOnClickListener {
            presenter.getDataIntent(intent)
        }
    }

    override fun onGetDataIntentError(errorType: Int) {
        layoutErrorClient.visibility = View.VISIBLE
        scrollData.visibility = View.GONE
        when(errorType){
            Constant.ERROR_INTERNET -> {
                imgError.setImageResource(R.drawable.ic_error_network)
                if (StampDetailActivity.isVietNamLanguage == false) {
                    tvMessageError rText R.string.checking_network_please_try_again
                } else {
                    tvMessageError.text = getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                }
            }
            Constant.ERROR_UNKNOW -> {
                imgError.setImageResource(R.drawable.ic_error_request)
                if (StampDetailActivity.isVietNamLanguage == false) {
                    tvMessageError rText R.string.occurred_please_try_again
                } else {
                    tvMessageError.text = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }
            Constant.ERROR_EMPTY -> {
                imgError.setImageResource(R.drawable.ic_error_request)
                if (StampDetailActivity.isVietNamLanguage == false) {
                    tvMessageError rText R.string.occurred_please_try_again
                } else {
                    tvMessageError.text = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }
        }
    }

    override fun onGetInforSuccess(data: IC_RESP_InformationProduct.Data) {
        layoutErrorClient.visibility = View.GONE
        scrollData.visibility = View.VISIBLE

        dataHtml = data.content ?: ""

        val imageGetter = GlideImageGetter(tvInformation)

        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(data.content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(data.content, imageGetter, null)
        }
        tvInformation.text = html
        tvInformation.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@MoreInformationProductActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@MoreInformationProductActivity, isShow)
    }
}
