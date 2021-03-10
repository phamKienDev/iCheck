package vn.icheck.android.loyalty.screen.url_gift_detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_url_gift_detail.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class UrlGiftDetailActivity : BaseActivityGame(), CampaignLoyaltyHelper.ILoginListener {

    private val viewModel by viewModels<UrlGiftDetailViewModel>()

    override val getLayoutID: Int
        get() = R.layout.activity_url_gift_detail

    override fun onInitView() {
        viewModel.getDataIntent(intent)

        initListener()

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            showLongError(it.title)
        })

        viewModel.onSuccess.observe(this, Observer { obj ->

            btnCheckCode.setOnClickListener {
                CampaignLoyaltyHelper.checkCodeLoyalty(this@UrlGiftDetailActivity, obj, edittext.text.toString().trim(), viewModel.barcode,
                        object : CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener {
                            override fun onRemoveHolderInput() {

                            }
                        }, this@UrlGiftDetailActivity)
                edittext.setText("")
            }

            titleCampaign.text = obj.name ?: getString(R.string.dang_cap_nhat)

            SharedLoyaltyHelper(this@UrlGiftDetailActivity).putString(ConstantsLoyalty.OWNER_NAME, obj.owner?.name)

            WidgetHelper.loadImageUrl(bannerCampaign, obj.image?.original)

            if (!obj.description.isNullOrEmpty()) {
                des.settings.javaScriptEnabled = true
                des.loadData(obj.description, "text/html; charset=utf-8", "UTF-8")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == CampaignLoyaltyHelper.REQUEST_CHECK_CODE) {
                if (viewModel.obj != null) {
                    CampaignLoyaltyHelper.checkCodeLoyalty(this@UrlGiftDetailActivity, viewModel.obj!!, viewModel.code, viewModel.barcode,
                            object : CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener {
                                override fun onRemoveHolderInput() {

                                }
                            }, this@UrlGiftDetailActivity)
                }
            }
        }
    }

    override fun showDialogLogin(data: ICKLoyalty, code: String?) {
        viewModel.code = code ?: ""
        LoyaltySdk.openActivityLogin(data, viewModel.code)
    }
}