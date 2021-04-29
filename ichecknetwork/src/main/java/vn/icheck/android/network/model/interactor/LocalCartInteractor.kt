package vn.icheck.android.network.model.interactor

//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import vn.icheck.android.ICheckApplication
//import vn.icheck.android.network.base.ICApiListener
//import vn.icheck.android.network.models.ICItemCart
//import vn.icheck.android.room.database.AppDatabase
//import vn.icheck.android.room.entity.ICCart

class LocalCartInteractor {
//    private val cartDao = AppDatabase.getDatabase(ICheckApplication.getInstance().applicationContext).cartDao()
//
//    private var disposable: Disposable? = null
//
//    fun getListCartOfflineRealTime(listener: ICApiListener<MutableList<ICCart>>) {
//        disposable = cartDao.getAllCartRealTime()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { listCart ->
//                    if (!listCart.isNullOrEmpty()) {
//                        listener.onSuccess(listCart)
//                    } else {
//                        listener.onSuccess(mutableListOf())
//                    }
//                }
//    }
//
//    fun getListCartsOffline(listener: ICApiListener<MutableList<ICCart>>) {
//        disposable = cartDao.getAllCart()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { listCart ->
//                    if (!listCart.isNullOrEmpty()) {
//                        listener.onSuccess(listCart)
//                    } else {
//                        listener.onSuccess(mutableListOf())
//                    }
//                }
//    }
//
//    fun getListItemCartOffline(listener: ICApiListener<MutableList<ICItemCart>>) {
//        disposable = cartDao.getAllCartRealTime()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { listCart ->
//                    val list = mutableListOf<ICItemCart>()
//
//                    if (!listCart.isNullOrEmpty()) {
//                        for (cart in listCart) {
//                            for (item in cart.items) {
//                                list.add(item)
//                            }
//                        }
//                    }
//
//                    listener.onSuccess(list)
//                }
//    }
//
//    fun dispose() {
//        disposable?.dispose()
//        disposable = null
//    }
}