package vn.icheck.android.screen.user.createuseraddress

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_create_user_address.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.createuseraddress.presenter.CreateUserAddressPresenter
import vn.icheck.android.screen.user.createuseraddress.view.ICreateUserAddressView
import vn.icheck.android.screen.user.selectdistrict.SelectDistrictActivity
import vn.icheck.android.screen.user.selectprovince.SelectProvinceActivity
import vn.icheck.android.screen.user.selectward.SelectWardActivity
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 12/28/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateUserAddressActivity : BaseActivity<CreateUserAddressPresenter>(), ICreateUserAddressView, View.OnClickListener {

    private val requestProvince = 1
    private val requestDistrict = 2
    private val requestWard = 3

    override val getLayoutID: Int
        get() = R.layout.activity_create_user_address

    override val getPresenter: CreateUserAddressPresenter
        get() = CreateUserAddressPresenter(this)

    override fun onInitView() {
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
                    edtLastName.text.toString().trim(),
                    edtFirstName.text.toString().trim(),
                    edtPhone.text.toString().trim(),
                    edtAddress.text.toString().trim())
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

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
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
//        edtLastName.requestFocus()
//        edtLastName.setSelection(edtLastName.length())
    }

    override fun onErrorFirstName(error: String) {
        layoutInputFirstName.error = error
//        edtFirstName.requestFocus()
//        edtFirstName.setSelection(edtFirstName.length())
    }

    override fun onErrorPhone(error: String) {
        layoutInputPhone.error = error
//        edtPhone.requestFocus()
//        edtPhone.setSelection(edtPhone.length())
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
//        edtAddress.requestFocus()
//        edtAddress.setSelection(edtAddress.length())
    }

    override fun onCreateAddressSuccess(json: String) {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, json)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

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