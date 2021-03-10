package vn.icheck.android.screen.user.createqrcode.home

import android.view.View
import kotlinx.android.synthetic.main.fragment_create_qr_code.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.createqrcode.content.CreateQrCodeContentActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateQrCodeHomeActivity : BaseActivity<BaseActivityPresenter>(), BaseActivityView, View.OnClickListener {

    override val getLayoutID: Int
        get() = R.layout.fragment_create_qr_code

    override val getPresenter: BaseActivityPresenter
        get() = BaseActivityPresenter(this)

    override fun onInitView() {
        TrackingAllHelper.trackCreateQrcodeViewed()
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.tao_ma_qr)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, txtText, txtLink, txtPhone, txtMessage, txtEmail, txtLocation, txtContact, txtEvent, txtWifi)
    }

    override fun onClick(view: View?) {
        view?.id?.let { id ->
            startActivity<CreateQrCodeContentActivity, Int>(Constant.DATA_1, id)
        }
    }
}