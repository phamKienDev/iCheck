package vn.icheck.android.model.interactor

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import vn.icheck.android.ICheckApplication
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICQrScan

class LocalHistoryQrCodeInteractor {

    private val qrDao = AppDatabase.getDatabase(ICheckApplication.getInstance().applicationContext).qrScanDao()

    private var disposable: Disposable? = null

    fun getListHistoryScanQrOffline(listener: ICApiListener<MutableList<ICQrScan>>) {
        disposable = qrDao.getAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<MutableList<ICQrScan>> {
                    override fun accept(listCart: MutableList<ICQrScan>?) {
                        if (!listCart.isNullOrEmpty()) {
                            listener.onSuccess(listCart)
                        } else {
                            listener.onSuccess(mutableListOf())
                        }
                    }
                })
    }

    fun dispose() {
        disposable?.dispose()
        disposable = null
    }
}