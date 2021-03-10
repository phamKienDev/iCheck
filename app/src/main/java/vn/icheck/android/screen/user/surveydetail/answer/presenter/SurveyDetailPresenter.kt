package vn.icheck.android.screen.user.surveydetail.answer.presenter

import android.content.Intent
import retrofit2.Response
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.ICReqDirectSurvey
import vn.icheck.android.screen.user.surveydetail.answer.view.ISurveyDetailView

/**
 * Created by VuLCL on 10/21/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SurveyDetailPresenter(val view: ISurveyDetailView) : BaseActivityPresenter(view) {
    private var surveyID: Long? = null
    private lateinit var ads: ICAds

    fun getData(intent: Intent?) {
        surveyID = try {
            intent?.getLongExtra(Constant.DATA_1, -1)
        } catch (e: Exception) {
            null
        }

        if (surveyID == null || surveyID == -1L) {
            view.onGetDataError()
        } else {
            getSurveyDetail()
        }
    }

    fun getSurveyDetail() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onNoInternet()
            return
        }

        view.showLoading()

        AdsRepository().getAdsDetail(surveyID!!, object : ICApiListener<ICListResponse<ICAds>> {
            override fun onSuccess(obj: ICListResponse<ICAds>) {
                view.closeLoading()

                if (obj.rows.isNotEmpty()) {
                    ads = obj.rows[0]
                    val question = ads.survey?.questions

                    if (question != null) {
                        view.onGetDetailSuccess(question)
                        return
                    }
                }

                view.onGetDetailError()
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()
                view.onGetDataError()
            }
        })
    }

    fun hideDirectSurvey() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.showLoading()

        AdsRepository().hideAds(ads.id, object : ICApiListener<Response<Void>> {
            override fun onSuccess(obj: Response<Void>) {
                view.closeLoading()
                view.onHideDirectSuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()
                showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        })
    }

    fun answerDirectSurvey(list: MutableList<ICReqDirectSurvey>?) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (list == null) {
            showError(getString(R.string.vui_long_tra_loi_het_cac_cau_hoi))
            return
        }

        val url = APIConstants.defaultHost + if (ads.respond_url.toString().subSequence(0, 1) == "/") {
            ads.respond_url.toString().substring(1, ads.respond_url.toString().length)
        } else {
            ads.respond_url
        }

        view.showLoading()

        AdsRepository().answerAds(ads.id, url, list, object : ICApiListener<ICNone> {
            override fun onSuccess(obj: ICNone) {
                view.closeLoading()
                view.onAnsweredSuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.closeLoading()
                showError(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        })
    }
}