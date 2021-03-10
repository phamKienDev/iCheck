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
                tvTitle.text = "Chọn quà hay, đổi liền tay"
                tvMessage.text = "Sử dụng điểm tích lũy để đổi những\nphần quà hấp dẫn bạn nhé!"
                btnTiepTuc.setBackgroundResource(R.drawable.bg_corner_47_outline_1_light_blue)

                btnTiepTuc.text = "Tiếp tục"

                btnQuayLai.setOnClickListener {
                    listener.onBackStep(1)
                }

                btnTiepTuc.setOnClickListener {
                    listener.onNextStep(3)
                }
            }
            else -> {
                imgDefault.setImageResource(R.drawable.ic_onboarding_step3)
                tvTitle.text = "Wow! Quà về rồi!"
                tvMessage.text = "Thật tuyệt vời, phần quà đổi điểm của\nbạn đã về tay nhanh như chớp đó!"
                btnTiepTuc.setBackgroundResource(R.drawable.bg_corner_47_light_blue)
                btnTiepTuc.text = "Tôi đã hiểu"
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