package vn.icheck.android.chat.icheckchat.screen.conversation

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
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
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk
import java.util.*

class ListConversationFragment : BaseFragmentChat<FragmentListConversationBinding>(), IRecyclerViewCallback {

    private var listener: ICountMessageListener?=null
    private val adapter = ListConversationAdapter(this@ListConversationFragment)
    private lateinit var viewModel: ListConversationViewModel
    private val listData = mutableListOf<MCConversation>()

    companion object {
        var isOpenChat = false

        var isOpenConversation = false

        fun finishAllChat() {
            EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.ON_FINISH_ALL_CHAT))
        }

        interface ICountMessageListener {
            fun getCountMessage(count: Long)
            fun onClickLeftMenu()
        }
    }

    fun setListener(listener:ICountMessageListener?){
        this.listener=listener
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentListConversationBinding {
        return FragmentListConversationBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {
        isOpenConversation = true
        viewModel = ViewModelProvider(this@ListConversationFragment)[ListConversationViewModel::class.java]

        initRecyclerView()
        initSwipeLayout()
        initListener()
        initEditText()
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
//            binding.edtSearch.visibleOrGone(it.title.isNullOrEmpty())
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

        listData.clear()
        binding.edtSearch.setText("")

        viewModel.loginFirebase({
            getConversation(0)
            getChatSender()
        }, {
            binding.swipeRefresh.isRefreshing = false
            binding.recyclerView.setGone()
            binding.layoutNoData.setVisible()
        })
    }

    private fun initEditText() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.recyclerView.setGone()
                binding.layoutNoData.setGone()
                binding.layoutNoDataSearch.setGone()
                binding.imgDelete.setGone()

                if (!s.isNullOrEmpty()) {
                    listData.clear()
                    binding.imgDelete.setVisible()

                    for (item in adapter.getListData) {
                        if (item.targetUserName?.toLowerCase(Locale.ROOT)?.contains(s.toString().trim().toLowerCase(Locale.ROOT)) == true) {
                            listData.add(item)
                        }
                    }

                    if (listData.isNullOrEmpty()) {
                        binding.layoutNoDataSearch.setVisible()
                    } else {
                        binding.recyclerView.setVisible()

                        adapter.setData(listData)
                    }
                } else {
                    binding.imgDelete.setGone()

                    if (!adapter.getListData.isNullOrEmpty()) {
                        binding.recyclerView.setVisible()

                        getConversation(0)
                    } else {
                        binding.layoutNoData.setVisible()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.imgDelete.setOnClickListener {
            binding.edtSearch.setText("")
        }
    }

    private fun setOnClick() {
        adapter.setListener(object : ListConversationAdapter.IListener {
            override fun onClickConversation(obj: MCConversation) {
                startActivity(Intent(requireContext(), ChatSocialDetailActivity::class.java).apply {
                    putExtra(ConstantChat.DATA_1, obj)
                })
            }
        })
    }

    private fun loadData(snapshot: DataSnapshot, lastTimeStamp: Long) {
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
                                    element.isVerified = success.child("is_verify").value.toString().toBoolean()

                                    adapter.refreshItem(element)
                                }, {

                                })
                                element.type = i.child("type").value.toString().trim()
                            }else{
                                element.isNotification = i.child("is_subscribe").value.toString().toBoolean()
                            }
                        }
                    }
                }, {

                })

                conversationList.add(element)
            }
        }

        binding.swipeRefresh.isRefreshing = false

        if (lastTimeStamp == 0L) {
            if (conversationList.isNullOrEmpty()) {
                viewModel.checkError(true, dataEmpty = true)
            } else {
                binding.recyclerView.setVisible()
                binding.layoutNoData.setGone()
                binding.swipeRefresh.isRefreshing = false
                adapter.setListData(conversationList)
            }
        } else {
            adapter.addListData(conversationList)
        }
    }

    private fun getConversation(lastTimeStamp: Long) {
        viewModel.getConversation(lastTimeStamp, { snapshot ->
            loadData(snapshot, lastTimeStamp)
        }, { error ->
            viewModel.checkError(true, message = error.message)
        })
    }

    private fun getChatSender() {
        viewModel.getChatSender("user|${ShareHelperChat.getLong(USER_ID)}", { snapshot ->
            listener?.getCountMessage(try {
                if (snapshot.hasChildren()) {
                    if (snapshot.child("unread_count").exists()) {
                        snapshot.child("unread_count").value.toString().toLong()
                    } else {
                        0
                    }
                } else {
                    0
                }
            } catch (e: Exception) {
                0
            })
        }, {
            listener?.getCountMessage(0)
        })
    }

    fun checkLoginOrLogOut(isLogin: Boolean) {
        if (!isLogin) {
            binding.swipeRefresh.isRefreshing = false
            binding.recyclerView.setGone()
            binding.layoutNoData.setVisible()
        } else {
            getData()
        }
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        adapter.getListData.lastOrNull()?.let { obj ->
            getConversation(obj.time ?: 0)
        }
    }

    override fun onResume() {
        super.onResume()
        isOpenChat = false
        isOpenConversation = true
    }

    override fun onStop() {
        super.onStop()
        isOpenConversation = false
    }

    override fun onPause() {
        super.onPause()
        isOpenConversation = false
    }
}