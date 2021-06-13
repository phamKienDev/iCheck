package vn.icheck.android.screen.user.createqrcode.createlink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_link_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.createqrcode.base.presenter.BaseCreateQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 10/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateLinkQrCodeFragment : BaseFragmentMVVM(), IBaseCreateQrCodeView {

    val presenter = BaseCreateQrCodePresenter(this@CreateLinkQrCodeFragment)

    private val requestNew = 1

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_link_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        initListener()
        focusView(edtContent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_duong_link)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initListener() {
        btnCreate.setOnClickListener {
            presenter.validLink(edtContent.text.toString().trim())
        }
    }

    override fun onValidSuccess(text: String) {
        tvMessage.visibility = View.GONE
        edtContent.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_gray_solid_white)
        KeyboardUtils.hideSoftInput(edtContent)
        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestNew)
    }

    override fun showError(errorMessage: String) {
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = errorMessage
        edtContent.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_stroke_red_4)
        edtContent.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == requestNew){
                activity?.onBackPressed()
            }
        }
    }

    override val mContext: Context?
        get() = requireContext()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.BACK) {
            activity?.onBackPressed()
        }
    }
}