package vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_comfirm_ship_gift.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotEnoughPointListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICAcceptGift
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.network.models.ICDetailGift
import vn.icheck.android.network.models.ICDetailGiftStore
import vn.icheck.android.screen.user.createuseraddress.CreateUserAddressActivity
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift.presenter.ComfirmShipGiftPresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift.view.IComfirmShipGiftView
import vn.icheck.android.screen.user.orderdetail.OrderDetailActivity
import vn.icheck.android.screen.user.selectuseraddress.SelectUserAddressActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ComfirmShipGiftActivity : BaseActivity<ComfirmShipGiftPresenter>(), IComfirmShipGiftView {

    override val getLayoutID: Int
        get() = R.layout.activity_comfirm_ship_gift

    override val getPresenter: ComfirmShipGiftPresenter
        get() = ComfirmShipGiftPresenter(this)

    private var addressID: Long? = null
    private var giftID: String? = null
    private var mPointRemaining: Long? = null
    private var mPointExchange: Long? = null
    private var mType: Int? = null

    private val requestCreateUserAddress = 1
    private val requestSelectUserAddress = 2

    override fun onInitView() {
        txtTitle.text = "Tiến hành ship quà"
        presenter.getDataIntent(intent)
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnAdd.setOnClickListener {
            startActivityForResult<CreateUserAddressActivity>(requestCreateUserAddress)
        }

        btnEditAddress.setOnClickListener {
            val intent = Intent(this, SelectUserAddressActivity::class.java)
            intent.putExtra(Constant.DATA_2, addressID)
            ActivityUtils.startActivityForResult(this, intent, requestSelectUserAddress)
        }

        btnAcceptGift.setOnClickListener {
            if (addressID != null) {
                if (mType == 1) {
                    presenter.onAcceptShipGift(giftID, addressID, edtNote.text.toString())
                } else {
                    presenter.onAcceptExchangeGiftStore(giftID, addressID, edtNote.text.toString())
                }
            } else {
                showShortError("Bạn cần cập nhật địa chỉ")
                return@setOnClickListener
            }
        }
    }

    override fun onGetDataIntentSuccess(obj1: ICDetailGift?, obj2: ICDetailGiftStore?, pointRemaining: Long?, pointExchange: Long?, type: Int?) {
        if (obj1 != null) {
            giftID = obj1.id
        } else {
            giftID = obj2?.id.toString()
            mPointRemaining = pointRemaining
            mPointExchange = pointExchange
        }

        mType = type

        tvNameProduct.text = if (obj1 != null) obj1.name else obj2?.name

        if (obj1 != null) {
            WidgetUtils.loadImageUrlRounded4(imgGift, obj1.image)
        } else {
            WidgetUtils.loadImageUrlRounded4(imgGift, obj2?.image)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onNoAddress() {
        btnEditAddress.visibility = View.GONE
        tvAddress.visibility = View.GONE
        btnAdd.visibility = View.VISIBLE

        tvName.text = SessionManager.session.user?.last_name + " " + SessionManager.session.user?.first_name
        tvPhoneNumber.text = SessionManager.session.user?.phone
        tvAddress.text = null
    }

    override fun onSetAddressIdDefaul() {
        addressID = null
    }

    override fun onChangeUserAddress(address: ICAddress) {
        layoutHeader.visibility = View.VISIBLE
        tvAddress.visibility = View.VISIBLE
        tvName.visibility = View.VISIBLE
        tvPhoneNumber.visibility = View.VISIBLE
        btnAdd.visibility = View.GONE
        btnEditAddress.visibility = View.VISIBLE

        addressID = address.id

        tvName.text = (address.last_name + " " + address.first_name)
        tvPhoneNumber.text = address.phone
        tvAddress.text = (address.address + getAddress(address.ward?.name) + getAddress(address.district?.name) + getAddress(address.city?.name))
    }

    override fun onGetAddressDefaultSuccess(address: ICAddress) {
        layoutHeader.visibility = View.VISIBLE
        tvAddress.visibility = View.VISIBLE
        tvName.visibility = View.VISIBLE
        tvPhoneNumber.visibility = View.VISIBLE
        btnAdd.visibility = View.GONE

        addressID = address.id

        tvName.text = (address.last_name + " " + address.first_name)
        tvPhoneNumber.text = address.phone
        tvAddress.text = (address.address + getAddress(address.ward?.name) + getAddress(address.district?.name) + getAddress(address.city?.name))
    }

    private fun getAddress(address: String?): String {
        return if (!address.isNullOrEmpty()) {
            ", $address"
        } else {
            ""
        }
    }

    override fun onGetDetailError(errorType: Int, message: String?) {
        when (errorType) {
            Constant.ERROR_SERVER -> {
                showShortError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            }

            Constant.ERROR_UNKNOW -> {
                message?.let {
                    showShortError(it)
                }
            }

            Constant.ERROR_EMPTY -> {
                DialogHelper.showNotification(this@ComfirmShipGiftActivity, R.string.icheck_thong_bao, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        }
    }

    override fun onShipGiftSuccess(obj: ICAcceptGift) {
        DialogHelper.showDialogAcceptShipGift(this@ComfirmShipGiftActivity, false, object : NotEnoughPointListener {
            override fun onClose() {
                DialogHelper.closeLoading(this@ComfirmShipGiftActivity)
            }

            override fun onGiveIcoin() {
                DialogHelper.closeLoading(this@ComfirmShipGiftActivity)
                val intent = Intent(this@ComfirmShipGiftActivity, OrderDetailActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.order_id)
                startActivity(intent)
            }
        })
    }

    override fun onShipGiftExchangeStoreSuccess(obj: ICAcceptGift) {
        SettingManager.setUserCoin(mPointRemaining!! - mPointExchange!!)
        DialogHelper.showDialogAcceptShipGift(this@ComfirmShipGiftActivity, false, object : NotEnoughPointListener {
            override fun onClose() {
                DialogHelper.closeLoading(this@ComfirmShipGiftActivity)
            }

            override fun onGiveIcoin() {
                DialogHelper.closeLoading(this@ComfirmShipGiftActivity)
                val intent = Intent(this@ComfirmShipGiftActivity, OrderDetailActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj.order_id)
                startActivity(intent)
            }
        })
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REFRESH_DATA_GIFT_STORE))
    }

    override fun onShipGiftFail(message: String?) {
        message?.let {
            showShortError(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCreateUserAddress -> {
                    presenter.createAddress(data)
                }
                requestSelectUserAddress -> {
                    presenter.selectAddress(data)
                }
            }
        }
    }
}
