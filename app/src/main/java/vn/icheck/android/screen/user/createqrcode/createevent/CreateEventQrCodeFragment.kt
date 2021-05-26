package vn.icheck.android.screen.user.createqrcode.createevent

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_event_qr_code.*
import vn.icheck.android.R
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.createqrcode.base.fragment.BaseCreateQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createevent.presenter.CreateEventQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createevent.view.ICreateEventQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateEventQrCodeFragment : BaseCreateQrCodeFragment<CreateEventQrCodePresenter>(), ICreateEventQrCodeView {

    override val getLayoutID: Int
        get() = R.layout.fragment_create_event_qr_code

    override val getPresenter: CreateEventQrCodePresenter
        get() = CreateEventQrCodePresenter(this)

    private val requestNew = 1

    override fun onInitView() {
        initToolbar()
        setupView()
        initListener()
        focusView(edtEvent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_su_kien)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
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
        edtEvent.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEvent.context)
    }

    override fun onInvalidEventName(error: String) {
        tvMessageEvent.visibility = View.VISIBLE
        tvMessageEvent.text = error
        edtEvent.background = ViewHelper.bgTransparentRadius4StrokeAccentRed05(requireContext())
        edtEvent.requestFocus()
    }

    override fun onInvalidEventAddressSuccess() {
        tvMessageAddress.visibility = View.GONE
        edtAddress.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtAddress.context)
    }

    override fun onInvalidEventAddress(error: String) {
        tvMessageAddress.visibility = View.VISIBLE
        tvMessageAddress.text = error
        edtAddress.background = ViewHelper.bgTransparentRadius4StrokeAccentRed05(requireContext())
        edtAddress.requestFocus()
    }

    override fun onInvalidEventLinkSuccess() {
        tvMessageEventLink.visibility = View.GONE
        edtEventLink.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEventLink.context)
    }

    override fun onInvalidEventLink(error: String) {
        tvMessageEventLink.visibility = View.VISIBLE
        tvMessageEventLink.text = error
        edtEventLink.background = ViewHelper.bgTransparentRadius4StrokeAccentRed05(requireContext())
        edtEventLink.requestFocus()
    }

    override fun onInvalidStartDateSuccess() {
        tvMessageStartDate.visibility = View.GONE
        edtStartDate.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtStartDate.context)
    }

    override fun onInvalidStartDate(error: String) {
        tvMessageStartDate.visibility = View.VISIBLE
        tvMessageStartDate.text = error
        edtStartDate.background = ViewHelper.bgTransparentRadius4StrokeAccentRed05(requireContext())
    }

    override fun onInvalidEndDateSuccess() {
        tvMessageEndDate.visibility = View.GONE
        edtEndDate.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEndDate.context)
    }

    override fun onInvalidEndDate(error: String) {
        tvMessageEndDate.visibility = View.VISIBLE
        tvMessageEndDate.text = error
        edtEndDate.background = ViewHelper.bgTransparentRadius4StrokeAccentRed05(requireContext())
    }

    override fun onValidSuccess(text: String) {
        tvMessageEvent.visibility = View.GONE
        tvMessageAddress.visibility = View.GONE
        tvMessageEventLink.visibility = View.GONE
        tvMessageStartDate.visibility = View.GONE
        tvMessageEndDate.visibility = View.GONE

        edtEvent.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEvent.context)
        edtAddress.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEvent.context)
        edtEventLink.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEvent.context)
        edtStartDate.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEvent.context)
        edtEndDate.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtEvent.context)

        KeyboardUtils.hideSoftInput(edtEvent)

        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestNew)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showLongError(errorMessage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestNew) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.onBackPressed()
            }
        }
    }
}