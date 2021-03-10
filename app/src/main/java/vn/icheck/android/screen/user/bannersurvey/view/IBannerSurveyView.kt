package vn.icheck.android.screen.user.bannersurvey.view

import vn.icheck.android.base.activity.BaseActivityView

interface IBannerSurveyView : BaseActivityView {

    fun onGetDataError()
    fun onGetDataSuccess()

    fun showLoading()
    fun closeLoading()

    fun onHideSurvey()
    fun onAnsweredSurveySuccess()
}