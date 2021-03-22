package vn.icheck.android.chat.icheckchat.screen.conversation

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.chat.icheckchat.base.BaseFragmentChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.USER_ID
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.setGone
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.chat.icheckchat.base.view.showToastError
import vn.icheck.android.chat.icheckchat.base.view.visibleOrGone
import vn.icheck.android.chat.icheckchat.databinding.FragmentListConversationBinding
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.chat.icheckchat.model.MCConversation
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent
import vn.icheck.android.chat.icheckchat.model.MCStatus
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity

class ListConversationFragment : BaseFragmentChat<FragmentListConversationBinding>(), IRecyclerViewCallback {

    private val adapter = ListConversationAdapter(this@ListConversationFragment)

    private lateinit var viewModel: ListConversationViewModel

    companion object {
        var isOpenChat = false

        fun finishAllChat() {
            EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.ON_FINISH_ALL_CHAT))
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentListConversationBinding {
        return FragmentListConversationBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {
        viewModel = ViewModelProvider(this@ListConversationFragment)[ListConversationViewModel::class.java]

        initRecyclerView()
        initSwipeLayout()
        initListener()
        setOnClick()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }

        binding.swipeRefresh.post {
            getData()
        }
    }

    private fun initListener() {
        viewModel.onError.observe(this@ListConversationFragment, {
            binding.swipeRefresh.isRefreshing = false
            binding.recyclerView.visibleOrGone(it.title.isNullOrEmpty())
            binding.layoutNoData.visibleOrGone(!it.title.isNullOrEmpty())

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                requireContext().showToastError(it.message)
            }
        })

        viewModel.conversationError.observe(this@ListConversationFragment, {
            binding.swipeRefresh.isRefreshing = false
            requireContext().showToastError(it)
        })
    }

    private fun getData() {
        binding.swipeRefresh.isRefreshing = true

        viewModel.loginFirebase({
            getConversation()
            getChatSender()
        }, {
            binding.swipeRefresh.isRefreshing = false
        })
    }

    private fun setOnClick() {
        adapter.setListener(object : ListConversationAdapter.IListener {
            override fun onClickConversation(obj: MCConversation) {
                if (obj.unreadCount != null && obj.unreadCount!! > 0L) {
                    markReadMessage(obj)
                }
                startActivity(Intent(requireContext(), ChatSocialDetailActivity::class.java).apply {
                    putExtra(ConstantChat.DATA_1, obj)
                })
            }
        })
    }

    private fun markReadMessage(obj: MCConversation) {
        viewModel.markReadMessage("user|${ShareHelperChat.getLong(USER_ID)}", obj.key
                ?: "").observe(this@ListConversationFragment, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    requireContext().showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    if (it.data?.data.isNullOrEmpty()) {
                        markReadMessage(obj)
                    }
                }
            }
        })
    }

    private fun getConversation(isLoadMore: Boolean = false) {
        viewModel.getConversation(isLoadMore, { snapshot ->
            val conversationList = mutableListOf<MCConversation>()

            if (snapshot.hasChildren()) {
                for (item in snapshot.children.reversed()) {
                    val element = MCConversation().apply {
                        key = item?.key.toString()
                        enableAlert = item.child("enable_alert").value.toString().toBoolean()
                        keyRoom = item?.key.toString()
                        unreadCount = item.child("unread_count").value as Long? ?: 0L
                        time = item.child("last_activity").child("time").value as Long?
                                ?: System.currentTimeMillis()
                        lastMessage = if (item.child("last_activity").child("content").value != null) {
                            item.child("last_activity").child("content").value.toString()
                        } else {
                            ""
                        }
                    }

                    viewModel.getChatRoom(element.keyRoom ?: "", {
                        if (it.hasChildren()) {
                            for (i in it.child("members").children) {
                                if (!FirebaseAuth.getInstance().uid.toString().contains(i.child("source_id").value.toString())) {
                                    viewModel.getChatSender(i.child("id").value.toString(), { success ->
                                        element.targetUserName = success.child("name").value.toString()
                                        element.imageTargetUser = success.child("image").value.toString()

                                        adapter.refreshItem(element)
                                    }, {

                                    })
                                }else{
                                    element.isNotification = i.child("is_subscribe").value.toString().toBoolean()
                                    adapter.refreshItem(element)
                                }
                            }
                        }
                    }, {

                    })

                    conversationList.add(element)
                }
            }

            binding.swipeRefresh.isRefreshing = false

            if (!conversationList.isNullOrEmpty()) {
                binding.recyclerView.setVisible()
                binding.layoutNoData.setGone()
                adapter.setData(conversationList)
            }
//            if (!isLoadMore) {
//                if (conversationList.isNullOrEmpty()) {
//                    viewModel.checkError(true, dataEmpty = true)
//                } else {
//                    binding.recyclerView.setVisible()
//                    binding.layoutNoData.setGone()
//                    binding.swipeRefresh.isRefreshing = false
//
//                    adapter.setListData(conversationList)
//                }
//            } else {
//                adapter.addListData(conversationList)
//            }
        }, { error ->
            viewModel.checkError(true, message = error.message)
        })
    }

    private fun getChatSender() {
        viewModel.getChatSender("user|${ShareHelperChat.getLong(USER_ID)}", { snapshot ->
            viewModel.unreadCount = try {
                if (snapshot.hasChildren()) {
                    if (snapshot.child("unread_count").exists()) {
                        snapshot.child("unread_count").value as Long
                    } else {
                        0
                    }
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            }
        }, {
            viewModel.unreadCount = 0
        })
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        getConversation(true)
    }

    override fun onResume() {
        super.onResume()
        isOpenChat = false
    }
}