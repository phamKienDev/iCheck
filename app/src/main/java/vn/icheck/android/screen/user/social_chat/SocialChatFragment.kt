package vn.icheck.android.screen.user.social_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import vn.icheck.android.R
import vn.icheck.android.base.fragment.CoroutineFragment
import vn.icheck.android.databinding.FragmentSocialChatBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.util.ick.getStatusBarHeight

class SocialChatFragment:CoroutineFragment() {
    private var _binding:FragmentSocialChatBinding? = null
    private val binding get() = _binding!!
    lateinit var screenSlidePagerAdapter:ScreenSlidePagerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSocialChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setPadding(0, requireContext().getStatusBarHeight(), 0,0)
        binding.imgMenu.setImageResource(ViewHelper.setImageColorPrimary(R.drawable.ic_leftmenu_24_px,requireContext()))
        binding.divSelector.background=ViewHelper.bgPrimaryCornersTop4(requireContext())
        binding.tvMessage.setOnClickListener {
            binding.containerFramelayout.setCurrentItem(0, true)
        }
        binding.tvPhonebook.setOnClickListener {
            binding.containerFramelayout.setCurrentItem(1, true)
        }
        binding.imgMenu.setOnClickListener {
            if (requireActivity() is HomeActivity) {
                (requireActivity() as HomeActivity).openSlideMenu()
            }
        }
        screenSlidePagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        binding.containerFramelayout.adapter = screenSlidePagerAdapter
        binding.containerFramelayout.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position == 0) {
                    if (binding.root.currentState != binding.root.startState) {
                        binding.root.transitionToStart()
                    }
                } else {
                    if (binding.root.currentState != binding.root.endState) {
                        binding.root.transitionToEnd()
                    }
                }

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = if(position == 0) SocialMessagesFragment() else SocialPhoneBookFragment()


    }
}