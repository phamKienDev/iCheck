package vn.icheck.android.chat.icheckchat.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseFragmentChat
import vn.icheck.android.chat.icheckchat.base.recyclerview.adapter.ViewPagerAdapterChat
import vn.icheck.android.chat.icheckchat.base.view.setVisible
import vn.icheck.android.chat.icheckchat.databinding.FragmentChatSocialBinding
import vn.icheck.android.chat.icheckchat.helper.rText
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent
import vn.icheck.android.chat.icheckchat.screen.contact.ContactFragment
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.ichecklibs.Constant

class ChatSocialFragment : BaseFragmentChat<FragmentChatSocialBinding>() {

    private var callback: ListConversationFragment.Companion.ICountMessageListener? = null
    private var isUserLogged: Boolean = false

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChatSocialBinding {
        return FragmentChatSocialBinding.inflate(inflater, container, false)
    }

    override fun onInitView() {
        initToolbar()
        setupViewPager()
    }

    private fun initToolbar() {

        binding.layoutContainer.setPadding(0, getStatusBarHeight, 0, 0)

        binding.toolbar.imgBack.setImageResource(R.drawable.ic_left_menu_blue_24dp_chat)

        binding.toolbar.imgBack.setOnClickListener {
            callback?.onClickLeftMenu()
        }

        binding.toolbar.txtTitle rText R.string.tin_nhan

        binding.toolbar.imgAction.setVisible()

        binding.toolbar.imgAction.setImageResource(0)

        binding.toolbar.imgAction.setOnClickListener {
        }

        binding.tvMessage.setOnClickListener {
            selectTab(1)
        }

        binding.tvContact.setOnClickListener {
            selectTab(2)
        }
    }

    fun setDataFromHome(callback: ListConversationFragment.Companion.ICountMessageListener, isUserLogged: Boolean) {
        this.callback = callback
        this.isUserLogged = isUserLogged
    }

    private fun setupViewPager() {
        val listPage = mutableListOf<Fragment>()

        listPage.add(ListConversationFragment().apply {
            setListener(callback)
        })
        listPage.add(ContactFragment.newInstance(isUserLogged))

        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = ViewPagerAdapterChat(childFragmentManager, listPage)
        selectTab(1)
    }

    private fun selectTab(position: Int) {
        when (position) {
            1 -> {
                isChecked(binding.tvMessage)
                binding.viewPager.setCurrentItem(0, false)
            }
            2 -> {
                isChecked(binding.tvContact)
                binding.viewPager.setCurrentItem(1, false)
            }
        }
    }


    private fun isChecked(view: AppCompatCheckedTextView): Boolean {
        return if (view.isChecked) {
            true
        } else {
            unCheckAll()
            view.isChecked = true
            false
        }
    }

    private fun unCheckAll() {
        binding.tvMessage.isChecked = false
        binding.tvContact.isChecked = false
    }

    fun checkLoginOrLogOut(isLogin: Boolean) {
        (binding.viewPager.adapter as ViewPagerAdapterChat).apply {
            for (item in listData) {
                when (item) {
                    is ListConversationFragment -> {
                        item.checkLoginOrLogOut(isLogin)
                    }
                    is ContactFragment -> {
                        item.checkLoginOrLogOut(isLogin)
                    }
                }
            }
        }
    }
}