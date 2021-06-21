package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.fragment

import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_step_two.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.IOnboardingListener

class StepTwoThreeFragment(private val step: Int, private val listener: IOnboardingListener) : BaseFragmentGame() {
    override val getLayoutID: Int
        get() = R.layout.fragment_step_two

    override fun onInitView() {
        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        when (step) {
            2 -> {
                imgDefault.setImageResource(R.drawable.ic_onboarding_step2)
                tvTitle.setText(R.string.chon_qua_hay_doi_lien_tay)
                tvMessage.setText(R.string.su_dung_diem_tich_luy_de_doi_nhung_phan_qua_hap_dan_ban_nhe)
                btnTiepTuc.setBackgroundResource(R.drawable.bg_corner_47_outline_1_light_blue)

                btnTiepTuc.setText(R.string.tiep_tuc)

                btnQuayLai.setOnClickListener {
                    listener.onBackStep(1)
                }

                btnTiepTuc.setOnClickListener {
                    listener.onNextStep(3)
                }
            }
            else -> {
                imgDefault.setImageResource(R.drawable.ic_onboarding_step3)
                tvTitle.setText(R.string.wow_qua_ve_roi)
                tvMessage.setText(R.string.that_tuyet_voi_phan_qua_doi_diem_cua_ban_da_ve_tay_nhanh_nhu_chop_do)
                btnTiepTuc.setBackgroundResource(R.drawable.bg_corner_47_light_blue)
                btnTiepTuc.setText(R.string.toi_da_hieu)
                btnTiepTuc.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                btnQuayLai.setOnClickListener {
                    listener.onBackStep(2)
                }

                btnTiepTuc.setOnClickListener {
                    listener.onClickRedeemPoint()
                }
            }
        }
    }
}