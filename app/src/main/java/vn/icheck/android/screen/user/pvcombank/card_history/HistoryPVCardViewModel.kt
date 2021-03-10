package vn.icheck.android.screen.user.pvcombank.card_history

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.network.models.pvcombank.ICTransactionPVCard

class HistoryPVCardViewModel : BaseViewModel() {
    private val interactor = PVcomBankRepository()

    val onListCard = MutableLiveData<MutableList<ICListCardPVBank>>()
    val onSetTransaction = MutableLiveData<MutableList<ICTransactionPVCard.ICItemTransaction>>()
    val onAddTransaction = MutableLiveData<MutableList<ICTransactionPVCard.ICItemTransaction>>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val onError = MutableLiveData<ICError>()

    fun getListCard() = request { interactor.getMyListCards() }

    fun getListTransaction(cardId: String?, isLoadmore: Boolean = false) {
        if (cardId == null) {
            onSetTransaction.postValue(mutableListOf())
            return
        }

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
        interactor.getTransaction(cardId, object : ICNewApiListener<ICResponse<ICTransactionPVCard>> {
            override fun onSuccess(obj: ICResponse<ICTransactionPVCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!isLoadmore)
                    onSetTransaction.postValue(obj.data?.transactions)
                else
                    onAddTransaction.postValue(obj.data?.transactions)
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))

            }
        })
    }

}