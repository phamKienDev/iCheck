package vn.icheck.android.screen.user.bannersurvey.presenter

import android.content.Intent
import android.view.View
import retrofit2.Response
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.ICReqDirectSurvey
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.bannersurvey.view.IBannerSurveyView

/**
 * Created by VuLCL on 9/26/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class BannerSurveyPresenter(val view: IBannerSurveyView) : BaseActivityPresenter(view) {
    private val interaction = AdsRepository()

    private lateinit var ads: ICAds

    fun getData(data: Intent?) {
        val json = try {
            data?.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        try {
            ads = JsonHelper.parseJson(json, ICAds::class.java)!!

            if (ads.survey != null) {
                view.onGetDataSuccess()
            } else {
                view.onAnsweredSurveySuccess()
            }
        } catch (e: Exception) {
            view.onGetDataError()
        }
    }

    val getAds: ICAds
        get() {
            return ads
        }

    val isVisibleLeftButton: Int
        get() {
            return if (ads.survey!!.totalAnswer == 0 || (ads.survey!!.totalAnswer + 1) == ads.survey!!.questions.size) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

    val getLeftButtonText: String
        get() {
            return if (ads.survey!!.totalAnswer > 0) {
                view.mContext.getString(R.string.cau_s, ads.survey?.totalAnswer.toString())
            } else {
                view.mContext.getString(R.string.bo_qua)
            }
        }

    val getRightButtonText: String
        get() {
            return if (ads.survey!!.totalAnswer > 0) {
                if (ads.survey!!.totalAnswer + 1 == ads.survey!!.questions.size) {
                    view.mContext.getString(R.string.gui)
                } else {
                    view.mContext.getString(R.string.tiep_theo_title)
                }
            } else {
                view.mContext.getString(R.string.tiep_theo_title)
            }
        }

    val isDoneQuestion: Boolean
        get() {
            ads.survey?.let { survey ->
                for (option in survey.questions[survey.totalAnswer].options) {
                    if (option.isChecked) {
                        return true
                    }
                }
            }

            return false
        }

    fun answerQuestion() {
        val respDirectSurvey = mutableListOf<ICReqDirectSurvey>()

        for (q in 0 until ads.survey!!.questions.size) {
            val resp = ICReqDirectSurvey()

            resp.question_id = q.toLong()

            for (i in 0 until ads.survey!!.questions[q].options.size) {
                if (ads.survey!!.questions[q].options[i].isChecked) {
                    resp.option_ids.add(i.toLong())
                }
            }

            respDirectSurvey.add(resp)
        }

        val url = APIConstants.defaultHost + if (ads.respond_url.toString().subSequence(0, 1) == "/") {
            ads.respond_url.toString().substring(1, ads.respond_url.toString().length)
        } else {
            ads.respond_url
        }

        answerDirectionSurvey(url, respDirectSurvey)
    }

    fun hideDirectSurvey() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.showLoading()

        interaction.hideAds(ads.id, object : ICApiListener<Response<Void>> {
            override fun onSuccess(obj: Response<Void>) {
                view.closeLoading()
                view.onHideSurvey()
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    private fun answerDirectionSurvey(url: String, list: MutableList<ICReqDirectSurvey>) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.showLoading()

        interaction.answerAds(ads.id, url, list, object : ICApiListener<ICNone> {
            override fun onSuccess(obj: ICNone) {
                view.closeLoading()
                ads.survey?.measuredHeight = -1
                ads.survey?.questions?.clear()
                view.onAnsweredSurveySuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }
}