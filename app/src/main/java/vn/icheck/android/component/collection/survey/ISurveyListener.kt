package vn.icheck.android.component.collection.survey

import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICReqDirectSurvey

interface ISurveyListener {

    fun onHideSurvey(adsID: Long)
    fun onAnsweredSurvey(adsID: Long)
}