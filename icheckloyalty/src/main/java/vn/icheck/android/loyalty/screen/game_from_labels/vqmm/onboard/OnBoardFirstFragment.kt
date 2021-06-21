package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_on_board_first.*
import vn.icheck.android.ichecklibs.util.rText
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
                textView27.text = rText(R.string.nhap_ma_may_man_nhan_co_hoi_tot)
                textView31.text = rText(R.string.ban_oi_nhap_ma_may_man_in_tren_san_pham_de_tham_gia_minigame_co_thuong_nhe)
            } else {
                img_center.setImageResource(R.drawable.ic_onboarding_scan)
                textView27.text = rText(R.string.quet_qr_code_nhan_co_hoi_tot)
                textView31.text = rText(R.string.ban_oi_quet_qr_code_tren_san_pham_de_tham_gia_minigame_co_thuong_nhe)
            }
        }
    }
}