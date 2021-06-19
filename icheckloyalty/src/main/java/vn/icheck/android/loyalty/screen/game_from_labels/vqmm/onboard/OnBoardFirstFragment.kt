package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_on_board_first.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.sdk.CampaignType

class OnBoardFirstFragment(val navigateOnBoardListener: NavigateOnBoardListener) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_on_board_first, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_continue.setOnClickListener {
            navigateOnBoardListener.onNextStep()
        }
        btn_back.setOnClickListener {
            navigateOnBoardListener.onBack()
        }
        if (SharedLoyaltyHelper(requireContext()).getBoolean(CampaignType.ACCUMULATE_LONG_TERM_POINT_QR_MAR)){
            navigateOnBoardListener.onNextStep()
        }else{
            if (SharedLoyaltyHelper(requireContext()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM)) {
                img_center.setImageResource(R.drawable.ic_onboarding_nhap)
                textView27.text = "Nhập mã may mắn, nhận cơ hội tốt"
                textView31.text = "Bạn ơi! Nhập mã may mắn in trên sản phẩm để tham gia minigame có thưởng nhé!"
            } else {
                img_center.setImageResource(R.drawable.ic_onboarding_scan)
                textView27.text = "Quét QR code, nhận cơ hội tốt"
                textView31.text = "Bạn ơi! Quét QR code trên sản phẩm để tham gia minigame có thưởng nhé!"
            }
        }
    }
}