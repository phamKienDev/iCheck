package vn.icheck.android.screen.user.social_chat

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.FragmentSocialMessagesBinding
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.shipping.ship.ui.main.SuccessConfirmShipDialog
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class SocialMessagesFragment : CoroutineFragment() {
    private var _binding: FragmentSocialMessagesBinding? = null
    private val binding get() = _binding!!
    var init = false
    private val conversationAdapter = ConversationAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSocialMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        binding.rcvMessages.adapter = conversationAdapter
        binding.edtSearch.addTextChangedListener {
            delayAction({
                if (!it?.trim().isNullOrEmpty()) {
                    conversationAdapter.data = RelationshipManager.searchConversation(it?.trim().toString())
                    conversationAdapter.notifyDataSetChanged()
                } else {
                    conversationAdapter.data = AppDatabase.getDatabase().chatConversationDao().getAllConversation()
                    conversationAdapter.notifyDataSetChanged()
                }
            })
        }
        binding.root.setOnRefreshListener {
            getData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.ON_LOG_OUT -> {
                _binding?.tvNoMessage?.beVisible()
                _binding?.edtSearch?.beGone()
                _binding?.rcvMessages?.beGone()
            }
            ICMessageEvent.Type.UPDATE_CONVERSATION -> {
                getData()
            }
        }
    }

    private fun getData() {
        if (AppDatabase.getDatabase().chatConversationDao().getAllConversation().isNotEmpty()) {
            _binding?.tvNoMessage?.beGone()
            _binding?.edtSearch?.beVisible()
            _binding?.rcvMessages?.beVisible()
            Handler().post {
                conversationAdapter.data = AppDatabase.getDatabase().chatConversationDao().getAllConversation()
                conversationAdapter.notifyDataSetChanged()
            }

        } else {
            _binding?.tvNoMessage?.beVisible()
            _binding?.edtSearch?.beGone()
            _binding?.rcvMessages?.beGone()
        }
        binding.root.isRefreshing = false
    }

//    override fun onResume() {
//        super.onResume()
//        getData()
//    }

}