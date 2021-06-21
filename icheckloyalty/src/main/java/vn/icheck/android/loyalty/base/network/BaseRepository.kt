package vn.icheck.android.loyalty.base.network

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import vn.icheck.android.ichecklibs.util.RStringUtils
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.helper.JsonHelper
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.network.RetrofitException

internal open class BaseRepository {
    private val composite = CompositeDisposable()

    fun dispose() {
        composite.clear()
    }

    fun <T> requestApi(observable: Observable<T>, listener: ICApiListener<T>) {
        val disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            checkRequestError(it, listener)
                        }
                )

        composite.add(disposable)
    }

    private fun checkRequestError(throws: Throwable, listener: ICApiListener<*>) {
        val errorBody = ICKBaseResponse()
        errorBody.message = RStringUtils.rText(R.string.co_loi_xay_ra_vui_long_thu_lai)

        when (throws) {
            is RetrofitException -> {
                val error = JsonHelper.parseJson(throws.response?.errorBody()?.string(), ICKBaseResponse::class.java)

                if (error != null) {
                    listener.onError(error)
                    return
                }
            }
            is HttpException -> {
                val error = JsonHelper.parseJson(throws.response()?.errorBody()?.string(), ICKBaseResponse::class.java)

                if (error != null) {
                    listener.onError(error)
                    return
                }
            }
        }

        listener.onError(errorBody)
    }
}