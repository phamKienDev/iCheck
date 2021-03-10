package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.accept_ship_gift

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_accept_ship_gift_loyalty.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.select_address.district.SelectDistrictActivity
import vn.icheck.android.loyalty.screen.select_address.province.SelectProvinceActivity
import vn.icheck.android.loyalty.screen.select_address.ward.SelectWardActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

class AcceptShipGiftLoyaltyActivity : BaseActivityGame(), View.OnClickListener {

    private val viewModel by viewModels<AcceptShipGiftLoyaltyViewModel>()

    private val requestProvince = 1
    private val requestDistrict = 2
    private val requestWard = 3

    override val getLayoutID: Int
        get() = R.layout.activity_accept_ship_gift_loyalty

    override fun onInitView() {
        initToolbar()
        initListener()
        initError()
        initSuccess()
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.text = "Xác nhận đổi quà"
    }

    private fun initListener() {
        WidgetHelper.setClickListener(this, spProvince, spDistrict, spWard)
        val obj = intent.getSerializableExtra(ConstantsLoyalty.DATA_2) as ICKBoxGifts
        viewModel.collectionID = intent.getLongExtra(ConstantsLoyalty.DATA_3, -1)

        val user = SessionManager.session.user
        edtName.setText(user?.name)
        edtPhone.setText(user?.phone)
        edtEmail.setText(user?.email)
        edtAddress.setText(user?.address)
        spProvince.text = if (!user?.city?.name.isNullOrEmpty()) {
            user?.city?.name
        } else {
            "Tùy chọn"
        }
        spDistrict.text = if (!user?.district?.name.isNullOrEmpty()) {
            user?.district?.name
        } else {
            "Tùy chọn"
        }
        spWard.text = if (!user?.ward?.name.isNullOrEmpty()) {
            user?.ward?.name
        } else {
            "Tùy chọn"
        }
        viewModel.province = user?.city
        viewModel.district = user?.district
        viewModel.ward = user?.ward

        setupInput(edtName, layoutInputName)
        setupInput(edtPhone, layoutInputPhone)
        setupInput(edtEmail, layoutInputEmail)
        setupInput(edtAddress, layoutInputAddress)

        btnDone.setOnClickListener {
            btnDone.isEnabled = false
            viewModel.postExchangeGift(obj.gift_id!!, edtName.text.toString().trim(), edtPhone.text.toString().trim(), edtEmail.text.toString().trim(), edtAddress.text.toString().trim())

            Handler().postDelayed({
                btnDone.isEnabled = true
            }, 2000)
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

    private fun initError() {
        viewModel.onError.observe(this, Observer {
            showLongError(it.title)
        })

        viewModel.onErrorName.observe(this, Observer {
            layoutInputName.error = it
        })

        viewModel.onErrorPhone.observe(this, Observer {
            layoutInputPhone.error = it
        })

        viewModel.onErrorEmail.observe(this, Observer {
            layoutInputEmail.error = it
        })

        viewModel.onErrorAddress.observe(this, Observer {
            layoutInputAddress.error = it
        })

        viewModel.onErrorProvince.observe(this, Observer {
            layoutInputProvince.error = it
        })

        viewModel.onErrorDistrict.observe(this, Observer {
            layoutInputDistrict.error = it
        })

        viewModel.onErrorWard.observe(this, Observer {
            layoutInputWard.error = it
        })
    }

    private fun initSuccess() {
        viewModel.onSetProvince.observe(this, Observer {
            spProvince.text = it
        })

        viewModel.onSetDistrict.observe(this, Observer {
            spDistrict.text = it
        })

        viewModel.onSetWard.observe(this, Observer {
            spWard.text = it
        })

        viewModel.onSuccess.observe(this, Observer {
            showDialog(it.image?.medium)
        })
    }

    private fun showDialog(image: String?) {
        DialogHelperGame.dialogAcceptShipGiftSuccess(this@AcceptShipGiftLoyaltyActivity, image
                ?: "", intent.getLongExtra(ConstantsLoyalty.DATA_3, -1), R.drawable.bg_gradient_button_orange_yellow,
                object : IDismissDialog {
                    override fun onDismiss() {
                        onBackPressed()
                    }
                },
                object : IClickButtonDialog<Long> {
                    override fun onClickButtonData(obj: Long?) {
                        LoyaltySdk.startActivityRedemptionHistory(this@AcceptShipGiftLoyaltyActivity, obj.toString(), 0)
                        this@AcceptShipGiftLoyaltyActivity.finish()
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