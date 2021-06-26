package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class OnBoardRunningAdapter(fm: FragmentManager, private val landingPage: String?) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val listFragment = mutableListOf<Fragment>()

    fun setList(list: MutableList<Fragment>) {
        listFragment.clear()
        listFragment.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount() = listFragment.size
}