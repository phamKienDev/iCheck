package vn.icheck.android.chat.icheckchat.base.recyclerview.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@SuppressLint("WrongConstant")
class ViewPagerAdapterChat(fragmentManager: FragmentManager, val listData: MutableList<Fragment> = mutableListOf()) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return listData[position]
    }

    override fun getCount(): Int = listData.size
}