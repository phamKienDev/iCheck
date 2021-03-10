package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.fragment

import kotlinx.android.synthetic.main.fragment_step_one.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.IOnboardingListener

class StepOneFragment(val listener: IOnboardingListener) : BaseFragmentGame() {
    override val getLayoutID: Int
        get() = R.layout.fragment_step_one

    override fun onInitView() {
        if (SharedLoyaltyHelper(requireContext()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS)) {
            imgDefault.setImageResource(R.drawable.ic_onboarding_nhap)
            tvTitle.text = "Nhập mã may mắn, tích điểm đầy kho"
            tvMessage.text = "Bạn ơi! Nhập mã in trên sản phẩm để\nnhận điểm tích lũy đổi quà nhé!"
        } else {
            imgDefault.setImageResource(R.drawable.ic_onboarding_scan)
            tvTitle.text = "Quét QR code, tích điểm đầy kho"
            tvMessage.text = "Bạn ơi! Quét QR code trên sản phẩm để\nnhận điểm tích lũy đổi quà nhé!"
        }

        btnTiepTuc.setOnClickListener {
            listener.onNextStep(2)
        }

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}