package vn.icheck.android.network.feature.base

import android.text.TextUtils
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import vn.icheck.android.network.base.*
import vn.icheck.android.network.util.JsonHelper
import java.util.concurrent.TimeUnit

open class BaseInteractor {
    val composite = CompositeDisposable()

    fun dispose() {
        composite.clear()
    }

    fun addDispose(dispose: Disposable) {
        composite.add(dispose)
    }

    fun <T : ICResponseCode> requestNewApi(observable: Observable<T>, listener: ICNewApiListener<T>) {
        val disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.statusCode == "200" || it.code == 1) {
                                listener.onSuccess(it)
                            } else {
                                it.message = APIConstants.checkErrorString(it.statusCode, it.message)

                                when (it.statusCode) {
                                    "P401" -> {
                                        SettingManager.setSessionIdPvcombank("")
                                        ICNetworkManager.onEndOfPVCombankToken()
                                    }
                                    "U102", "S402" -> {
                                        ICNetworkManager.onEndOfToken()
                                        listener.onError(it.apply {
                                            it.message = null
                                        })
                                    }
                                }

                                listener.onError(it)
                            }
                        },
                        {
                            checkRequestErrorSocial(it, listener)
                        }
                )

        composite.add(disposable)
    }


    fun <T : ICResponseCode> requestNewApi2(observable: Observable<T>, listener: ICNewApiListener<T>) {
        val disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            checkRequestErrorSocial(it, listener)
                        }
                )

        composite.add(disposable)
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

    fun <T> requestApi(single: Single<T>, listener: ICApiListener<T>) {
        val disposable = single
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

    fun <T> requestApiDelay(observable: Observable<T>, listener: ICApiListener<T>) {
        val disposable = observable
                .delay(400, TimeUnit.MILLISECONDS)
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

    fun <T> requestApi(call: Call<T>, listener: ICApiListener<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()

                if (body != null) {
                    listener.onSuccess(body)
                } else {
                    val error = JsonHelper.parseJson(response.errorBody()?.string(), ICBaseResponse::class.java)
                    listener.onError(error)
//                    checkError(error?.statusCode)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                listener.onError(null)
            }
        })
    }

    fun checkRequestError(throws: Throwable, listener: ICApiListener<*>) {
        val errorBody = ICBaseResponse()
        errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

        when (throws) {
            is RetrofitException -> {
                val error = JsonHelper.parseJson(throws.response?.errorBody()?.string(), ICBaseResponse::class.java)

                if (error != null) {
                    listener.onError(error)
//                    checkError(error.statusCode)
                    return
                }
            }
            is HttpException -> {
                val error = JsonHelper.parseJson(throws.response()?.errorBody()?.string(), ICBaseResponse::class.java)

                if (error != null) {
                    listener.onError(error)
//                    checkError(error.statusCode)
                    return
                }
            }
        }

        listener.onError(errorBody)
    }

    fun checkRequestErrorSocial(throws: Throwable, listener: ICNewApiListener<*>) {
        val errorBody = ICResponseCode()
        errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

        when (throws) {
            is RetrofitException -> {
                val error = JsonHelper.parseJson(throws.response?.errorBody()?.string(), ICResponseCode::class.java)

                if (error != null) {
                    when (error.statusCode) {
                        "U102", "S402" -> {
                            ICNetworkManager.onEndOfToken()
                            error.message = null
                        }
                        else -> {
                            error.message = APIConstants.checkErrorString(error.statusCode, error.message)
                        }
                    }

                    listener.onError(error)
                    return
                }
            }
            is HttpException -> {
                val error = JsonHelper.parseJson(throws.response()?.errorBody()?.string(), ICResponseCode::class.java)

                if (error != null) {
                    when (error.statusCode) {
                        "U102", "S402" -> {
                            ICNetworkManager.onEndOfToken()
                            error.message = null
                        }
                        else -> {
                            error.message = APIConstants.checkErrorString(error.statusCode, error.message)
                        }
                    }

                    listener.onError(error)
                    return
                }
            }
        }

        listener.onError(errorBody)
    }

    fun <T> parseJson(json: String?, clazz: Class<T>): T? {
        if (TextUtils.isEmpty(json))
            return null

        return try {
            val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            null
        }
    }
}