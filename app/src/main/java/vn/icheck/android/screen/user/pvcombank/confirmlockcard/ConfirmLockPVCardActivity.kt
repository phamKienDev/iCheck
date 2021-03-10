package vn.icheck.android.screen.user.pvcombank.confirmlockcard

import android.os.Bundle
import android.text.Editable
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_confirm_lock_pvcard.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.pvcombank.confirmlockcard.viewmodel.ConfirmLockPVCardViewModel
import vn.icheck.android.util.AfterTextWatcher

class ConfirmLockPVCardActivity : BaseActivityMVVM() {

    private val viewModel : ConfirmLockPVCardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_lock_pvcard)
        initView()
        listener()
        initViewModel()
        viewModel.getDataIntent(intent)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    private fun initView() {
        edt_pin.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                checkForm()
            }
        })
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnConfirm.setOnClickListener {
           viewModel.verifyPinCard(edt_pin.text.toString())
        }
    }

    private fun checkForm() {
        if (edt_pin.text?.length ?: 0 >= 6){
            btnConfirm.setBackgroundResource(R.drawable.bg_corners_4_light_blue_solid)
            btnConfirm.isEnabled = true
        } else {
            btnConfirm.setBackgroundResource(R.drawable.bg_corner_gray_topup_4)
            btnConfirm.isEnabled = false
        }
    }

    private fun initViewModel() {
        viewModel.dataLockCard.observe(this,{
            if (!it.verification?.requestId.isNullOrEmpty()) {
                viewModel.requestId = it.verification?.requestId!!
            }

            if (!it.verification?.otpTransId.isNullOrEmpty()) {
                viewModel.otptranid = it.verification?.otpTransId!!
            }
//            val intent = Intent()
//            intent.putExtra(Constant.DATA_1,viewModel.objCard?.cardId)
//            setResult(Activity.RESULT_OK,intent)
//            onBackPressed()
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.lockCard(viewModel.objCard?.cardId)
                        }
                    })
                }
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

        viewModel.errorData.observe(this,{
            when(it){
                Constant.ERROR_EMPTY -> {
                    showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onBackPressed()
                }

                Constant.ERROR_SERVER -> {
                    showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onBackPressed()
                }

                Constant.ERROR_UNKNOW -> {
                    showLongError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    onBackPressed()
                }
            }
        })
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.FINISH_ALL_PVCOMBANK -> {
                onBackPressed()
            }
            else -> {}
        }
    }
}