package vn.icheck.android.loyalty.screen.loyalty_customers.accept_ship_gift

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_accept_ship_gift_loyalty.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.dialog.ConfirmLoyaltyDialog
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKRedemptionHistory
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.gift_detail_from_app.GiftDetailFromAppActivity
import vn.icheck.android.loyalty.screen.select_address.district.SelectDistrictActivity
import vn.icheck.android.loyalty.screen.select_address.province.SelectProvinceActivity
import vn.icheck.android.loyalty.screen.select_address.ward.SelectWardActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class AcceptShipGiftActivity : BaseActivityGame(), View.OnClickListener {

    private val viewModel by viewModels<AcceptShipGiftViewModel>()

    private val requestProvince = 1
    private val requestDistrict = 2
    private val requestWard = 3

    override val getLayoutID: Int
        get() = R.layout.activity_accept_ship_gift_loyalty

    override fun onInitView() {
        initToolbar()
        initDataTheFirst()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.setText(R.string.xac_nhan_nhan_qua)
    }

    private fun initDataTheFirst() {
        WidgetHelper.setClickListener(this, spProvince, spDistrict, spWard)
        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_2, -1)
        viewModel.type = intent.getIntExtra(ConstantsLoyalty.TYPE, 0)
        val voucher = intent.getStringExtra(ConstantsLoyalty.VOUCHER)

        if (viewModel.type != 0) {
            when (viewModel.type) {
                1 -> {
                    btnDone.setBackgroundResource(R.drawable.bg_gradient_button_blue)
                }
                4 -> {
                    txtTitle.setText(R.string.xac_nhan_thong_tin)
                    btnDone.setGone()
                    layoutEdtVoucher.setVisible()
                    layoutButtonVoucher.setVisible()
                    tvTitleName.setText(R.string.ho_va_ten)
                    tvTitleCity.setText(R.string.tinh_thanh)
                    tvTitleDistrict.setText(R.string.huyen)
                    tvTitleWard.setText(R.string.phuong_xa)
                    tvTitleAddress.setText(R.string.dia_chi_nhan_qua)
                }
                else -> {
                    btnDone.setBackgroundResource(R.drawable.bg_blue_border_20)
                }
            }
        } else {
            object : DialogNotification(this@AcceptShipGiftActivity, null, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, false) {
                override fun onDone() {
                    this@AcceptShipGiftActivity.onBackPressed()
                }
            }.show()
        }

        val user = SessionManager.session.user

        if (viewModel.type != 4) {
            if (user?.name?.contains("null") == true) {
                edtName.setText("")
            } else {
                edtName.setText(user?.name)
            }

            edtPhone.setText(user?.phone)
            edtEmail.setText(user?.email)
            edtAddress.setText(user?.address)

            spProvince.text = if (!user?.city?.name.isNullOrEmpty()) {
                user?.city?.name
            } else {
                getString(R.string.tuy_chon)
            }
            spDistrict.text = if (!user?.district?.name.isNullOrEmpty()) {
                user?.district?.name
            } else {
                getString(R.string.tuy_chon)
            }
            spWard.text = if (!user?.ward?.name.isNullOrEmpty()) {
                user?.ward?.name
            } else {
                getString(R.string.tuy_chon)
            }
            viewModel.province = user?.city
            viewModel.district = user?.district
            viewModel.ward = user?.ward
        }

        setupInput(edtName, layoutInputName)
        setupInput(edtPhone, layoutInputPhone)
        setupInput(edtEmail, layoutInputEmail)
        setupInput(edtAddress, layoutInputAddress)

        btnDone.setOnClickListener {
            btnDone.isEnabled = false

            viewModel.postExchangeGift(edtName.text.toString().trim(), edtPhone.text.toString().trim(), edtEmail.text.toString().trim(), edtAddress.text.toString().trim())

            Handler().postDelayed({
                btnDone.isEnabled = true
            }, 2000)
        }

        btnBoQua.setOnClickListener {
            onBackPressed()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_BACK_PRESSED))
        }

        btnXacNhan.setOnClickListener {
            object : ConfirmLoyaltyDialog(this@AcceptShipGiftActivity, "", getString(R.string.ban_chac_chan_muon_danh_dau_su_dung_voucher_ma_s, voucher), getString(R.string.de_sau), getString(R.string.chac_chan), false) {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    this@AcceptShipGiftActivity.apply {
                        viewModel.usedVoucher(
                                voucher ?: "",
                                edtNote.text.toString().trim(),
                                edtName.text.toString().trim(),
                                edtPhone.text.toString().trim(),
                                edtEmail.text.toString().trim(),
                                edtAddress.text.toString().trim())
                    }
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    private fun setupInput(input: AppCompatEditText, layout: TextInputLayout) {
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                layout.error = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun initListener() {
        viewModel.onError.observe(this, {
            if (it.title.contains(getString(R.string.phan_thuong_khong_hop_le)) && viewModel.type == 3) {
                showLongError(getString(R.string.khong_tim_thay_thong_tin_phat_hanh_voucher))
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onErrorName.observe(this, {
            layoutInputName.error = it
        })

        viewModel.onErrorPhone.observe(this, {
            layoutInputPhone.error = it
        })

        viewModel.onErrorEmail.observe(this, {
            layoutInputEmail.error = it
        })

        viewModel.onErrorAddress.observe(this, {
            layoutInputAddress.error = it
        })

        viewModel.onErrorProvince.observe(this, {
            layoutInputProvince.error = it
        })

        viewModel.onErrorDistrict.observe(this, {
            layoutInputDistrict.error = it
        })

        viewModel.onErrorWard.observe(this, {
            layoutInputWard.error = it
        })

        viewModel.onSetProvince.observe(this, {
            spProvince.text = it
        })

        viewModel.onSetDistrict.observe(this, {
            spDistrict.text = it
        })

        viewModel.onSetWard.observe(this, {
            spWard.text = it
        })

        viewModel.onSuccessRedemption.observe(this, {
            showDialog(it)
        })

        viewModel.onSuccessVoucher.observe(this, {
            object : DialogNotification(this@AcceptShipGiftActivity, getString(R.string.thong_bao), getString(R.string.doi_qua_thanh_cong), null, false) {
                override fun onDone() {
                    if (intent.getStringExtra(ConstantsLoyalty.BACK_TO_DETAIL)?.contains("BACK_TO_DETAIL") == true) {
                        this@AcceptShipGiftActivity.onBackPressed()
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK))
                    } else {
                        startActivityAndFinish<GiftDetailFromAppActivity, Long>(ConstantsLoyalty.DATA_1, it)
                    }
                }
            }.show()
        })

        viewModel.onSuccessReceiveGift.observe(this@AcceptShipGiftActivity, {
            object : DialogNotification(this@AcceptShipGiftActivity, getString(R.string.thong_bao), getString(R.string.doi_qua_thanh_cong), null, false) {
                override fun onDone() {
                    this@AcceptShipGiftActivity.onBackPressed()
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK))
                }
            }.show()
        })

        viewModel.showError.observe(this, {
            if (it.contains(getString(R.string.phan_thuong_khong_hop_le)) && viewModel.type == 3) {
                showLongError(getString(R.string.khong_tim_thay_thong_tin_phat_hanh_voucher))
            } else {
                showLongError(it)
            }
        })

        viewModel.onSuccessUsedVoucher.observe(this, {
            showShortSuccess(getString(R.string.danh_dau_su_dung_voucher_thanh_cong))
            onBackPressed()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK))
        })
    }

    private fun showDialog(obj: ICKRedemptionHistory?) {
        DialogHelperGame.dialogAcceptShipGiftSuccess(this@AcceptShipGiftActivity, obj?.gift?.image?.medium
                ?: "", intent.getLongExtra(ConstantsLoyalty.DATA_3, -1), R.drawable.bg_gradient_button_blue,
                object : IDismissDialog {
                    override fun onDismiss() {
                        onBackPressed()
                    }
                },
                object : IClickButtonDialog<Long> {
                    override fun onClickButtonData(data: Long?) {
                        if (obj?.owner?.id != null) {
                            LoyaltySdk.startActivityRedemptionHistory(this@AcceptShipGiftActivity, obj.owner?.id.toString(), 1)
                            this@AcceptShipGiftActivity.finish()
                        }
                    }
                })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.spProvince -> {
                startActivityForResult<SelectProvinceActivity>(requestProvince)
            }
            R.id.spDistrict -> {
                val obj = viewModel.getProvince

                if (obj != null) {
                    startActivityForResult<SelectDistrictActivity, Int>(ConstantsLoyalty.DATA_1, obj.id, requestDistrict)
                }
            }
            R.id.spWard -> {
                val obj = viewModel.getDistrict

                if (obj != null) {
                    startActivityForResult<SelectWardActivity, Int>(ConstantsLoyalty.DATA_1, obj.id, requestWard)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestProvince -> {
                    viewModel.selectProvince(data)
                }
                requestDistrict -> {
                    viewModel.selectDistict(data)
                }
                requestWard -> {
                    viewModel.selectWard(data)
                }
            }
        }
    }
}