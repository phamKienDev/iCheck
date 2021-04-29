package vn.icheck.android.screen.user.scan_history.view_model

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.history.HistoryInteractor
import vn.icheck.android.network.models.ICValidStampSocial
import vn.icheck.android.network.models.history.ICBigCorp
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.network.models.history.ICTypeHistory
import vn.icheck.android.screen.user.scan_history.model.ICScanHistory
import vn.icheck.android.util.ick.logError

class ScanHistoryViewModel @ViewModelInject constructor(@Assisted val savedStateHandle: SavedStateHandle, val ickApi: ICKApi) : ViewModel() {

    private val interactor = HistoryInteractor()

    private val listDataMenu = mutableListOf<ICScanHistory>()

    val onAddBigCorp = MutableLiveData<MutableList<ICScanHistory>>()
    val onSetData = MutableLiveData<MutableList<ICScanHistory>>()
    val onAddData = MutableLiveData<MutableList<ICScanHistory>>()
    val onErrorListData = MutableLiveData<MutableList<ICScanHistory>>()
    val onError = MutableLiveData<ICError>()
    val onErrorString = MutableLiveData<String>()

    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    val onAddDataMenu = MutableLiveData<MutableList<ICScanHistory>>()
    val onUpdateDataMenu = MutableLiveData<ICScanHistory>()

    val listCategory = mutableListOf<ICBigCorp>()

    val stampHoaPhat = MutableLiveData<ICValidStampSocial>()
    val stampThinhLong = MutableLiveData<ICValidStampSocial>()
    val showDialogSuggestApp = MutableLiveData<ICValidStampSocial>()
    val checkStampSocial = MutableLiveData<ICValidStampSocial>()
    val stampFake = MutableLiveData<String>()
    val errorQr = MutableLiveData<String>()

    var offset = 0

    fun getData() {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                onError.postValue(ICError(R.drawable.ic_error_network, null, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
                return@launch
            }

            interactor.dispose()
            createListMenu()
            getListBigCorp()
            getListFilterType()
        }
    }

    private fun createListMenu() {
        if (listDataMenu.isEmpty()) {
            listDataMenu.add(ICScanHistory(ICViewTypes.TOOLBAR_MENU_HISTORY))
            listDataMenu.add(ICScanHistory(ICViewTypes.FILTER_TYPE_HISTORY))
            listDataMenu.add(ICScanHistory(ICViewTypes.FILTER_SHOP_HISTORY))
        }
        onAddDataMenu.postValue(listDataMenu)
    }

    private fun getListBigCorp() {
        interactor.getListBigCorp(object : ICNewApiListener<ICResponse<ICListResponse<ICBigCorp>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICBigCorp>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {

                    val itemMenu = ICScanHistory(ICViewTypes.FILTER_SHOP_HISTORY)
                    val listType = mutableListOf<ICTypeHistory>()
                    for (i in obj.data?.rows ?: mutableListOf()) {
                        listType.add(ICTypeHistory(name = i.name, idShop = i.id))
                    }
                    itemMenu.data = listType
                    onUpdateDataMenu.value = itemMenu

                    val list = mutableListOf<ICScanHistory>()
                    val item = ICScanHistory(ICViewTypes.LIST_BIG_CORP)
                    listCategory.clear()
                    listCategory.add(0, ICBigCorp(avatar_all = R.drawable.ic_all_history_31dp, name = "Tất cả"))
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        for (i in obj.data?.rows ?: mutableListOf()) {
                            listCategory.add(i)
                        }
                        item.data = listCategory
                    }
                    list.add(item)

                    val suggestShop = ICScanHistory(ICViewTypes.SUGGEST_SHOP_HISTORY)
                    list.add(suggestShop)

                    onAddBigCorp.value = list
                } else {
                    onError.postValue(ICError(R.drawable.ic_error_emty_history_topup, null, ICheckApplication.getInstance().getString(R.string.ban_chua_co_lich_su_quet_ma), null))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, null, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai), null))
            }
        })
    }

    fun getListScanHistory(sort: Int? = null, listIdBigCorp: MutableList<Any>? = null, listType: MutableList<Any>? = null, isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onErrorString.value = ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interactor.getListScanHistory(offset, sort, listIdBigCorp, listType, null, APIConstants.LATITUDE, APIConstants.LONGITUDE, object : ICNewApiListener<ICResponse<ICListResponse<ICItemHistory>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICItemHistory>>) {
                offset += APIConstants.LIMIT

                val list = mutableListOf<ICScanHistory>()

                for (i in obj.data?.rows ?: mutableListOf()) {
                    when (i.actionType) {
                        "qr-scan" -> {
                            if (i.actionData?.stampIcheck == false) {
                                list.add(ICScanHistory(ICViewTypes.QR_CODE_SCAN_HISTORY, i))
                            } else {
                                list.add(ICScanHistory(ICViewTypes.QR_PRODUCT_SCAN_HISTORY, i))
                            }
                        }
                        "scan" -> {
                            list.add(ICScanHistory(ICViewTypes.PRODUCT_SCAN_HISTORY, i))
                        }
                    }
                }

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        val item = ICScanHistory(ICViewTypes.MESSAGE_SCAN_HISTORY, ICError(R.drawable.ic_error_emty_history_topup, null, ICheckApplication.getInstance().getString(R.string.ban_chua_co_lich_su_quet_ma), R.string.thu_lai))
                        list.add(item)
                        onErrorListData.value = list
                    } else {
                        onSetData.value = list
                    }
                } else {
                    onAddData.value = list
                }
            }

            override fun onError(error: ICResponseCode?) {
                if (!isLoadMore) {
                    val list = mutableListOf<ICScanHistory>()
                    val item = ICScanHistory(ICViewTypes.MESSAGE_SCAN_HISTORY, ICError(R.drawable.ic_error_request, null, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai), R.string.thu_lai))
                    list.add(item)
                    onErrorListData.value = list
                } else {
                    onErrorString.value = ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            }
        })
    }

    private fun getListFilterType() {
        interactor.getListFilterType(object : ICNewApiListener<ICResponse<ICListResponse<ICTypeHistory>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICTypeHistory>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val itemMenu = ICScanHistory(ICViewTypes.FILTER_TYPE_HISTORY)
                    itemMenu.data = obj.data?.rows
                    onUpdateDataMenu.value = itemMenu
                }
            }

            override fun onError(error: ICResponseCode?) {

            }
        })
    }

    fun getCartCount(): LiveData<ICResponse<Int>> {
        return liveData {
            try {
                val res = ickApi.getCartCount()
                emit(res)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun checkQrStampSocial(code: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onErrorString.value = ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.checkScanQrCode(code, object : ICNewApiListener<ICResponse<ICValidStampSocial>> {
            override fun onSuccess(obj: ICResponse<ICValidStampSocial>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                when (obj.data?.theme) {
                    1 -> {
                        stampHoaPhat.postValue(obj.data)
                    }
                    2 -> {
                        stampThinhLong.postValue(obj.data)
                    }
                    else -> {
                        if (obj.data?.suggest_apps.isNullOrEmpty()) {
                            checkStampSocial.postValue(obj.data)
                        } else {
                            showDialogSuggestApp.postValue(obj.data)
                        }
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (error?.code == 400) {
                    stampFake.postValue("Sản phẩm này có dấu hiệu làm giả sản phẩm chính hãng.\nXin vui lòng liên hệ với đơn vị phân phối chính hãng để được hỗ trợ.")
                } else {
                    errorQr.postValue(code)
                }
            }
        })
    }
}