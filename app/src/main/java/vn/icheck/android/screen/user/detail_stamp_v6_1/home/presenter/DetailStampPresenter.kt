package vn.icheck.android.screen.user.detail_stamp_v6_1.home.presenter

import android.content.Intent
import android.os.Handler
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampInteractor
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDetailStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICMoreProductVerified
import vn.icheck.android.network.models.detail_stamp_v6_1.ICShopVariantStamp
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView

class DetailStampPresenter(val view: IDetailStampView) : BaseActivityPresenter(view) {
//    private var bannerInteractor: AdsInteractor? = AdsInteractor()
//    private var popupInteractor: AdsInteractor? = AdsInteractor()
    private val interactor = DetailStampInteractor()
    private val cartInteraction = CartInteractor()
    private val cartHelper = CartHelper()

    var code: String? = null
    var data: String? = null
    private var mLat: String? = null
    private var mLon: String? = null

    fun onGetDataIntent(intent: Intent?, lat: String?, lon: String?) {
        data = try {
            intent?.getStringExtra("data")
        } catch (e: Exception) {
            ""
        }

        if (!data.isNullOrEmpty()) {
            code = if (data!!.contains("http")) {
                val separated: List<String> = data!!.split("/")
                separated[3]
            } else {
                data
            }
            mLat = lat
            mLon = lon

            onGetDataDetailStamp(code!!, lat, lon)
        } else {
            code = intent?.getStringExtra(Constant.DATA_1)

            if (code.isNullOrEmpty()) {
                view.onGetDataIntentError(Constant.ERROR_UNKNOW)
            } else {
                onGetDataDetailStamp(code!!, lat, lon)
            }
        }
    }

    private var mId: String? = null

    private fun onGetDataDetailStamp(code: String, lat: String?, lon: String?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        val phone = SessionManager.session.user?.phone
        val name = SessionManager.session.user?.last_name + " " + SessionManager.session.user?.first_name
        val email = SessionManager.session.user?.email
        val address = SessionManager.session.user?.address
        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        view.onShowLoading(true)

        interactor.getDetailStamp(phone,name,email,address, mId, code, lat, lon, object : ICApiListener<ICDetailStampV6_1> {
            override fun onSuccess(obj: ICDetailStampV6_1) {
                Handler().postDelayed({
                    view.onShowLoading(false)
                }, 100)

//                750x300
//                Handler().postDelayed({
//                    getBusinessBanner(obj.data?.distributor?.id, obj.data?.product?.id)
//                    getBusinessPopup(obj.data?.distributor?.id, obj.data?.product?.id)
//                }, 100)

                view.onGetDetailStampSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                if (error?.statusCode == 401) {
                    view.onGetDataRequireLogin()
                } else {
                    error?.message?.let {
                        showError(it)
                    }
                }
            }
        })
    }

    fun onGetDataMoreProductVerified(distributorId: Long?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        interactor.getListMoreProductVerifiedDistributor(distributorId, object : ICApiListener<ICMoreProductVerified> {
            override fun onSuccess(obj: ICMoreProductVerified) {
                if (!obj.data?.products.isNullOrEmpty()) {
                    view.onGetDataMoreProductVerifiedSuccess(obj.data?.products!!)
                } else {
                    view.onGetDataMoreProductVerifiedError(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun getShopVariant(lat: String?, lon: String?, sellerId: Long, barcode: String?) {
        interactor.getShopVariant(lat, lon, sellerId, barcode, object : ICApiListener<ICListResponse<ICShopVariantStamp>> {
            override fun onSuccess(obj: ICListResponse<ICShopVariantStamp>) {
                if (!obj.rows.isNullOrEmpty()) {
                    view.onGetShopVariantSuccess(obj)
                } else {
                    view.onGetShopVariantFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onGetShopVariantFail()
            }
        })
    }

    fun getConfigError() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        interactor.getConfigError(object : ICApiListener<IC_Config_Error> {
            override fun onSuccess(obj: IC_Config_Error) {
                if (obj.data != null) {
                    view.onGetConfigSuccess(obj)
                } else {
                    view.onGetDataIntentError(Constant.ERROR_UNKNOW)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }

    fun addToCart(id: Long, prouduct: ICShopVariantStamp, count: Int, type: Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        view.onShowLoading(true)

        cartInteraction.addCart(id, count, object : ICApiListener<ICRespCart> {
            override fun onSuccess(obj: ICRespCart) {
                view.onShowLoading(false)
                cartHelper.saveCart(obj)
//                InsiderHelper.tagAddToCartSuccessShopVariantStamp(prouduct, count)
                if (type == 2) {
//                    InsiderHelper.tagBuyNowSuccess()
                }
                view.onAddToCartSuccess(type)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    fun onGetDataDetailStampSecond() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataIntentError(Constant.ERROR_INTERNET)
            return
        }

        val phone = SessionManager.session.user?.phone
        val name = SessionManager.session.user?.last_name + " " + SessionManager.session.user?.first_name
        val email = SessionManager.session.user?.email
        val address = SessionManager.session.user?.address
        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        interactor.getDetailStamp(phone,name,email,address, mId, code!!, mLat, mLon, object : ICApiListener<ICDetailStampV6_1> {
            override fun onSuccess(obj: ICDetailStampV6_1) {
                view.onGetDetailStampSuccess(obj)

//                750x300
//                Handler().postDelayed({
//                    getBusinessBanner(obj.data?.distributor?.id, obj.data?.product?.id)
//                    getBusinessPopup(obj.data?.distributor?.id, obj.data?.product?.id)
//                }, 100)
            }

            override fun onError(error: ICBaseResponse?) {
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }
}