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
import vn.icheck.android.databinding.ActivityUpdateInformationFirstBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.ichecklibs.ViewHelper
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
import vn.icheck.android.util.ick.rHintText
import vn.icheck.android.util.ick.rText
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class UpdateInformationFirstActivity : BaseActivityMVVM(), IUpdateInformationFirstView {
    private lateinit var binding: ActivityUpdateInformationFirstBinding
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
        binding = ActivityUpdateInformationFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StampDetailActivity.listActivities.add(this)

        setupToolbar()
        setupView()
        setupListener()
        setupSearchCustomer()
        checkData()
    }

    private fun setupToolbar() {
        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.layoutToolbar.txtTitle.setText(R.string.thong_tin_khach_hang)
    }

    private fun setupView() {
        ViewHelper.bgTransparentStrokeLineColor1Corners4(this).apply {
            binding.layoutSelectCity.background=this
            binding.layoutSelectDistrict.background=this
            binding.edtPhone.background=this
            binding.edtName.background=this
            binding.edtEmail.background=this
            binding.edtAddress.background=this
            binding.edtProductCode.background=this
            binding.edtVariant.background=this
        }

        vn.icheck.android.ichecklibs.Constant.getSecondTextColor(this).apply {
            binding.edtPhone.setHintTextColor(this)
            binding.edtName.setHintTextColor(this)
            binding.edtEmail.setHintTextColor(this)
            binding.tvCities.setHintTextColor(this)
            binding.tvDistricts.setHintTextColor(this)
            binding.edtAddress.setHintTextColor(this)
            binding.edtProductCode.setHintTextColor(this)
            binding.edtVariant.setHintTextColor(this)
        }

        binding.btnUpdate.background=ViewHelper.btnPrimaryCorners4(this)
        binding.tvViewVerifiedPhoneNumber.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondTextColor(this))
    }



    private fun setupListener() {
        binding.layoutSelectCity.setOnClickListener {
//            val intent = Intent(this, SelectProvinceStampActivity::class.java)
//            startActivityForResult(intent, requestSelectProvince)
            startActivityForResult<SelectProvinceStampActivity>(requestSelectProvince)
        }

        binding.layoutSelectDistrict.setOnClickListener {
//            val intent = Intent(this, SelectDistrictStampActivity::class.java)
//            intent.putExtra(Constant.DATA_1, cityId)
//            startActivityForResult(intent, requestSelectDistrict)
            presenter.cityId?.let {
                startActivityForResult<SelectDistrictStampActivity, Int>(Constant.DATA_1, it, requestSelectDistrict)
            }
        }

        binding.edtVariant.setOnClickListener {
            val intent = Intent(this, SelectVariantActivity::class.java)
            intent.putExtra(Constant.DATA_1, viewModel.productID)
            startActivityForResult(this, intent, requestSelectVariant)
        }

        binding.btnUpdate.setOnClickListener {
            updateUserInfo()
        }
    }

    private fun showInputDataError() {
        showShortError("Bạn cần nhập các trường yêu cầu")
    }

    private fun setupSearchCustomer() {
        disposable = RxTextView.afterTextChangeEvents(binding.edtPhone)
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewModel.updateType = if (!viewModel.phoneNumber.isNullOrEmpty() && binding.edtPhone.text.toString() != viewModel.phoneNumber) {
                        2
                    } else {
                        1
                    }
                    searchGuaranteeCustomerDetail(binding.edtPhone.text.toString())
                }, {
                    logError(it)
                })
    }

    private fun stopSerchCustomer() {
        disposable?.dispose()
        disposable = null
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

        if (viewModel.updateType != 0) {
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
            getProductVariantError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        lifecycleScope.launch {
            var productVariant: ICVariantProductStampV6_1? = null
            var customerVariant: MutableList<ICFieldGuarantee>? = null
            var guaranteeVariant: MutableList<ICFieldGuarantee>? = null

            withContext(Dispatchers.IO) {
                val listSync = mutableListOf<Deferred<Any>>()

                if (viewModel.productID != 0L) {
                    listSync.add(async {
                        productVariant = withTimeoutOrNull(APIConstants.REQUEST_TIME) { viewModel.getProductVariant() }
                    })
                }
                if (viewModel.barcode.isNotEmpty()) {
                    listSync.add(async {
                        customerVariant = withTimeoutOrNull(APIConstants.REQUEST_TIME) { viewModel.getCustomerVariant() }
                    })
                }
                if (viewModel.updateType == 1 || viewModel.updateType == 2) {
                    listSync.add(async {
                        guaranteeVariant = withTimeoutOrNull(APIConstants.REQUEST_TIME) { viewModel.getGuaranteeVariant() }
                    })
                }

                listSync.awaitAll()
            }

            withContext(Dispatchers.Main) {
                if (!productVariant?.data?.products.isNullOrEmpty()) {
                    binding.tvSubProductCode.visibility = View.GONE
                    binding.edtProductCode.visibility = View.GONE
                    binding.tvSubProductVariant.visibility = View.VISIBLE
                    binding.edtVariant.visibility = View.VISIBLE
                } else {
                    binding.tvSubProductCode.visibility = View.VISIBLE
                    binding.edtProductCode.visibility = View.VISIBLE
                    binding.tvSubProductVariant.visibility = View.GONE
                    binding.edtVariant.visibility = View.GONE
                }

                /*
                * Thông tin động của user
                * */
                customerVariant?.let { variant ->
                    for (i in variant.size - 1 downTo 0) {
                        if (variant[i].status == 0) {
                            variant.removeAt(i)
                        }
                    }
                }

                if (!customerVariant.isNullOrEmpty()) {
                    binding.rcvCustomerVariant.apply {
                        beVisible()
                        layoutManager = CustomLinearLayoutManager(this@UpdateInformationFirstActivity, LinearLayoutManager.VERTICAL, false)
                        adapter = customerVariantAdapter
                    }
                    customerVariantAdapter.addData(customerVariant!!)
                } else {
                    binding.rcvCustomerVariant.visibility = View.GONE
                }

                /*
                * Thông tin động của bảo hành
                * */
                guaranteeVariant?.let { variant ->
                    for (i in variant.size - 1 downTo 0) {
                        if (variant[i].status == 0) {
                            variant.removeAt(i)
                        }
                    }
                }

                if (!guaranteeVariant.isNullOrEmpty()) {
                    binding.tvTitleField.visibility = View.VISIBLE
                    binding.rcvField.apply {
                        beVisible()
                        binding.rcvField.layoutManager = CustomLinearLayoutManager(this@UpdateInformationFirstActivity, LinearLayoutManager.VERTICAL, false)
                        binding.rcvField.adapter = guaranteeVariantAdapter
                    }
                    guaranteeVariantAdapter.addData(guaranteeVariant!!)
                } else {
                    binding.tvTitleField.visibility = View.GONE
                    binding.rcvField.visibility = View.GONE
                }

                when (viewModel.updateType) {
                    1 -> {
                        //show layout ForceUpdate
                        binding.tvViewForceUpdate.visibility = View.VISIBLE
                        binding.edtProductCode.setText(viewModel.productCode)
                        binding.edtVariant.setText(viewModel.objVariant?.extra)
                        if (!viewModel.phoneNumber.isNullOrEmpty()) {
                            binding.edtPhone.setText(viewModel.phoneNumber)
                        } else {
                            val phone = SessionManager.session.user?.phone
                            if (!phone.isNullOrEmpty()) {
                                binding.edtPhone.setText(phone)
                            }
                        }
                    }
                    2 -> {
                        //getData customer ve va show len view
                        binding.tvViewVerifiedPhoneNumber.visibility = View.VISIBLE
                        binding.edtProductCode.setText(viewModel.productCode)
                        binding.edtVariant.setText(viewModel.objVariant?.extra)
                        if (!viewModel.phoneNumber.isNullOrEmpty()) {
                            binding.edtPhone.setText(viewModel.phoneNumber)
                        } else {
                            val phone = SessionManager.session.user?.phone
                            if (!phone.isNullOrEmpty()) {
                                binding.edtPhone.setText(phone)
                            }
                        }
                    }
                    else -> {
                        binding.tvSubProductCode.visibility = View.GONE
                        binding.edtProductCode.visibility = View.GONE
                        binding.tvSubProductVariant.visibility = View.GONE
                        binding.edtVariant.visibility = View.GONE

                        SessionManager.session.user?.let { user ->
                            stopSerchCustomer()
                            binding.edtPhone.setText(user.phone)
                            setupSearchCustomer()

                            binding.edtName.setText(Constant.getName(user.last_name, user.first_name, ""))
                            binding.edtEmail.setText(user.email)
                            binding.tvCities.text = user.city?.name
                            binding.tvDistricts.text = user.district?.name
                            presenter.cityId = user.city?.id?.toInt()
                            presenter.districtId = user.district?.id
                        }
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
                    showLongError(R.string.khong_tim_thay_tai_khoan)
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
            binding.edtName.setText(customer.name)
        }

        stopSerchCustomer()
        if (!customer.phone.isNullOrEmpty()) {
            binding.edtPhone.setText(customer.phone)
        }
        setupSearchCustomer()

        if (!customer.email.isNullOrEmpty()) {
            binding.edtEmail.setText(customer.email)
        }

        if (!customer.address.isNullOrEmpty()) {
            binding.edtAddress.setText(customer.address)
        }

        if (!viewModel.productCode.isNullOrEmpty()) {
            binding.edtProductCode.setText(viewModel.productCode)
        }

        if (!viewModel.objVariant?.extra.isNullOrEmpty()) {
            binding.edtVariant.setText(viewModel.objVariant?.extra)
        }

        customer.fields?.let { customerField ->
            for (field in customerVariantAdapter.listData) {
                customerField.find { it.key == field.key }?.let { find ->
                    if (field.type == "checkbox" || field.type == "select") {
                        find.string_values?.split(",")?.let { listSelected ->
                            try {
                                for (i in (field.valueF ?: mutableListOf()).indices) {
                                    if (listSelected.find { it.toInt() == i } != null) {
                                        field.valueF!![i].isChecked = true
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        field.string_values = find.string_values
                    }
                }
            }

            customerVariantAdapter.notifyDataSetChanged()
        }
    }

    private fun resetCustomerInfo() {
        binding.edtName.setText("")
        binding.edtEmail.setText("")
        binding.edtAddress.setText("")
    }

    private fun getBody(listData: MutableList<ICFieldGuarantee>): HashMap<String, Any>? {
        val body = hashMapOf<String, Any>()

        for (item in listData) {
            if (item.type == "input") {
                if (item.require == 1) {
                    if (item.string_values.isNullOrEmpty()) {
                        return null
                    }
                }
                if (!item.string_values.isNullOrEmpty()) {
                    body[item.key] = item.string_values!!
                }
            }

            if (item.type == "textarea") {
                if (item.require == 1) {
                    if (item.string_values.isNullOrEmpty()) {
                        return null
                    }
                }
                if (!item.string_values.isNullOrEmpty()) {
                    body[item.key] = item.string_values!!
                }
            }

            if (item.type == "date") {
                if (item.require == 1 && item.string_values.isNullOrEmpty()) {
                    return null
                }
                if (!item.string_values.isNullOrEmpty()) {
                    body[item.key] = item.string_values!!
                }
            }

            if (item.type == "select") {
                val stringBuilder = getInputValue(item.valueF)
                if (item.require == 1 && stringBuilder.isEmpty()) {
                    return null
                }
                if (stringBuilder.isNotEmpty()) {
                    body[item.key] = stringBuilder
                }
            }

            if (item.type == "checkbox") {
                val stringBuilder = getInputValue(item.valueF)
                if (item.require == 1 && stringBuilder.isEmpty()) {
                    return null
                }
                if (stringBuilder.isNotEmpty()) {
                    body[item.key] = stringBuilder
                }
            }
        }

        return body
    }

    private fun getInputValue(list: MutableList<ValueFItem>?): String {
        val stringBuilder = StringBuilder()
        for (item in list ?: mutableListOf()) {
            if (item.isChecked) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(",")
                }
                stringBuilder.append(item.id)
            }
        }
        return stringBuilder.toString()
    }

    private fun updateUserInfo() {
        val customerData = getBody(customerVariantAdapter.listData)
        if (customerData == null) {
            showInputDataError()
            return
        }

        val guaranteeData = getBody(guaranteeVariantAdapter.listData)
        if (guaranteeData == null) {
            showInputDataError()
            return
        }

        val name = binding.edtName.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val address = binding.edtAddress.text.toString().trim()

        val validName = ValidHelper.validName(this, name)
        if (validName != null) {
            showLongError(validName)
            return
        }

        val validPhone = ValidHelper.validPhoneNumber(this, phone)
        if (validPhone != null) {
            showLongError(validPhone)
            return
        }

        if (presenter.cityId ?: 0 <= 0) {
            showLongError(getString(R.string.tinh_thanh_khong_duoc_de_trong))
            return
        }

        if (presenter.districtId ?: 0 <= 0) {
            showLongError(getString(R.string.quan_huyen_khong_duoc_de_trong))
            return
        }

        if (address.isEmpty()) {
            showLongError(getString(R.string.dia_chi_khong_duoc_de_trong))
            return
        }

        val user = ICUpdateCustomerGuarantee(name, phone, email, address, presenter.districtId, presenter.cityId, customerData)

        if (viewModel.serial.isEmpty()) {
            finishActivity(RESULT_OK, Intent().apply {
                putExtra(Constant.DATA_1, user)
            })
            return
        }

        presenter.validUpdateInformationGuarantee(
                user = user,
                productCode = binding.edtProductCode.text.toString(),
                variant = viewModel.objVariant?.id,
                customerData = customerData,
                guaranteeData = guaranteeData,
                barcode = viewModel.barcode,
                updateType = viewModel.updateType,
                serial = viewModel.serial)
    }

    override fun onGetDetailStampSuccess(obj: ICDetailStampV6_1) {
        finish()
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
    }

    override fun onGetNameCitySuccess(obj: ICNameCity) {
        presenter.cityId = obj.data?.id
        binding.tvCities.text = obj.data?.name
    }

    @SuppressLint("SetTextI18n")
    override fun onGetNameCityFail() {
        binding.tvCities.text = ""
    }

    override fun onGetNameDistrictSuccess(obj: ICNameDistricts) {
        binding.tvDistricts.text = obj.data?.name
    }

    @SuppressLint("SetTextI18n")
    override fun onGetNameDistrictFail() {
        binding.tvDistricts.text = ""
    }

    override fun onShowError(message: String) {
        showShortError(message)
    }

    override fun onErrorProductCode(message: String) {
        showShortError(message)
    }

    @SuppressLint("SetTextI18n")
    override fun onSetCityName(name: String, id: Int) {
        binding.tvCities.text = name
        presenter.cityId = id
        binding.tvDistricts.text = ""
    }

    override fun onSetDistrictName(name: String, id: Int) {
        binding.tvDistricts.text = name
        presenter.districtId = id
    }

    override fun onSendOtpGuaranteeSuccess(user: ICUpdateCustomerGuarantee, productCode: String,
                                           variant: Long?, customerData: HashMap<String, Any>,
                                           guaranteeData: HashMap<String, Any>) {

        val intent = Intent(this, VerifyOTPGuaranteeActivity::class.java)
        intent.putExtra(Constant.DATA_1, user)
        intent.putExtra(Constant.DATA_2, viewModel.serial)
        intent.putExtra(Constant.DATA_3, productCode)
        intent.putExtra(Constant.DATA_4, variant)
        intent.putExtra(Constant.DATA_5, guaranteeData)
        startActivityForResult(this, intent, requestChangeData)
    }

    override fun updateInformationCusomterGuaranteeSuccess(user: ICUpdateCustomerGuarantee) {
        for (act in StampDetailActivity.listActivities) {
            act.finish()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA))
            showShortSuccess(rText(R.string.cap_nhat_thong_tin_thanh_cong))
        }
    }

    override fun updateInformationCusomterGuaranteeFail() {
        showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestChangeData -> {
                    val user = try {
                        data?.getSerializableExtra(Constant.DATA_1) as ICUpdateCustomerGuarantee?
                    } catch (e: Exception) {
                        null
                    }

                    setResult(RESULT_OK, Intent().apply {
                        putExtra(Constant.DATA_1, user)
                    })
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
                    binding.edtVariant.setText(variantName)
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
