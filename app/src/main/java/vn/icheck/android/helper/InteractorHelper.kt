package vn.icheck.android.helper

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import vn.icheck.android.ICheckApplication
import vn.icheck.android.network.models.ICSurvey
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.room.database.AppDatabase

class InteractorHelper() {
    val cartDao = AppDatabase.getDatabase(ICheckApplication.getInstance()).cartDao()
    var disposable: Disposable? = null

    fun getListCardOwner(list: MutableList<ICSurvey>): MutableList<ICSurvey> {
        for (item in list) {
            if (cartDao.getCartByID(item.id) != null) {
                item.title = "owner"
            }
        }

        return list
    }
}