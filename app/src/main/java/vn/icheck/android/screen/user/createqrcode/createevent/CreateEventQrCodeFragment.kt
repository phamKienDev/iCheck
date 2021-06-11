package vn.icheck.android.screen.user.createqrcode.createevent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_event_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.screen.user.createqrcode.createevent.presenter.CreateEventQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createevent.view.ICreateEventQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateEventQrCodeFragment : BaseFragmentMVVM(), ICreateEventQrCodeView {

    val presenter = CreateEventQrCodePresenter(this@CreateEventQrCodeFragment)

    private val requestNew = 1

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_event_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        initListener()
        focusView(edtEvent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_su_kien)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initListener() {
        edtStartDate.setOnClickListener {
            TimeHelper.dateTimePicker(context, edtStartDate.text.toString(), TimeHelper.getCurrentDateTime(), edtEndDate.text.toString(), object : DateTimePickerListener {
                override fun onSelected(dateTime: String, milliseconds: Long) {
                    edtStartDate.setText(dateTime)
                }
            })
        }

        edtEndDate.setOnClickListener {
            TimeHelper.dateTimePicker(context, edtStartDate.text.toString(), edtStartDate.text.toString(), null, object : DateTimePickerListener {
                override fun onSelected(dateTime: String, milliseconds: Long) {
                    edtEndDate.setText(dateTime)
                }
            })
        }

        btnCreate.setOnClickListener {
            presenter.validData(edtEvent.text.toString().trim(), edtAddress.text.toString().trim(),
                    edtEventLink.text.toString().trim(), edtStartDate.text.toString().trim(),
                    edtEndDate.text.toString().trim())
        }
    }

    override fun onInvalidEventNameSuccess() {
        tvMessageEvent.visibility = View.GONE
        edtEvent.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
    }

    override fun onInvalidEventName(error: String) {
        tvMessageEvent.visibility = View.VISIBLE
        tvMessageEvent.text = error
        edtEvent.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
        edtEvent.requestFocus()
    }

    override fun onInvalidEventAddressSuccess() {
        tvMessageAddress.visibility = View.GONE
        edtAddress.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
    }

    override fun onInvalidEventAddress(error: String) {
        tvMessageAddress.visibility = View.VISIBLE
        tvMessageAddress.text = error
        edtAddress.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
        edtAddress.requestFocus()
    }

    override fun onInvalidEventLinkSuccess() {
        tvMessageEventLink.visibility = View.GONE
        edtEventLink.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
    }

    override fun onInvalidEventLink(error: String) {
        tvMessageEventLink.visibility = View.VISIBLE
        tvMessageEventLink.text = error
        edtEventLink.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
        edtEventLink.requestFocus()
    }

    override fun onInvalidStartDateSuccess() {
        tvMessageStartDate.visibility = View.GONE
        edtStartDate.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
    }

    override fun onInvalidStartDate(error: String) {
        tvMessageStartDate.visibility = View.VISIBLE
        tvMessageStartDate.text = error
        edtStartDate.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
    }

    override fun onInvalidEndDateSuccess() {
        tvMessageEndDate.visibility = View.GONE
        edtEndDate.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
    }

    override fun onInvalidEndDate(error: String) {
        tvMessageEndDate.visibility = View.VISIBLE
        tvMessageEndDate.text = error
        edtEndDate.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
    }

    override fun onValidSuccess(text: String) {
        tvMessageEvent.visibility = View.GONE
        tvMessageAddress.visibility = View.GONE
        tvMessageEventLink.visibility = View.GONE
        tvMessageStartDate.visibility = View.GONE
        tvMessageEndDate.visibility = View.GONE

        edtEvent.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
        edtAddress.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
        edtEventLink.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
        edtStartDate.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)
        edtEndDate.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_gray_solid_white)

        KeyboardUtils.hideSoftInput(edtEvent)

        val intent = Intent(requireContext(), CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1, text)
        startActivityForResult(intent, requestNew)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestNew) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.onBackPressed()
            }
        }
    }

    override fun showError(errorMessage: String) {
        requireContext().showLongErrorToast(errorMessage)
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