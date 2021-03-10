package vn.icheck.android.loyalty.base.viewpager

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@SuppressLint("WrongConstant")
class ViewPagerAdapter(fragmentManager: FragmentManager, private val listData: MutableList<ICKFragment> = mutableListOf()) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val getItemCount: Int
        get() {
            return listData.size
        }

    fun setData(list: MutableList<ICKFragment>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun getTitle(position: Int): String {
        return if (position < listData.size) {
            return listData[position].title.toString()
        } else {
            ""
        }
    }

    override fun getItem(position: Int): Fragment {
        return listData[position].fragment
    }

    override fun getCount(): Int = listData.size

    override fun getPageTitle(position: Int): CharSequence? {
        return if (listData[position].title != null) listData[position].title else super.getPageTitle(position)
    }
}