package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_onboard_second.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.setInvisible
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.sdk.CampaignType

class OnBoardSecondFragment(val navigateOnBoardListener: NavigateOnBoardListener) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboard_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (SharedLoyaltyHelper(requireContext()).getBoolean(CampaignType.ACCUMULATE_LONG_TERM_POINT_QR_MAR)){
            btn_back.setOnClickListener {
                requireActivity().finish()
            }

            btn_prev.setOnClickListener {
                requireActivity().finish()
            }
        }else{
            btn_prev.setOnClickListener {
                navigateOnBoardListener.onPrev()
            }

            btn_back.setOnClickListener {
                navigateOnBoardListener.onBack()
            }
        }

        btn_continue.setOnClickListener {
            navigateOnBoardListener.onNextStep()
        }
    }
}