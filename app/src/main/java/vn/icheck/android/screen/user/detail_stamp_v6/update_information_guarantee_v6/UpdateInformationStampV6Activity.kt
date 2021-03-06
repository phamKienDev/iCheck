package vn.icheck.android.screen.user.detail_stamp_v6.update_information_guarantee_v6

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_update_information_stamp_v6.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6.IC_RESP_UpdateCustomerGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameCity
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameDistricts
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectCustomerHistoryGurantee
import vn.icheck.android.room.entity.ICDistrict
import vn.icheck.android.room.entity.ICProvince
import vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.SelectStoreStampV6Activity
import vn.icheck.android.screen.user.detail_stamp_v6.update_information_guarantee_v6.presenter.UpdateInformationStampV6Presenter
import vn.icheck.android.screen.user.detail_stamp_v6.update_information_guarantee_v6.view.IUpdateInformationStampV6View
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.SelectCityBottomSheet
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.SelectDistrictBottomSheet

class UpdateInformationStampV6Activity : BaseActivityMVVM(), IUpdateInformationStampV6View {

    val presenter = UpdateInformationStampV6Presenter(this@UpdateInformationStampV6Activity)

    private val REQUEST_CODE_STORE = 10

    private var midStamp: String? = null
    private var midStore: Int? = null
    private var cityId: Int? = null
    private var districtId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_information_stamp_v6)
        onInitView()
    }

    fun onInitView() {
        txtTitle.setText(R.string.thong_tin_khach_hang)

        setupView()

        presenter.getDataIntent(intent)
        listener()
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    private fun setupView() {
        btnUpdate.background = ViewHelper.bgSecondaryCorners40(this)
        ViewHelper.bgWhiteStrokeLineColor1Corners40(this).apply {
            edtPhone.background = this
            edtName.background = this
            edtEmail.background = this
            edtAddress.background = this
            edtShop.background = this
        }
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnUpdate.setOnClickListener {
            presenter.validUpdateInformationGuarantee(
                edtName.text.toString(),
                edtPhone.text.toString(),
                edtEmail.text.toString(),
                edtAddress.text.toString(),
                edtShop.text.toString(),
                midStore
            )
        }

        layoutSelectCity.setOnClickListener {
            val myRoundedBottomSheet = SelectCityBottomSheet()
            myRoundedBottomSheet.show(supportFragmentManager, myRoundedBottomSheet.tag)
        }

        layoutSelectDistrict.setOnClickListener {
            val myRoundedBottomSheet = SelectDistrictBottomSheet(cityId)
            myRoundedBottomSheet.show(supportFragmentManager, myRoundedBottomSheet.tag)
        }

        edtShop.setOnClickListener {
            val intent = Intent(this, SelectStoreStampV6Activity::class.java)
            intent.putExtra(Constant.DATA_1, midStamp)
            startActivityForResult(intent, REQUEST_CODE_STORE)
        }
    }

    override fun onGetDataIntentSuccess(mIdStamp: String?, objCustomer: ICObjectCustomerHistoryGurantee) {
        midStamp = mIdStamp
        if (objCustomer.phone != null) edtPhone.setText(objCustomer.phone)
        if (objCustomer.name != null) edtName.setText(objCustomer.name)
        if (objCustomer.email != null) edtEmail.setText(objCustomer.email)
        if (objCustomer.address != null) edtAddress.setText(objCustomer.address)
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

    override fun onSetCityName(name: String, id: Int) {
        tvCities.text = name
        presenter.cityId = id
        presenter.nameCity = name
        cityId = id
    }

    override fun onSetDistrictName(name: String, id: Int) {
        tvDistricts.text = name
        presenter.districtId = id
        presenter.nameDistrict = name
        districtId = id
    }

    override fun onGetNameCitySuccess(obj: ICNameCity) {
        tvCities.text = obj.data?.name
    }

    override fun onGetNameCityFail() {
        tvCities.setText(R.string.tuy_chon)
    }

    override fun onGetNameDistrictSuccess(obj: ICNameDistricts) {
        tvDistricts.text = obj.data?.name
    }

    override fun onGetNameDistrictFail() {
        tvDistricts.setText(R.string.tuy_chon)
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@UpdateInformationStampV6Activity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@UpdateInformationStampV6Activity, isShow)
    }

    override fun onUpdateInformationSuccess(obj: IC_RESP_UpdateCustomerGuaranteeV6) {
        val objUpdate = ICObjectCustomerHistoryGurantee()
        objUpdate.phone = obj.data?.phone
        objUpdate.name = obj.data?.name
        objUpdate.email = obj.data?.email
        objUpdate.address = obj.data?.address
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, objUpdate)
        setResult(Activity.RESULT_OK, intent)
        showShortSuccess(getString(R.string.cap_nhat_thong_tin_thanh_cong))
        onBackPressed()
    }

    override fun onGetDataFail(type: Int) {

    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.SELECT_CITY -> {
                presenter.selectCity(event.data as ICProvince)
            }
            ICMessageEvent.Type.SELECT_DISTRICT -> {
                presenter.selectDistrict(event.data as ICDistrict)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_STORE) {
            if (resultCode == Activity.RESULT_OK) {
                val store = data?.getStringExtra(Constant.DATA_5)
                edtShop.setText(store)

                val idStore = data?.getIntExtra(Constant.DATA_4, -1)
                midStore = idStore
            }
        }
    }
}
