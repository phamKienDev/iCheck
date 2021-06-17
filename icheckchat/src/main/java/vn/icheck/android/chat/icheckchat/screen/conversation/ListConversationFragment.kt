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
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import java.util.*

class ListConversationFragment : BaseFragmentChat<FragmentListConversationBinding>(), IRecyclerViewCallback {

    private var listener: ICountMessageListener? = null
    private val adapter = ListConversationAdapter(this@ListConversationFragment)
    private lateinit var viewModel: ListConversationViewModel
    private val listData = mutableListOf<MCConversation>()

    companion object {
        var isOpenConversation = false

        interface ICountMessageListener {
            fun getCountMessage(count: Long)
            fun onClickLeftMenu()
        }
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    fun setListener(listener: ICountMessageListener?) {
        this.listener = listener
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentListConversationBinding {
        return FragmentListConversationBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {
        isOpenConversation = true
        viewModel = ViewModelProvider(this@ListConversationFragment)[ListConversationViewModel::class.java]

        initRecyclerView()
        initSwipeLayout()
        initView()
        initListener()
        initEditText()
        setOnClick()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        val primary=Constant.getPrimaryColor(requireContext())
        binding.swipeRefresh.setColorSchemeColors(primary,primary,primary)

        binding.swipeRefresh.isEnabled = ShareHelperChat.getBoolean(ConstantChat.USER_LOGIN)

        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }

        binding.swipeRefresh.post {
            getData()
        }
    }

    private fun initView() {
        binding.edtSearch.setTextColor(Constant.getNormalTextColor(requireContext()))
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

    fun getData() {
        binding.swipeRefresh.isRefreshing = true

        listData.clear()
        binding.layoutNoData.setGone()
        binding.layoutNoDataSearch.setGone()
        binding.edtSearch.removeTextChangedListener(textChangeListener)
        binding.edtSearch.setText("")
        binding.edtSearch.addTextChangedListener(textChangeListener)

        viewModel.loginFirebase({
            getConversation(0)
            getChangeConversation()
            getChatSender()
        }, {
            binding.swipeRefresh.isRefreshing = false
            binding.recyclerView.setGone()
            binding.layoutNoData.setVisible()
        })
    }

    private val textChangeListener = object : TextWatcher {
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

                    adapter.setListData(listData)
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
    }

    private fun initEditText() {
        binding.layoutSearch.visibleOrGone(ShareHelperChat.getBoolean(ConstantChat.USER_LOGIN))

        binding.edtSearch.addTextChangedListener(textChangeListener)

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

    private fun convertDataFirebase(snapshot: DataSnapshot): MCConversation {
        val element = MCConversation().apply {
            key = snapshot.key.toString()
            enableAlert = snapshot.child("enable_alert").value.toString().toBoolean()
            keyRoom = snapshot.key.toString()
            unreadCount = snapshot.child("unread_count").value as Long? ?: 0L
            time = snapshot.child("last_activity").child("time").value as Long?
                    ?: System.currentTimeMillis()
            lastMessage = if (snapshot.child("last_activity").child("content").value != null) {
                snapshot.child("last_activity").child("content").value.toString()
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
                            element.kycStatus = success.child("kyc_status").value as Long? ?: 0L

                            adapter.refreshItem(element)
                        }, {

                        })
                        element.type = i.child("type").value.toString().trim()
                    } else {
                        element.isNotification = i.child("is_subscribe").value.toString().toBoolean()
                    }
                }
            }
        }, {

        })

        return element
    }

    private fun loadData(snapshot: DataSnapshot, lastTimeStamp: Long) {
        val conversationList = mutableListOf<MCConversation>()

        if (snapshot.hasChildren()) {
            for (item in snapshot.children.reversed()) {
                conversationList.add(convertDataFirebase(item))
            }
        }

        binding.swipeRefresh.isRefreshing = false

        if (lastTimeStamp == 0L) {
            if (conversationList.isNullOrEmpty()) {
                viewModel.checkError(true, dataEmpty = true)
            } else {
                binding.recyclerView.setVisible()
                binding.layoutNoData.setGone()

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

    private fun getChangeConversation() {
        viewModel.getChangeConversation(
                { obj ->
                    adapter.changeConversation(obj)
                    binding.recyclerView.smoothScrollToPosition(0)
                }, { obj ->
            val key = obj.key.toString()

            if (adapter.getListData.firstOrNull()?.key == key) {
                adapter.getListData[0] = adapter.getListData[0].apply {
                    this.key = obj.key.toString()
                    enableAlert = obj.child("enable_alert").value.toString().toBoolean()
                    keyRoom = obj.key.toString()
                    unreadCount = obj.child("unread_count").value as Long? ?: 0L
                    time = obj.child("last_activity").child("time").value as Long?
                            ?: System.currentTimeMillis()
                    lastMessage = if (obj.child("last_activity").child("content").value != null) {
                        obj.child("last_activity").child("content").value.toString()
                    } else {
                        ""
                    }
                }
                binding.recyclerView.findViewHolderForAdapterPosition(0)?.let { holder ->
                    if (holder is ListConversationAdapter.ConversationHolder) {
                        holder.updateConversation(adapter.getListData[0])
                    }
                }
            }
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
        adapter.resetData(false)
        listData.clear()
        if (!isLogin) {
            binding.layoutSearch.beGone()
            binding.swipeRefresh.isEnabled = false
            binding.recyclerView.setGone()
            binding.layoutNoData.setVisible()
        } else {
            binding.layoutSearch.beVisible()
            binding.swipeRefresh.isEnabled = true
            getData()
        }
    }

    override fun onMessageClicked() {

    }

    private val getLastTime: Long?
        get() {
            var smallTime: Long? = null

            for (item in adapter.getListData) {
                if (smallTime == null) {
                    smallTime = item.time ?: 0
                } else if (smallTime > item.time ?: 0) {
                    smallTime = item.time ?: 0
                }
            }

            return smallTime
        }

    override fun onLoadMore() {
        getLastTime?.let {
            getConversation(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MCMessageEvent) {
        if (event.type == MCMessageEvent.Type.UPDATE_DATA) {
            getData()
        }
    }

    override fun onResume() {
        super.onResume()
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