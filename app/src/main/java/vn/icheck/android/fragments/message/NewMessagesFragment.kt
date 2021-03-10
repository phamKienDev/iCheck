package vn.icheck.android.fragments.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_message_new.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.WrapContentLinearLayoutManager
import vn.icheck.android.activities.chat.v2.ChatV2Activity
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.fragments.message.model.ICMessage
import vn.icheck.android.fragments.message.model.MsgType
import vn.icheck.android.fragments.message.model.UserToUserModel
import vn.icheck.android.network.base.SessionManager

class NewMessagesFragment : Fragment() {
    lateinit var messageViewModel: MessageViewModel
    lateinit var msgPagedAdapter: MsgPagedAdapter
    lateinit var newMsgPagedAdapter: NewMsgPagedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_new, container, false)
    }

    companion object {
        var instance: NewMessagesFragment? = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root_swipe.isRefreshing = true
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        if (!::messageViewModel.isInitialized) {
            instance = this
            messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
            msgPagedAdapter = MsgPagedAdapter()
            newMsgPagedAdapter = NewMsgPagedAdapter()
            rcv_messages.adapter = newMsgPagedAdapter
            rcv_messages.layoutManager = WrapContentLinearLayoutManager(context)
            root_swipe.setOnRefreshListener {
                lifecycleScope.launch {
                    messageViewModel.invalidate()
                    delay(200)
                    root_swipe.isRefreshing = false

                }
            }
            messageViewModel.newListMsg.observe(this, Observer {
                newMsgPagedAdapter.submitList(it)
                root_swipe.isRefreshing = false
                if (it.size == 0) {
                    rcv_messages.visibility = View.GONE
                    no_msg.visibility = View.VISIBLE
                } else {
                    rcv_messages.visibility = View.VISIBLE
                    no_msg.visibility = View.GONE
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        instance = null
    }

    fun openNewMsg(icMessage: ICMessage) {
//        messageViewModel.messageDao.updateUnread(0, icMessage.id)
        activity?.let {
            //                        ChatActivity.createChatUser(userToUserModel.id, it)
            if (icMessage.msgType == MsgType.TYPE_USER_2_USER) {
                ChatV2Activity.createNewChat(icMessage.userId, it)
            } else {
                ChatV2Activity.createGroupChat(icMessage.id, icMessage.roomName!!,icMessage.avatar, it)
            }
        }
    }

    fun openMessage(userToUserModel: UserToUserModel) {
        activity?.let {
            //                        ChatActivity.createChatUser(userToUserModel.id, it)
            ChatV2Activity.createNewChat(userToUserModel.id, it)
        }
    }

    fun confirmDelete(icMessage: ICMessage) {
        val dialog = ConfirmDeleteUserMsg(icMessage)
        dialog.show(childFragmentManager, null)
    }

    fun deleteMessage(icMessage: ICMessage) {
        messageViewModel.delete(icMessage)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        try {
            when (event.type) {
                ICMessageEvent.Type.ON_LOG_OUT -> {
                    if (::messageViewModel.isInitialized) {
                        messageViewModel.messageDao.delete()
                    }
                }
                ICMessageEvent.Type.ON_LOG_IN_FIREBASE -> {
                    SessionManager.session.user?.let {
                        //                    messageViewModel.msgDataSourceFactory.messageLiveData.value?.invalidate()
                        messageViewModel.currentUserId = it.id
                        messageViewModel.invalidate()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}