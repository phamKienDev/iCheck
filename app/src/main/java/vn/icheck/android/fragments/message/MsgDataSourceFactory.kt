package vn.icheck.android.fragments.message

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import vn.icheck.android.fragments.message.model.MsgModel

class MsgDataSourceFactory:DataSource.Factory<Int, MsgModel>(){
    val messageLiveData = MutableLiveData<MessagesDataSource>()
    override fun create(): DataSource<Int, MsgModel> {
        val messagesDataSource = MessagesDataSource()
        messageLiveData.postValue(messagesDataSource)
        return messagesDataSource
    }
}