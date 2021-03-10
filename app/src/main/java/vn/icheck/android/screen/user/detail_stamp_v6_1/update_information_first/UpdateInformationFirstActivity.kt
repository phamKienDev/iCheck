package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_update_information_first.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.VerifyOTPGuaranteeActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.select_variant.SelectVariantActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.selectdistrictstamp.SelectDistrictStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.selectprovincestamp.SelectProvinceStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter.FieldAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.presenter.UpdateInformationFirstPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.view.IUpdateInformationFirstView
import vn.icheck.android.screen.user.selectprovince.SelectProvinceActivity
import vn.icheck.android.util.ick.logError
import java.util.concurrent.TimeUnit

class UpdateInformationFirstActivity : BaseActivity<UpdateInformationFirstPresenter>(), IUpdateInformationFirstView {

    override val getLayoutID: Int
        get() = R.layout.activity_update_information_first

    override val getPresenter: UpdateInformationFirstPresenter
        get() = UpdateInformationFirstPresenter(this)

    private var mProductCode: String? = null
    private var mVariantName: String? = null
    private var mId: Long? = null
    private var mProductId: Long? = null
    private var mIdVariantSelected: Long? = null
    private var cityId: Int? = null
    private var districtId: Int? = null

    private var typeUpdateCustomer: Int? = null

    private var isChangeData: Boolean? = false
    private var requestChangeData = 1
    private var requestSelectProvince = 2
    private var requestSelectDistrict = 3
    private var requestSelectVariant = 4

    private var disposable: Disposable? = null

    private var adapter = FieldAdapter()

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        DetailStampActivity.listActivities.add(this)

        runOnUiThread {
            if (DetailStampActivity.isVietNamLanguage == false) {
                txtTitle.text = "Customer Information"
                tvLabelInforCustomer.text = "Customer Information"
                tvViewVerifiedPhoneNumber.text = "Update customer information to receive the hot deals and service."
                tvViewForceUpdate.text = "You have to update personal information to active warranty.Manufacturer may refuse warranty if the information is incorrect"
                tvSubPhone.text = "Phone Number (*)"
                tvSubName.text = "Name"
                tvSubTinhThanh.text = "City"
                tvSubHuyen.text = "District"
                tvCities.text = "Option"
                tvDistricts.text = "Option"
                tvSubAddress.text = "Address"
                btnUpdate.text = "Update"
                edtPhone.hint = "Enter Phone Number"
                edtName.hint = "Enter Name"
                edtEmail.hint = "Enter Email"
                edtAddress.hint = "Enter Address"
                tvSubProductCode.text = "Product Code"
                edtProductCode.hint = "Enter Product Code"
                tvSubProductVariant.text = "Varitation code"
                edtVariant.hint = "Enter Variation Code"
                tvTitleField.text = "Received information"
            } else {
                txtTitle.text = "Thông tin khách hàng"
                tvLabelInforCustomer.text = "Thông tin khách hàng"
                tvViewVerifiedPhoneNumber.text = "Cập nhật thông tin khách hàng để nhận các ưu đãi\nvà chăm sóc tốt nhất."
                tvViewForceUpdate.text = "Bạn phải nhập thông tin cá nhân để kích hoạt bảo hành.\nNhà sản xuất có quyền từ chối bảo hành nếu thông tin\nkhông chính xác."
                tvSubPhone.text = "Số điện thoại (*)"
                tvSubName.text = "Họ và tên"
                tvSubTinhThanh.text = "Tỉnh thành"
                tvSubHuyen.text = "Huyện"
                tvCities.text = "Tùy chọn"
                tvDistricts.text = "Tùy chọn"
                tvSubAddress.text = "Địa chỉ"
                btnUpdate.text = "Cập nhật"
                edtPhone.hint = "Nhập số điện thoại"
                edtName.hint = "Nhập họ tên"
                edtEmail.hint = "Nhập email"
                edtAddress.hint = "Nhập địa chỉ"
                tvSubProductCode.text = "Mã hiệu sản phẩm"
                edtProductCode.hint = "Nhập mã hiệu sản phẩm"
                tvSubProductVariant.text = "Mã biến thể"
                edtVariant.hint = "Nhập biến thể sản phẩm"
                tvTitleField.text = "Thông tin tiếp nhận"
            }
        }

        listener()
        presenter.getDataByIntent(intent)
    }

    private fun listener() {
        imgBack.setOnClickListener {
            if (isChangeData == true) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            }
            onBackPressed()

        }

        btnUpdate.setOnClickListener {
            val body = getBody()

            if (body == null) {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    showShortError("Please fill in the required fields")
                } else {
                    showShortError("Bạn cần nhập các trường yêu cầu")
                }
                return@setOnClickListener
            }

            presenter.validUpdateInformationGuarantee(edtName.text.toString(), edtPhone.text.toString(), edtEmail.text.toString(), edtAddress.text.toString(), edtProductCode.text.toString(), mIdVariantSelected, typeUpdateCustomer, body)
        }

        layoutSelectCity.setOnClickListener {
            val intent = Intent(this, SelectProvinceStampActivity::class.java)
            startActivityForResult(intent, requestSelectProvince)
        }

        layoutSelectDistrict.setOnClickListener {
            val intent = Intent(this, SelectDistrictStampActivity::class.java)
            intent.putExtra(Constant.DATA_1, cityId)
            startActivityForResult(intent, requestSelectDistrict)
        }

        edtVariant.setOnClickListener {
            val intent = Intent(this, SelectVariantActivity::class.java)
            intent.putExtra(Constant.DATA_1, mProductId)
            startActivityForResult(intent, requestSelectVariant)
        }

        searchCustomer()
    }

    private fun getBody(): HashMap<String, Any>? {
        val body = hashMapOf<String, Any>()

        for (item in adapter.listData) {
            if (item.type == "input") {
                if (item.require == 1) {
                    if (item.inputContent.isNullOrEmpty()) {
                        return null
                    }
                }
                if (!item.inputContent.isNullOrEmpty()) {
                    body[item.key] = item.inputContent!!
                }
            }

            if (item.type == "textarea") {
                if (item.require == 1) {
                    if (item.inputArea.isNullOrEmpty()) {
                        return null
                    }
                }
                if (!item.inputArea.isNullOrEmpty()) {
                    body[item.key] = item.inputArea!!
                }
            }

            if (item.type == "date") {
                if (item.require == 1) {
                    if (item.date.isNullOrEmpty()) {
                        return null
                    }
                }
                if (!item.date.isNullOrEmpty()) {
                    body[item.key] = item.date!!
                }
            }

            if (item.type == "select") {
                if (item.require == 1) {
                    if (!item.valueF.isNullOrEmpty()) {
                        var value: String? = null
                        for (select in item.valueF!!) {
                            if (select.isChecked) {
                                value = select.value
                            }
                        }
                        if (value == null) {
                            return null
                        }
                        body[item.key] = value
                    }
                }
            }

            if (item.type == "checkbox") {
                if (item.require == 1) {
                    if (!item.valueF.isNullOrEmpty()) {
                        val list = mutableListOf<String>()
                        for (checkbox in item.valueF!!) {
                            if (checkbox.isChecked) {
                                list.add(checkbox.value!!)
                            }
                        }
                        if (list.isNotEmpty()) {
                            body[item.key] = list
                        }
                    }
                }
            }
        }

        return body
    }

    private fun searchCustomer() {
        disposable = RxTextView.afterTextChangeEvents(edtPhone)
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (mId != null) {
                        presenter.searchInforCustomer(mId!!, edtPhone.text.toString())
                    }
                }, {
                    logError(it)
                })
    }

    @SuppressLint("SetTextI18n")
    override fun onSearchCustomerFail() {
        runOnUiThread {
            edtName.setText("")
            edtEmail.setText("")
            edtAddress.setText("")
            if (DetailStampActivity.isVietNamLanguage == false) {
                edtPhone.hint = "Enter Phone Number"
                edtName.hint = "Enter Name"
                edtEmail.hint = "Enter Email"
                tvCities.text = "Option"
                tvDistricts.text = "Option"
                edtAddress.hint = "Enter Address"
            } else {
                edtPhone.hint = "Nhập số điện thoại"
                edtName.hint = "Nhập họ tên"
                edtEmail.hint = "Nhập email"
                tvCities.text = "Tùy chọn"
                tvDistricts.text = "Tùy chọn"
                edtAddress.hint = "Nhập địa chỉ"
            }
        }
    }

    override fun onGetDataVariantSuccess(products: MutableList<ICVariantProductStampV6_1.ICVariant.ICObjectVariant>, productId: Long) {
        mProductId = productId
        edtVariant.visibility = View.VISIBLE
        tvSubProductVariant.visibility = View.VISIBLE
        tvSubProductCode.visibility = View.GONE
        edtProductCode.visibility = View.GONE
        presenter.getDataByIntentSecond(intent)
    }

    override fun onGetDataVariantFail() {
        tvSubProductCode.visibility = View.VISIBLE
        edtProductCode.visibility = View.VISIBLE
        edtVariant.visibility = View.GONE
        tvSubProductVariant.visibility = View.GONE
        presenter.getDataByIntentSecond(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetDataIntentSuccess(type: Int, id: Long, phoneNumber: String?, productCode: String?, objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant?) {
        typeUpdateCustomer = type
        mVariantName = objVariant?.extra
        mIdVariantSelected = objVariant?.id
        mProductCode = productCode

        when (type) {
            1 -> {
                if (id != null) {
                    mId = id
                }
                //show layout ForceUpdate
                tvViewForceUpdate.visibility = View.VISIBLE
                edtProductCode.setText(productCode)
                edtVariant.setText(mVariantName)
                phoneNumber?.let {
                    presenter.getInforCustomer(id, it)
                }

            }
            2 -> {
                if (id != null) {
                    mId = id
                }
                //getData customer ve va show len view
                tvViewVerifiedPhoneNumber.visibility = View.VISIBLE
                edtProductCode.setText(productCode)
                edtVariant.setText(mVariantName)
                phoneNumber?.let {
                    presenter.getInforCustomer(id, it)
                }
            }
            3 -> {
                imgBack.visibility = View.GONE
                tvSubProductCode.visibility = View.GONE
                edtProductCode.visibility = View.GONE
                tvSubProductVariant.visibility = View.GONE
                edtVariant.visibility = View.GONE
                edtPhone.setText(SessionManager.session.user?.phone)
                edtName.setText(SessionManager.session.user?.first_name + SessionManager.session.user?.last_name)
                edtEmail.setText(SessionManager.session.user?.email)
                tvCities.text = SessionManager.session.user?.city?.name
                tvDistricts.text = SessionManager.session.user?.district?.name
                presenter.cityId = SessionManager.session.user?.city?.id?.toInt()
                presenter.districtId = SessionManager.session.user?.district?.id
            }
        }
    }

    override fun onGetFieldListGuareanteeSuccess(data: MutableList<ICFieldGuarantee>) {
        if (!data.isNullOrEmpty()) {
            tvTitleField.visibility = View.VISIBLE
            rcvField.visibility = View.VISIBLE
            initRecyclerView()
            adapter.addData(data)
        } else {
            tvTitleField.visibility = View.GONE
            rcvField.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        rcvField.layoutManager = LinearLayoutManager(this)
        rcvField.adapter = adapter
    }

    override fun onGetFieldListGuareanteeFail() {
        tvTitleField.visibility = View.GONE
        rcvField.visibility = View.GONE
    }

    override fun onGetDetailStampSuccess(obj: ICDetailStampV6_1) {
        finish()
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
    }

    override fun onGetDataError(errorType: Int) {
        when (errorType) {
            Constant.ERROR_INTERNET -> {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    DialogHelper.showConfirm(this@UpdateInformationFirstActivity, "Checking network. Please try again", false, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            presenter.getDataByIntent(intent)
                        }
                    })
                } else {
                    DialogHelper.showConfirm(this@UpdateInformationFirstActivity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            presenter.getDataByIntent(intent)
                        }
                    })
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onGetDataDetailCustomeFail() {
        typeUpdateCustomer = 2

        runOnUiThread {
            edtName.setText("")
            edtEmail.setText("")
            edtAddress.setText("")
            if (DetailStampActivity.isVietNamLanguage == false) {
                edtName.hint = "Enter Name"
                edtEmail.hint = "Enter Email"
                tvCities.text = "Option"
                tvDistricts.text = "Option"
                edtAddress.hint = "Enter Address"
            } else {
                edtName.hint = "Nhập họ tên"
                edtEmail.hint = "Nhập email"
                tvCities.text = "Tùy chọn"
                tvDistricts.text = "Tùy chọn"
                edtAddress.hint = "Nhập địa chỉ"
            }
        }
    }

    override fun onGetDataDetailCustomeSuccess(customer: ICDetailCustomerGuranteeVerified.ICDetailCustomerGurantee.ICObjectCustomerGurantee) {
        runOnUiThread {
            customer.city?.let {
                if (it > 0) {
                    presenter.cityId = it
                    presenter.onGetNameCity(customer.city)
                }
            }

            customer.district?.let {
                if (it > 0) {
                    presenter.districtId = it
                    presenter.onGetNameDistricts(customer.district)
                }
            }

            if (!customer.name.isNullOrEmpty()) {
                edtName.setText(customer.name)
            } else {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    edtName.hint = "Enter Name"
                } else {
                    edtName.hint = "Nhập họ tên"
                }
            }

            disposable?.dispose()
            disposable = null

            if (!customer.phone.isNullOrEmpty()) {
                edtPhone.setText(customer.phone)

                typeUpdateCustomer = if (edtPhone.text.toString() != customer.phone) {
                    2
                } else {
                    1
                }

            } else {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    edtPhone.hint = "Enter Phone Number"
                } else {
                    edtPhone.hint = "Nhập số điện thoại"
                }
            }
            searchCustomer()

            if (!customer.email.isNullOrEmpty()) {
                edtEmail.setText(customer.email)
            } else {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    edtEmail.hint = "Enter Email"
                } else {
                    edtEmail.hint = "Nhập email"
                }
            }

            if (!customer.address.isNullOrEmpty()) {
                edtAddress.setText(customer.address)
            } else {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    edtAddress.hint = "Enter Address"
                } else {
                    edtAddress.hint = "Nhập địa chỉ"
                }
            }

            if (!mProductCode.isNullOrEmpty()) {
                edtProductCode.setText(mProductCode)
            } else {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    edtProductCode.hint = "Enter Product Code"
                } else {
                    edtProductCode.hint = "Nhập mã hiệu sản phẩm"
                }
            }

            if (!mVariantName.isNullOrEmpty()) {
                edtVariant.setText(mVariantName)
            } else {
                if (DetailStampActivity.isVietNamLanguage == false) {
                    edtVariant.hint = "Enter Variation Code"
                } else {
                    edtVariant.hint = "Nhập biến thể sản phẩm"
                }
            }
        }
    }

    override fun onGetNameCitySuccess(obj: ICNameCity) {
        tvCities.text = obj.data?.name
    }

    @SuppressLint("SetTextI18n")
    override fun onGetNameCityFail() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            tvCities.text = "Option"
        } else {
            tvCities.text = "Tùy chọn"
        }
    }

    override fun onGetNameDistrictSuccess(obj: ICNameDistricts) {
        tvDistricts.text = obj.data?.name
    }

    @SuppressLint("SetTextI18n")
    override fun onGetNameDistrictFail() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            tvDistricts.text = "Option"
        } else {
            tvDistricts.text = "Tùy chọn"
        }
    }

    override fun onErrorPhone(message: String) {
        showShortError(message)
    }

    override fun onErrorName(message: String) {
        showShortError(message)
    }

    override fun onErrorEmail(message: String) {
        showShortError(message)
    }

    override fun onErrorAddress(message: String) {
        showShortError(message)
    }

    override fun onErrorProductCode(message: String) {
        showShortError(message)
    }

    @SuppressLint("SetTextI18n")
    override fun onSetCityName(name: String, id: Int) {
        tvCities.text = name
        presenter.cityId = id
        cityId = id

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvDistricts.text = "Option"
        } else {
            tvDistricts.text = "Tùy chọn"
        }
    }

    override fun onSetDistrictName(name: String, id: Int) {
        tvDistricts.text = name
        presenter.districtId = id
        districtId = id
    }

    override fun onSendOtpGuaranteeSuccess(name: String, phone: String, email: String, cityId: Int?, districtId: Int?, address: String, productCode: String, mSerial: String?, variant: Long?, body: HashMap<String, Any>) {
        val obj = ICUpdateCustomerGuarantee()
        if (!name.isEmpty()) {
            obj.name = name
        }
        obj.phone = phone
        if (!email.isEmpty()) {
            obj.email = email
        }
        if (!address.isEmpty()) {
            obj.address = address
        }
        if (cityId != null) {
            obj.city = cityId
        }
        if (districtId != null) {
            obj.district = districtId
        }

        val intent = Intent(this, VerifyOTPGuaranteeActivity::class.java)
        intent.putExtra(Constant.DATA_1, obj)
        intent.putExtra(Constant.DATA_2, mSerial)
        intent.putExtra(Constant.DATA_3, productCode)
        intent.putExtra(Constant.DATA_4, variant)
        intent.putExtra(Constant.DATA_5, body)
        startActivityForResult(intent, requestChangeData)
    }

    override fun updateInformationCusomterGuaranteeSuccess() {
        for (act in DetailStampActivity.listActivities) {
            act.finish()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            if (DetailStampActivity.isVietNamLanguage == false) {
                showShortSuccess("Successfully updated")
            } else {
                showShortSuccess("Cập nhật thông tin thành công")
            }
        }
    }

    override fun updateInformationCusomterGuaranteeFail() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            showShortError("Occurred. Please try again")
        } else {
            showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestChangeData -> {
                    isChangeData = true
                }

                requestSelectProvince -> {
                    presenter.selectCity(data)
                }

                requestSelectDistrict -> {
                    presenter.selectDistrict(data)
                }

                requestSelectVariant -> {
                    val variantName = data?.getStringExtra(Constant.DATA_1)
                    mIdVariantSelected = data?.getLongExtra(Constant.DATA_2, 0L)
                    edtVariant.setText(variantName)
                }
            }
        }
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        showShortError(errorMessage)
    }

    override fun onBackPressed() {
        if (typeUpdateCustomer == 1 || typeUpdateCustomer == 2) {
            super.onBackPressed()
            if (isChangeData == true) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }
}
