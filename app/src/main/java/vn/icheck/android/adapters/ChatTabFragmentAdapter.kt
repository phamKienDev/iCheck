package vn.icheck.android.adapters

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import vn.icheck.android.R
import vn.icheck.android.fragments.ContactsFragment
import vn.icheck.android.fragments.message.NewMessagesFragment

class ChatTabFragmentAdapter(fragmentManager: FragmentManager, val resources: Resources) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> ContactsFragment()
            else ->
//                MessagesFragment()
                NewMessagesFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            1 -> resources.getString(R.string.danh_ba)
            else -> resources.getString(R.string.tin_nhan)
        }
    }

    override fun getCount() = 2
}