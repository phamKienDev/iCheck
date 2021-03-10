package vn.icheck.android.screen.user.coinhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.coin.CoinInteractor
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.ICCoinHistory

class CoinHistoryViewModel : BaseViewModel() {
    private val interactor = CoinInteractor()
    private val settingInteraction = SettingRepository()

    var onSetData = MutableLiveData<MutableList<ICCoinHistory>>()
    var onAddData = MutableLiveData<MutableList<ICCoinHistory>>()
    var onError = MutableLiveData<ICError>()


    private var offset = 0

    private var typeXu = 0
    private var beginAt = ""
    private var endAt = ""

    fun setTypeXu(type: Int) {
        typeXu = type
    }

    fun setBeginAt(begin: String) {
        beginAt = begin
    }

    fun setEndAt(end: String) {
        endAt = end
    }

    val getTypeXu: Int
        get() = typeXu
    val getBeginAt: String
        get() = beginAt
    val getEndAt: String
        get() = endAt

    fun getCoin() = request { settingInteraction.getCoin() }

//    fun getCoin() {
//        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
//            return
//        }
//
//        settingInteraction.getCoinOfMe(object : ICNewApiListener<ICResponse<ICSummary>> {
//            override fun onSuccess(obj: ICResponse<ICSummary>) {
//                SessionManager.setCoin(obj.data?.availableBalance ?: 0)
//                onCoin.postValue(obj.data?.availableBalance ?: 0)
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COIN_AND_RANK))
//            }
//
//            override fun onError(error: ICResponseCode?) {
//            }
//        })
//    }

    fun getCointHistory(isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadmore) {
            offset = 0
        }

        val begin = if (beginAt.isNotEmpty()) {
            TimeHelper.convertDateTimeVnToDateSv(beginAt)
        } else {
            ""
        }

        val end = if (endAt.isNotEmpty()) {
            TimeHelper.convertDateTimeVnToDateSv(endAt)
        } else {
            ""
        }

        interactor.getCointHistory(offset, APIConstants.LIMIT, typeXu, begin, end, object : ICNewApiListener<ICResponse<ICListResponse<ICCoinHistory>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCoinHistory>>) {
                offset += APIConstants.LIMIT
                if (!isLoadmore)
                    onSetData.postValue(obj.data?.rows)
                else
                    onAddData.postValue(obj.data?.rows)

            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onError.postValue(ICError(R.drawable.ic_error_request, message, null, null))
            }
        })
    }
}