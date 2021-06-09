package vn.icheck.android.screen.user.updateaddress

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_create_user_address.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.screen.user.selectdistrict.SelectDistrictActivity
import vn.icheck.android.screen.user.selectprovince.SelectProvinceActivity
import vn.icheck.android.screen.user.selectward.SelectWardActivity
import vn.icheck.android.screen.user.updateaddress.presenter.UpdateUserAddressPresenter
import vn.icheck.android.screen.user.updateaddress.view.IUpdateUserAddressView
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 12/28/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class UpdateUserAddressActivity : BaseActivityMVVM(), IUpdateUserAddressView, View.OnClickListener {
    private val requestProvince = 1
    private val requestDistrict = 2
    private val requestWard = 3

    val presenter = UpdateUserAddressPresenter(this@UpdateUserAddressActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user_address)
        onInitView()
    }

    fun onInitView() {
        setupToolbar()
        setupListener()
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.them_dia_chi_moi)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupListener() {
        WidgetUtils.setClickListener(this, spProvince, spDistrict, spWard)

        setupInput(edtLastName, layoutInputLastName)
        setupInput(edtFirstName, layoutInputFirstName)
        setupInput(edtPhone, layoutInputPhone)
        setupInput(edtAddress, layoutInputAddress)

        btnCreate.setOnClickListener {
            presenter.createUserAddress(
                    edtLastName.text.toString(),
                    edtFirstName.text.toString(),
                    edtPhone.text.toString(),
                    edtAddress.text.toString())
        }
    }

    private fun setupInput(input: TextInputEditText, layout: TextInputLayout) {
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

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@UpdateUserAddressActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onGetIDError() {
        DialogHelper.showNotification(this@UpdateUserAddressActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetAddressError(error: String) {
        DialogHelper.showConfirm(this@UpdateUserAddressActivity, error, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getAddressDetail()
            }
        })
    }

    override fun onGetAddressSuccess(address: ICAddress) {
        edtLastName.setText(address.last_name)
        edtFirstName.setText(address.first_name)
        edtPhone.setText(address.phone)
        edtAddress.setText(address.address)
    }

    override fun onSetProvince(name: String) {
        spProvince.text = name
    }

    override fun onSetDistrict(name: String) {
        spDistrict.text = name
    }

    override fun onSetWard(name: String) {
        spWard.text = name
    }

    override fun onErrorLastName(error: String) {
        layoutInputLastName.error = error
    }

    override fun onErrorFirstName(error: String) {
        layoutInputFirstName.error = error
    }

    override fun onErrorPhone(error: String) {
        layoutInputPhone.error = error
    }

    override fun onErrorProvince(error: String) {
        layoutInputProvince.error = error
    }

    override fun onErrorDistrict(error: String) {
        layoutInputDistrict.error = error
    }

    override fun onErrorWard(error: String) {
        layoutInputWard.error = error
    }

    override fun onErrorAddress(error: String) {
        layoutInputAddress.error = error
    }

    override fun onCreateAddressSuccess(json: String) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, json)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {

        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@UpdateUserAddressActivity

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.spProvince -> {
                startActivityForResult<SelectProvinceActivity>(requestProvince)
            }
            R.id.spDistrict -> {
                val obj = presenter.getProvince

                if (obj != null) {
                    startActivityForResult<SelectDistrictActivity, Int>(Constant.DATA_1, obj.id.toInt(), requestDistrict)
                }
            }
            R.id.spWard -> {
                val obj = presenter.getDistrict

                if (obj != null) {
                    startActivityForResult<SelectWardActivity, Int>(Constant.DATA_1, obj.id, requestWard)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestProvince -> {
                    presenter.selectProvince(data)
                }
                requestDistrict -> {
                    presenter.selectDistict(data)
                }
                requestWard -> {
                    presenter.selectWard(data)
                }
            }
        }
    }
}