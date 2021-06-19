package vn.icheck.android.network.base

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import vn.icheck.android.ichecklibs.util.RStringUtils
import vn.icheck.android.network.R
import vn.icheck.android.network.util.JsonHelper

open class BaseRepository {
    val composite = CompositeDisposable()

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
        val errorBody = ICBaseResponse()
        errorBody.message = RStringUtils.rText(R.string.co_loi_xay_ra_vui_long_thu_lai)

        when (throws) {
            is RetrofitException -> {
                val error = JsonHelper.parseJson(throws.response?.errorBody()?.string(), ICBaseResponse::class.java)

                if (error != null) {
                    listener.onError(error)
                    return
                }
            }
            is HttpException -> {
                val error = JsonHelper.parseJson(throws.response()?.errorBody()?.string(), ICBaseResponse::class.java)

                if (error != null) {
                    listener.onError(error)
                    return
                }
            }
        }

        listener.onError(errorBody)
    }

    fun <T> parseJson(json: String?, clazz: Class<T>) = JsonHelper.parseJson(json, clazz)
}