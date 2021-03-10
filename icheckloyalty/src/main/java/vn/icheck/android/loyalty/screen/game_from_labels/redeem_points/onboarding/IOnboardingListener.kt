package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding

interface IOnboardingListener {
    fun onNextStep(i: Int)
    fun onBackStep(i: Int)
    fun onClickRedeemPoint()
}