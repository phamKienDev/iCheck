package vn.icheck.android.screen.user.suggest_topic

import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICSuggestTopic

interface ISuggestTopicView : IRecyclerViewCallback {
    fun onGetListTopicSelected(list: MutableList<ICSuggestTopic>)
}