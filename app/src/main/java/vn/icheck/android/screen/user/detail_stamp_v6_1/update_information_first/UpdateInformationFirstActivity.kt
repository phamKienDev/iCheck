package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_update_information_first.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.Status
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.VerifyOTPGuaranteeActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.select_variant.SelectVariantActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.selectdistrictstamp.SelectDistrictStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.selectprovincestamp.SelectProvinceStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.adapter.FieldAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.presenter.UpdateInformationFirstPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.view.IUpdateInformationFirstView
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.viewmodel.UpdateInformationFirstViewModel
import vn.icheck.android.ui.layout.CustomLinearLayoutManager
import vn.icheck.android.util.ick.logError
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class UpdateInformationFirstActivity : BaseActivityMVVM(), IUpdateInformationFirstView {
    private val viewModel by viewModels<UpdateInformationFirstViewModel>()

    private val presenter = UpdateInformationFirstPresenter(this)

    private var isChangeData: Boolean? = false
    private var requestChangeData = 1
    private var requestSelectProvince = 2
    private var requestSelectDistrict = 3
    private var requestSelectVariant = 4

    private var disposable: Disposable? = null

    private var customerVariantAdapter = FieldAdapter()
    private var guaranteeVariantAdapter = FieldAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_information_first)

        StampDetailActivity.listActivities.add(this)

        setupView()
        setupListener()
        setupSearchCustomer()
        checkData()
    }

    private fun setupView() {
        if (StampDetailActivity.isVietNamLanguage == false) {
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

    private fun setupListener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        layoutSelectCity.setOnClickListener {
//            val intent = Intent(this, SelectProvinceStampActivity::class.java)
//            startActivityForResult(intent, requestSelectProvince)
            startActivityForResult<SelectProvinceStampActivity>(requestSelectProvince)
        }

        layoutSelectDistrict.setOnClickListener {
//            val intent = Intent(this, SelectDistrictStampActivity::class.java)
//            intent.putExtra(Constant.DATA_1, cityId)
//            startActivityForResult(intent, requestSelectDistrict)
            presenter.cityId?.let {
                startActivityForResult<SelectDistrictStampActivity, Int>(Constant.DATA_1, it, requestSelectDistrict)
            }
        }

        edtVariant.setOnClickListener {
            val intent = Intent(this, SelectVariantActivity::class.java)
            intent.putExtra(Constant.DATA_1, viewModel.productID)
            startActivityForResult(intent, requestSelectVariant)
        }

        btnUpdate.setOnClickListener {
            val customerData = getBody(customerVariantAdapter.listData)
            if (customerData == null) {
                showInputDataError()
                return@setOnClickListener
            }

            val guaranteeData = getBody(guaranteeVariantAdapter.listData)
            if (guaranteeData == null) {
                showInputDataError()
                return@setOnClickListener
            }

            presenter.validUpdateInformationGuarantee(name = edtName.text.toString(),
                    phone = edtPhone.text.toString(), email = edtEmail.text.toString(),
                    address = edtAddress.text.toString(), productCode = edtProductCode.text.toString(),
                    variant = viewModel.objVariant?.id, customerData = customerData,
                    guaranteeData = guaranteeData, barcode = viewModel.barcode,
                    updateType = viewModel.updateType, serial = viewModel.serial)
        }
    }

    private fun showInputDataError() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            showShortError("Please fill in the required fields")
        } else {
            showShortError("Bạn cần nhập các trường yêu cầu")
        }
    }

    private fun setupSearchCustomer() {
        disposable = RxTextView.afterTextChangeEvents(edtPhone)
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchGuaranteeCustomerDetail(edtPhone.text.toString())
                }, {
                    logError(it)
                })
    }

    private fun checkData() {
        viewModel.productID = intent.getLongExtra(Constant.DATA_6, 0)
        viewModel.updateType = intent.getIntExtra(Constant.DATA_1, 0)
        viewModel.distributorID = intent.getLongExtra(Constant.DATA_2, 0)
        viewModel.phoneNumber = intent.getStringExtra(Constant.DATA_3)
        viewModel.productCode = intent.getStringExtra(Constant.DATA_4)
        viewModel.serial = intent.getStringExtra(Constant.DATA_5) ?: ""
        viewModel.objVariant = try {
            intent.getSerializableExtra(Constant.DATA_7) as ICVariantProductStampV6_1.ICVariant.ICObjectVariant?
        } catch (e: Exception) {
            null
        }
        viewModel.barcode = intent.getStringExtra(Constant.DATA_8) ?: ""

        if (viewModel.productID != 0L && viewModel.barcode.isNotEmpty()) {
            getProductVariant()
        } else {
            DialogHelper.showNotification(this@UpdateInformationFirstActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false,
                    object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
        }
    }

    private fun getProductVariantError(message: String) {
        DialogHelper.showConfirm(this@UpdateInformationFirstActivity, message, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                getProductVariant()
            }
        })
    }

    private fun getProductVariant() {
        if (NetworkHelper.isNotConnected(this)) {
            getProductVariantError(if (StampDetailActivity.isVietNamLanguage == false) {
                "Checking network. Please try again"
            } else {
                getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            })
            return
        }

        lifecycleScope.launch {
            var productVariant: ICVariantProductStampV6_1? = null
            var customerVariant: MutableList<ICFieldGuarantee>? = null
            var guaranteeVariant: MutableList<ICFieldGuarantee>? = null

            withContext(Dispatchers.IO) {
                val listSync = mutableListOf<Deferred<Any>>()

                listSync.add(async {
                    productVariant = withTimeoutOrNull(APIConstants.REQUEST_TIME) { viewModel.getProductVariant() }
                })
                listSync.add(async {
                    customerVariant = withTimeoutOrNull(APIConstants.REQUEST_TIME) { viewModel.getCustomerVariant() }
                })
                if (viewModel.updateType == 1 || viewModel.updateType == 2) {
                    listSync.add(async {
                        guaranteeVariant = withTimeoutOrNull(APIConstants.REQUEST_TIME) { viewModel.getGuaranteeVariant() }
                    })
                }

                listSync.awaitAll()
            }

            withContext(Dispatchers.Main) {
//                if (productVariant == null || customerVariant == null || guaranteeVariant == null) {
//                    getProductVariantError(if (StampDetailActivity.isVietNamLanguage == false) {
//                        "Occurred. Please try again"
//                    } else {
//                        getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
//                    })
//                    return@withContext
//                }

                if (!productVariant?.data?.products.isNullOrEmpty()) {
                    tvSubProductCode.visibility = View.GONE
                    edtProductCode.visibility = View.GONE
                    tvSubProductVariant.visibility = View.VISIBLE
                    edtVariant.visibility = View.VISIBLE
                } else {
                    tvSubProductCode.visibility = View.VISIBLE
                    edtProductCode.visibility = View.VISIBLE
                    tvSubProductVariant.visibility = View.GONE
                    edtVariant.visibility = View.GONE
                }

                if (!customerVariant.isNullOrEmpty()) {
                    rcvCustomerVariant.apply {
                        beVisible()
                        layoutManager = CustomLinearLayoutManager(this@UpdateInformationFirstActivity, LinearLayoutManager.VERTICAL, false)
                        adapter = customerVariantAdapter
                    }
                    customerVariantAdapter.addData(customerVariant!!)
                } else {
                    rcvCustomerVariant.visibility = View.GONE
                }

                if (!guaranteeVariant.isNullOrEmpty()) {
                    tvTitleField.visibility = View.VISIBLE
                    rcvField.apply {
                        beVisible()
                        rcvField.layoutManager = CustomLinearLayoutManager(this@UpdateInformationFirstActivity, LinearLayoutManager.VERTICAL, false)
                        rcvField.adapter = guaranteeVariantAdapter
                    }
                    guaranteeVariantAdapter.addData(guaranteeVariant!!)
                } else {
                    tvTitleField.visibility = View.GONE
                    rcvField.visibility = View.GONE
                }

                when (viewModel.updateType) {
                    1 -> {
                        //show layout ForceUpdate
                        tvViewForceUpdate.visibility = View.VISIBLE
                        edtProductCode.setText(viewModel.productCode)
                        edtVariant.setText(viewModel.objVariant?.extra)
                        if (!viewModel.phoneNumber.isNullOrEmpty()) {
                            searchGuaranteeCustomerDetail(viewModel.phoneNumber!!)
                        } else {
                            edtPhone.setText(SessionManager.session.user?.phone)
                        }
                    }
                    2 -> {
                        //getData customer ve va show len view
                        tvViewVerifiedPhoneNumber.visibility = View.VISIBLE
                        edtProductCode.setText(viewModel.productCode)
                        edtVariant.setText(viewModel.objVariant?.extra)
                        if (!viewModel.phoneNumber.isNullOrEmpty()) {
                            searchGuaranteeCustomerDetail(viewModel.phoneNumber!!)
                        } else {
                            edtPhone.setText(SessionManager.session.user?.phone)
                        }
                    }
                    else -> {
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
        }
    }

    private fun searchGuaranteeCustomerDetail(phoneNumber: String) {
        viewModel.getGuaranteeCustomerDetail(phoneNumber).observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (it.message.isNullOrEmpty()) {
                        DialogHelper.showLoading(this@UpdateInformationFirstActivity)
                    } else {
                        DialogHelper.closeLoading(this@UpdateInformationFirstActivity)
                    }
                }
                Status.ERROR_NETWORK -> {
                    showLongError(ICheckApplication.getError(it.message))
                    resetCustomerInfo()
                }
                Status.ERROR_REQUEST -> {
                    showLongError(ICheckApplication.getError(it.message))
                    resetCustomerInfo()
                }
                Status.SUCCESS -> {
                    val customer = it.data?.data?.customer

                    if (customer != null) {
                        setCustomerInfo(customer)
                    } else {
                        resetCustomerInfo()
                    }
                }
            }
        })
    }

    private fun setCustomerInfo(customer: ICGuaranteeCustomerDetail.ICGuaranteeCustomer) {
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
            if (StampDetailActivity.isVietNamLanguage == false) {
                edtName.hint = "Enter Name"
            } else {
                edtName.hint = "Nhập họ tên"
            }
        }

        disposable?.dispose()
        disposable = null

        if (!customer.phone.isNullOrEmpty()) {
            edtPhone.setText(customer.phone)

            viewModel.updateType = if (edtPhone.text.toString() != customer.phone) {
                2
            } else {
                1
            }
        } else {
            if (StampDetailActivity.isVietNamLanguage == false) {
                edtPhone.hint = "Enter Phone Number"
            } else {
                edtPhone.hint = "Nhập số điện thoại"
            }
        }

        setupSearchCustomer()

        if (!customer.email.isNullOrEmpty()) {
            edtEmail.setText(customer.email)
        } else {
            if (StampDetailActivity.isVietNamLanguage == false) {
                edtEmail.hint = "Enter Email"
            } else {
                edtEmail.hint = "Nhập email"
            }
        }

        if (!customer.address.isNullOrEmpty()) {
            edtAddress.setText(customer.address)
        } else {
            if (StampDetailActivity.isVietNamLanguage == false) {
                edtAddress.hint = "Enter Address"
            } else {
                edtAddress.hint = "Nhập địa chỉ"
            }
        }

        if (!viewModel.productCode.isNullOrEmpty()) {
            edtProductCode.setText(viewModel.productCode)
        } else {
            if (StampDetailActivity.isVietNamLanguage == false) {
                edtProductCode.hint = "Enter Product Code"
            } else {
                edtProductCode.hint = "Nhập mã hiệu sản phẩm"
            }
        }

        if (!viewModel.objVariant?.extra.isNullOrEmpty()) {
            edtVariant.setText(viewModel.objVariant?.extra)
        } else {
            if (StampDetailActivity.isVietNamLanguage == false) {
                edtVariant.hint = "Enter Variation Code"
            } else {
                edtVariant.hint = "Nhập biến thể sản phẩm"
            }
        }
    }

    private fun resetCustomerInfo() {
        edtName.setText("")
        edtEmail.setText("")
        edtAddress.setText("")
        if (StampDetailActivity.isVietNamLanguage == false) {
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

    private fun getBody(listData: MutableList<ICFieldGuarantee>): HashMap<String, Any>? {
        val body = hashMapOf<String, Any>()

        for (item in listData) {
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

    override fun onGetDetailStampSuccess(obj: ICDetailStampV6_1) {
        finish()
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
    }

    override fun onGetNameCitySuccess(obj: ICNameCity) {
        presenter.cityId = obj.data?.id
        tvCities.text = obj.data?.name
    }

    @SuppressLint("SetTextI18n")
    override fun onGetNameCityFail() {
        if (StampDetailActivity.isVietNamLanguage == false) {
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
        if (StampDetailActivity.isVietNamLanguage == false) {
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

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvDistricts.text = "Option"
        } else {
            tvDistricts.text = "Tùy chọn"
        }
    }

    override fun onSetDistrictName(name: String, id: Int) {
        tvDistricts.text = name
        presenter.districtId = id
    }

    override fun onSendOtpGuaranteeSuccess(name: String, phone: String, email: String, cityId: Int?,
                                           districtId: Int?, address: String, productCode: String,
                                           variant: Long?, customerData: HashMap<String, Any>,
                                           guaranteeData: HashMap<String, Any>) {
        val obj = ICUpdateCustomerGuarantee(name, phone, email, address, districtId, cityId, customerData)

        val intent = Intent(this, VerifyOTPGuaranteeActivity::class.java)
        intent.putExtra(Constant.DATA_1, obj)
        intent.putExtra(Constant.DATA_2, viewModel.serial)
        intent.putExtra(Constant.DATA_3, productCode)
        intent.putExtra(Constant.DATA_4, variant)
        intent.putExtra(Constant.DATA_5, guaranteeData)
        startActivityForResult(this, intent, requestChangeData)
    }

    override fun updateInformationCusomterGuaranteeSuccess() {
        setResult(RESULT_OK)

        for (act in StampDetailActivity.listActivities) {
            act.finish()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            if (StampDetailActivity.isVietNamLanguage == false) {
                showShortSuccess("Successfully updated")
            } else {
                showShortSuccess("Cập nhật thông tin thành công")
            }
        }
    }

    override fun updateInformationCusomterGuaranteeFail() {
        if (StampDetailActivity.isVietNamLanguage == false) {
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
                    setResult(RESULT_OK)
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
                    viewModel.objVariant?.id = data?.getLongExtra(Constant.DATA_2, 0L)
                    edtVariant.setText(variantName)
                }
            }
        }
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this

    override fun onShowLoading(isShow: Boolean) {

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (isChangeData == true) {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
        }
//        if (viewModel.updateType == 1 || viewModel.updateType == 2) {
//            if (isChangeData == true) {
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }
}
