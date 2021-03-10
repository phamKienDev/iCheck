package vn.icheck.android.network.feature.chat

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICUserId
import vn.icheck.android.network.models.RoomMessages
import java.util.function.BiFunction

class MessagesRepository {

    fun getAllUser(list: List<RoomMessages>, error: MutableLiveData<Throwable>, response: MutableLiveData<ICUserId>) {
        Observable.fromIterable(list)
                .flatMap { item ->
                    ICNetworkClient.getApiClient().getUser(item.idNumber!!)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ICUserId> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: ICUserId) {
                        response.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        error.postValue(e)
                    }
                })
    }
}