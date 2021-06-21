package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.fragment

import kotlinx.android.synthetic.main.fragment_step_one.*
import vn.icheck.android.ichecklibs.util.rText
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
            tvTitle rText R.string.nhap_ma_may_man_tich_diem_day_kho
            tvMessage rText R.string.ban_oi_nhap_ma_in_tren_san_pham_de_nhan_diem_tich_luy_doi_qua_nhe
        } else {
            imgDefault.setImageResource(R.drawable.ic_onboarding_scan)
            tvTitle rText R.string.quet_qr_code_tich_diem_day_kho
            tvMessage rText R.string.ban_oi_quet_qr_code_tren_san_pham_de_nhan_diem_tich_luy_doi_qua_nhe
        }

        btnTiepTuc.setOnClickListener {
            listener.onNextStep(2)
        }

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}