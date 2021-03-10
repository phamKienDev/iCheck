package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

const val TOTAL_LANDING_PAGE = 3

class OnBoardRunningAdapter(fm: FragmentManager, private val navigateOnBoardListener: NavigateOnBoardListener, private val landingPage: String?, private val titleButton: String?, private val schema: String?, private val idCampaign: String?) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> {
                OnBoardSecondFragment(navigateOnBoardListener)
            }
            2 -> {
                OnBoardThirdFragment(navigateOnBoardListener)
            }
            else -> {
                if (!landingPage.isNullOrEmpty()) LandingPageFragment(landingPage, titleButton, schema, idCampaign) else OnBoardFirstFragment(navigateOnBoardListener)
            }
        }
    }

    override fun getCount(): Int {
        return if (landingPage.isNullOrEmpty()) {
            TOTAL_LANDING_PAGE
        } else {
            1
        }
    }
}