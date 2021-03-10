package vn.icheck.android.screen.user.surveydetail.answer.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICQuestions

interface ISurveyDetailView : BaseActivityView {

    fun onGetDataError()
    fun onNoInternet()
    fun onGetDetailError()

    fun showLoading()
    fun closeLoading()

    fun onGetDetailSuccess(obj: List<ICQuestions>)
    fun onHideDirectSuccess()
    fun onAnsweredSuccess()
}