package vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift.presenter

import android.content.Intent
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift.view.IComfirmShipGiftView

class ComfirmShipGiftPresenter(val view: IComfirmShipGiftView) : BaseActivityPresenter(view) {

    private val listAddress = mutableListOf<ICAddress>()
    private var obj: ICDetailGift? = null
    private var obj2: ICDetailGiftStore? = null
    private var pointRemaining: Long? = null
    private var pointExchange: Long? = null

    private val userInteraction = UserInteractor()
    private val interactor = ListCampaignInteractor()

    fun getDataIntent(intent: Intent?) {
        obj = intent?.getSerializableExtra(Constant.DATA_1) as ICDetailGift?
        if (obj != null) {
            view.onGetDataIntentSuccess(obj!!, null, null, null, 1)
        } else {
            obj2 = intent?.getSerializableExtra(Constant.DATA_2) as ICDetailGiftStore?
            if (obj2 != null) {
                pointRemaining = intent?.getLongExtra(Constant.DATA_3, 0)
                pointExchange = intent?.getLongExtra(Constant.DATA_4, 0)
                val type = intent?.getIntExtra(Constant.DATA_5, 0)
                view.onGetDataIntentSuccess(null, obj2, pointRemaining, pointExchange, type)
            } else {
                view.onGetDetailError(Constant.ERROR_EMPTY, null)
            }
        }
        getAddresses()
    }

    private fun getAddresses() {
        userInteraction.getListUserAddress(object : ICApiListener<ICListResponse<ICAddress>> {
            override fun onSuccess(obj: ICListResponse<ICAddress>) {
                listAddress.clear()
                listAddress.addAll(obj.rows)
                if (!obj.rows.isNullOrEmpty()) {
                    val address = defaultAddress(null)
                    if (address != null) {
                        view.onGetAddressDefaultSuccess(address)
                    } else {
                        view.onNoAddress()
                    }
                } else {
                    view.onNoAddress()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetDetailError(Constant.ERROR_UNKNOW, error?.message)
            }
        })
    }

    private fun defaultAddress(addressID: Long?): ICAddress? {
        for (it in listAddress) {
            if (addressID != null) {
                if (it.id == addressID) {
                    return it
                }
            } else {
                if (it.is_default == true) {
                    return it
                }
            }
        }

        return if (listAddress.isNotEmpty()) {
            listAddress[0]
        } else {
            null
        }
    }

    fun createAddress(intent: Intent?) {
        val address = try {
            JsonHelper.parseJson(intent?.getStringExtra(Constant.DATA_1), ICAddress::class.java)
        } catch (e: Exception) {
            null
        }

        address?.let {
            for (i in listAddress.size - 1 downTo 0) {
                listAddress[i].is_default = false
            }
            it.is_default = true

            if (listAddress.isNotEmpty()) {
                listAddress.add(0, it)
            } else {
                listAddress.add(it)
            }

            view.onChangeUserAddress(it)
        }
    }

    fun selectAddress(intent: Intent?) {
        val obj = JsonHelper.parseJson(intent?.getStringExtra(Constant.DATA_1), ICAddress::class.java)

        if (obj == null) {
            listAddress.clear()
            getAddresses()
            view.onSetAddressIdDefaul()
        } else {
            obj.is_default = true
            listAddress.clear()
            listAddress.add(obj)

            view.onChangeUserAddress(obj)
        }
    }

    fun onAcceptShipGift(giftID: String?, addressID: Long?, note: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDetailError(Constant.ERROR_INTERNET, null)
            return
        }

        view.onShowLoading(true)

        interactor.onAcceptGift(giftID, addressID, note, object : ICApiListener<ICAcceptGift> {
            override fun onSuccess(obj: ICAcceptGift) {
                view.onShowLoading(false)
                view.onShipGiftSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.onShipGiftFail(error?.message)
            }
        })
    }

    fun onAcceptExchangeGiftStore(giftID: String?, addressID: Long?, note: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDetailError(Constant.ERROR_INTERNET, null)
            return
        }

        view.onShowLoading(true)

        interactor.onAcceptExchangeGiftStore(giftID, addressID, note, object : ICApiListener<ICAcceptGift> {
            override fun onSuccess(obj: ICAcceptGift) {
                view.onShowLoading(false)
                if (obj.order_id != null) {
                    view.onShipGiftExchangeStoreSuccess(obj)
                } else {
                    view.onShipGiftFail(obj.message)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                view.onShipGiftFail(error?.message)
            }
        })
    }

}