package vn.icheck.android.screen.user.map_scan_history

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.history.HistoryInteractor
import vn.icheck.android.network.models.history.ICRoutesShop
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.network.util.JsonHelper.parseListStoreSellHistory
import vn.map4d.types.MFLocationCoordinate

class MapScanHistoryViewModel : ViewModel() {
    private val interactor = HistoryInteractor()

    val setListData = MutableLiveData<MutableList<ICStoreNear>>()
    val addListData = MutableLiveData<MutableList<ICStoreNear>>()
    val listRoute = MutableLiveData<MutableList<MFLocationCoordinate>>()

    var shopID = 0L
    var latShop = 0.0
    var lonShop = 0.0
    var productID = 0L
    var isPage = false
    var avatarShop = ""
    var offset = 0

    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val onError = MutableLiveData<Int>()
    val onShowErrorMessage = MutableLiveData<String>()

    fun getData(intent: Intent?) {
        val json = intent?.getStringExtra(Constant.DATA_1)
        shopID = intent?.getLongExtra(Constant.DATA_2, 0L) ?: 0L
        latShop = intent?.getDoubleExtra(Constant.DATA_3, 0.0) ?: 0.0
        lonShop = intent?.getDoubleExtra(Constant.DATA_4, 0.0) ?: 0.0
        productID = intent?.getLongExtra(Constant.DATA_5, 0) ?: 0L
        isPage = intent?.getBooleanExtra("isPage", false) ?: false
        avatarShop = intent?.getStringExtra("avatarShop") ?: ""

        val listShop = parseListStoreSellHistory(json)
        if (!listShop.isNullOrEmpty()) {
            setListData.postValue(listShop!!)
            offset = listShop.size

            for (shop in listShop) {
                if (shop.id == shopID) {
                    if (shop.id != null && shop.location != null) {
                        latShop = shop.location?.lat ?: 0.0
                        lonShop = shop.location?.lon ?: 0.0
                        getLocationShop(latShop, lonShop)
                    }
                }
            }
        } else {
            // request api
            getLocationShop(latShop, lonShop)
            getStoreNear()
        }
    }

    fun getLocationShop(latShop: Double, lonShop: Double) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            if (!NetworkHelper.isOpenedGPS(ICheckApplication.getInstance())) {
                return@launch
            }

            if (APIConstants.LATITUDE == 0.0 || APIConstants.LONGITUDE == 0.0) {
                onShowErrorMessage.postValue( getString(R.string.khong_lay_duoc_vi_tri_cua_ban_vui_long_kiem_tra_lai_thiet_bi))
                return@launch
            }

            if (latShop == 0.0 || lonShop == 0.0) {
                onShowErrorMessage.postValue( getString(R.string.khong_lay_duoc_vi_tri_cua_cua_hang))
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
            interactor.dispose()

            if (latShop != 0.0 && lonShop != 0.0) {
                interactor.getRouteShop(
                    latShop,
                    lonShop,
                    object : ICNewApiListener<ICResponse<MutableList<ICRoutesShop>>> {
                        override fun onSuccess(obj: ICResponse<MutableList<ICRoutesShop>>) {
                            statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                            val list = mutableListOf<MFLocationCoordinate>()
                            if (!obj.data.isNullOrEmpty()) {
                                if (!obj.data!![0].legs.isNullOrEmpty()) {
                                    for (step in obj.data!![0].legs!![0].steps ?: mutableListOf()) {
                                        for (location in step.locations ?: mutableListOf()) {
                                            list.add(
                                                MFLocationCoordinate(
                                                    location.lat ?: 0.0,
                                                    location.lon ?: 0.0
                                                )
                                            )
                                        }
                                    }
                                }
                                listRoute.postValue(list)
                            } else {
                                onError.postValue(Constant.ERROR_SERVER)
                            }
                        }

                        override fun onError(error: ICResponseCode?) {
                            statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                            onError.postValue(Constant.ERROR_SERVER)
                        }
                    })
            }
        }
    }

    fun getStoreNear(isLoadmore: Boolean = false) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
            if (!isLoadmore)
                offset = 0

            interactor.getStoreNear(productID, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICStoreNear>>> {
                    override fun onSuccess(obj: ICResponse<ICListResponse<ICStoreNear>>) {
                        statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                        if (!isLoadmore) {
                            if (!obj.data?.rows.isNullOrEmpty()) {
                                setListData.postValue(obj.data?.rows)
                            } else {
                                onError.postValue(Constant.ERROR_EMPTY)
                            }

                        } else {
                            addListData.postValue(obj.data?.rows)
                        }
                        offset += APIConstants.LIMIT
                    }

                    override fun onError(error: ICResponseCode?) {
                        statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                        onError.postValue(Constant.ERROR_EMPTY)
                    }
                })
        }
    }
}