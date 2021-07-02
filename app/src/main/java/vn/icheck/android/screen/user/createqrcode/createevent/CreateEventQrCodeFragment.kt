package vn.icheck.android.screen.user.createqrcode.createevent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_event_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
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

        edtStartDate.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext())
        edtEndDate.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext())
        edtEvent.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext())
        edtAddress.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext())
        edtEventLink.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext())

        vn.icheck.android.ichecklibs.ColorManager.getDisableTextColor(requireContext()).apply {
            edtStartDate.setHintTextColor(this)
            edtEndDate.setHintTextColor(this)
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
            presenter.validData(
                edtEvent.text.toString().trim(), edtAddress.text.toString().trim(),
                edtEventLink.text.toString().trim(), edtStartDate.text.toString().trim(),
                edtEndDate.text.toString().trim()
            )
        }
    }

    override fun onInvalidEventNameSuccess() {
        tvMessageEvent.visibility = View.GONE
        edtEvent.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEvent.context)
    }

    override fun onInvalidEventName(error: String) {
        tvMessageEvent.visibility = View.VISIBLE
        tvMessageEvent.text = error
        edtEvent.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtEvent.requestFocus()
    }

    override fun onInvalidEventAddressSuccess() {
        tvMessageAddress.visibility = View.GONE
        edtAddress.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtAddress.context)
    }

    override fun onInvalidEventAddress(error: String) {
        tvMessageAddress.visibility = View.VISIBLE
        tvMessageAddress.text = error
        edtAddress.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtAddress.requestFocus()
    }

    override fun onInvalidEventLinkSuccess() {
        tvMessageEventLink.visibility = View.GONE
        edtEventLink.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEventLink.context)
    }

    override fun onInvalidEventLink(error: String) {
        tvMessageEventLink.visibility = View.VISIBLE
        tvMessageEventLink.text = error
        edtEventLink.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtEventLink.requestFocus()
    }

    override fun onInvalidStartDateSuccess() {
        tvMessageStartDate.visibility = View.GONE
        edtStartDate.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtStartDate.context)
    }

    override fun onInvalidStartDate(error: String) {
        tvMessageStartDate.visibility = View.VISIBLE
        tvMessageStartDate.text = error
        edtStartDate.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
    }

    override fun onInvalidEndDateSuccess() {
        tvMessageEndDate.visibility = View.GONE
        edtEndDate.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEndDate.context)
    }

    override fun onInvalidEndDate(error: String) {
        tvMessageEndDate.visibility = View.VISIBLE
        tvMessageEndDate.text = error
        edtEndDate.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
    }

    override fun onValidSuccess(text: String) {
        tvMessageEvent.visibility = View.GONE
        tvMessageAddress.visibility = View.GONE
        tvMessageEventLink.visibility = View.GONE
        tvMessageStartDate.visibility = View.GONE
        tvMessageEndDate.visibility = View.GONE

        edtEvent.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEvent.context)
        edtAddress.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEvent.context)
        edtEventLink.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEvent.context)
        edtStartDate.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEvent.context)
        edtEndDate.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEvent.context)

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