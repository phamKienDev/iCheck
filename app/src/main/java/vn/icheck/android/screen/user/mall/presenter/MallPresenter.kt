package vn.icheck.android.screen.user.mall.presenter

import android.os.Handler
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.feature.business.BusinessInteractor
import vn.icheck.android.network.feature.category.CategoryInteractor
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.models.ICBusiness
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.screen.user.mall.ICMall
import vn.icheck.android.screen.user.mall.view.IMallView

/**
 * Created by VuLCL on 9/27/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class MallPresenter(val view: IMallView) : BaseFragmentPresenter(view) {
    private val categoryInteraction = CategoryInteractor()
    private val adsInteraction = AdsRepository()
    private val businessInteraction = BusinessInteractor()

    private var isClearData = true
    private var errorCount = 0

    fun checkInternet() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            getDataError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        isClearData = true
        errorCount = 0

        categoryInteraction.dispose()
        adsInteraction.dispose()
        businessInteraction.dispose()

        getListCategories()
        getTopBanner()
        getListBusiness()
        getListCampaign()
    }

    private fun getDataError(icon: Int, errorMessage: String) {
        Handler().postDelayed({
            view.closeLoading()
            view.onGetDataError(icon, errorMessage)
        }, 500)
    }

    @Synchronized
    private fun finishResponse(isSuccess: Boolean) {
        if (isClearData) {
            view.onResetData()
            isClearData = false
        }

        if (!isSuccess) {
            errorCount++
        }

        if (errorCount >= 4) {
            view.closeLoading()
            getDataError(R.drawable.ic_error_request, getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai))
        }
    }

    private fun getListCategories() {
        categoryInteraction.getListCategories(object : ICApiListener<ICListResponse<ICCategory>> {
            override fun onSuccess(obj: ICListResponse<ICCategory>) {
                view.closeLoading()

                if (!obj.rows.isNullOrEmpty()) {
                    view.onGetListCategorySuccess(obj.rows)
                }

                finishResponse(true)
            }

            override fun onError(error: ICBaseResponse?) {
                finishResponse(false)
            }
        })
    }

    private fun getTopBanner() {
        adsInteraction.getAds("mall_top_banner", null, null, null, object : ICApiListener<ICListResponse<ICAds>> {
            override fun onSuccess(obj: ICListResponse<ICAds>) {
                view.closeLoading()

                finishResponse(true)

                if (obj.rows.isNotEmpty()) {
                    val mall = ICMall()
                    mall.listAds = obj.rows
                    mall.type = ICViewTypes.ADS_SLIDE_BANNER_TYPE
                    view.onGetListBannerSuccess(mall)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                finishResponse(false)
            }
        })
    }

    private fun getListBusiness() {
        businessInteraction.getListBusiness(object : ICApiListener<ICListResponse<ICBusiness>> {
            override fun onSuccess(obj: ICListResponse<ICBusiness>) {
                view.closeLoading()

                finishResponse(true)

                if (!obj.rows.isNullOrEmpty()) {
                    val mall = ICMall()
                    mall.type = ICViewTypes.BUSINESS_TYPE
                    mall.business = obj.rows
                    view.onGetListBusinessSuccess(mall)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                finishResponse(false)
            }
        })
    }

    private fun getListCampaign() {
        adsInteraction.getAds("mall_campaign", null, null, 6, object : ICApiListener<ICListResponse<ICAds>> {
            override fun onSuccess(obj: ICListResponse<ICAds>) {
                view.closeLoading()

                finishResponse(true)

                view.getLifecycleScope.launch {
                    if (obj.rows.isNotEmpty()) {
                        val list = mutableListOf<ICMall>()

                        for (rows in obj.rows) {
                            when (rows.type) {
                                Constant.BANNER -> {
                                    if (!rows.collection?.products.isNullOrEmpty()) {
                                        val mall = ICMall()
                                        mall.ads = rows
                                        mall.type = if (rows.display_config?.type == Constant.GRID_TYPE) {
                                            ICViewTypes.ADS_LIST_PRODUCT_VERTICAL
                                        } else {
                                            ICViewTypes.ADS_LIST_PRODUCT_HORIZONTAL
                                        }
                                        list.add(mall)
                                    } else {
                                        val mall = ICMall()
                                        mall.ads = rows
                                        mall.type =  ICViewTypes.ADS_BANNER_TYPE
                                        list.add(mall)
                                    }
                                }
                                Constant.BANNER_SURVEY -> {
                                    val mall = ICMall()
                                    mall.ads = rows
                                    mall.type =  ICViewTypes.ADS_BANNER_TYPE
                                    list.add(mall)
                                }
                                Constant.COLLECTION -> {
                                    if (!rows.collection?.products.isNullOrEmpty()) {
                                        val mall = ICMall()
                                        mall.ads = rows
                                        mall.type = if (rows.display_config?.type == Constant.GRID_TYPE) {
                                            ICViewTypes.ADS_LIST_PRODUCT_VERTICAL
                                        } else {
                                            ICViewTypes.ADS_LIST_PRODUCT_HORIZONTAL
                                        }
                                        list.add(mall)
                                    }
                                }
                                Constant.DIRECT_SURVEY -> {
                                    val mall = ICMall()
                                    mall.ads = rows
                                    mall.type =  ICViewTypes.ADS_DIRECT_SURVEY_TYPE
                                    list.add(mall)
                                }
                            }
                        }

                        if (list.isNotEmpty())
                            view.onGetListCampaign(list)
                    }
                }
            }

            override fun onError(error: ICBaseResponse?) {
                finishResponse(false)
            }
        })
    }
}