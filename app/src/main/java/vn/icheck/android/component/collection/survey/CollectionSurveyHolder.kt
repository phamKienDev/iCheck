package vn.icheck.android.component.collection.survey

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Response
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.ICReqDirectSurvey
import vn.icheck.android.network.models.ICSurvey
import vn.icheck.android.util.kotlin.ToastUtils

class CollectionSurveyHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createSurvey(parent.context)) {
    private lateinit var layoutContent: ViewGroup
    private lateinit var layoutHeader: ViewGroup
    private lateinit var layoutOption: ViewGroup
    private lateinit var layoutButton: ViewGroup

    private lateinit var survey: ICSurvey
    private lateinit var listener: ISurveyListener

    fun bind(obj: ICSurvey, listener: ISurveyListener) {
        this.listener = listener
        this.survey = obj

        /* Layout Content */
        ((itemView as ViewGroup).getChildAt(0) as ViewGroup).run {
            layoutContent = this

            if (survey.measuredHeight != -1) {
                if (survey.isExpand) {
                    val mLayoutParams = layoutParams
                    mLayoutParams.height = survey.measuredHeight
                    layoutParams = mLayoutParams
                } else {
                    val mLayoutParams = layoutParams
                    mLayoutParams.height = 0
                    layoutParams = mLayoutParams
                }
            }

            /* Layout Header */
            (getChildAt(0) as ViewGroup).run {
                layoutHeader = this

                (getChildAt(0) as AppCompatTextView).text = survey.title

                (getChildAt(2) as ProgressBar).run {
                    max = survey.questions.size
                    progress = (survey.totalAnswer + 1)
                }
            }

            (getChildAt(1) as ViewGroup).run {
                layoutOption = this
                layoutOption.removeAllViews()
                initQuestion()
            }

            (getChildAt(2) as ViewGroup).run {
                layoutButton = this
                checkButton(survey)
                setupListener(getChildAt(0) as AppCompatTextView, getChildAt(1) as AppCompatTextView)
            }
        }
    }

    private fun initQuestion() {
        val layoutAnswer = LinearLayout(itemView.context)
        layoutAnswer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutAnswer.orientation = LinearLayout.VERTICAL

        val txtTitle = LayoutInflater.from(itemView.context).inflate(R.layout.item_survey_answer_title, layoutAnswer, false) as AppCompatTextView
        txtTitle.text = survey.questions[survey.totalAnswer].title
        layoutAnswer.addView(txtTitle)

        for (i in 0 until survey.questions[survey.totalAnswer].options.size) {
            val txtAnswer = LayoutInflater.from(itemView.context).inflate(R.layout.item_survey_answer, layoutAnswer, false) as AppCompatCheckedTextView
            txtAnswer.text = survey.questions[survey.totalAnswer].options[i].title
            txtAnswer.isChecked = survey.questions[survey.totalAnswer].options[i].isChecked
            ViewCompat.setElevation(txtAnswer, if (txtAnswer.isChecked) {
                SizeHelper.size4.toFloat()
            } else {
                SizeHelper.size1.toFloat()
            })

            txtAnswer.setOnClickListener {
                val question = survey.questions[survey.totalAnswer]

                if (question.type == Constant.SELECT) {
                    unCheckOption()
                }

                question.options[i].isChecked = !question.options[i].isChecked
                txtAnswer.isChecked = question.options[i].isChecked
                ViewCompat.setElevation(txtAnswer, if (txtAnswer.isChecked) {
                    SizeHelper.size4.toFloat()
                } else {
                    SizeHelper.size1.toFloat()
                })
            }

            layoutAnswer.addView(txtAnswer)
        }

        layoutOption.addView(layoutAnswer)
    }

    private fun changeQuestion(isNext: Boolean) {
        initQuestion()

        if (isNext) {
            playAnimation(layoutOption.getChildAt(0), R.anim.right_to_left_exit, true)
            playAnimation(layoutOption.getChildAt(1), R.anim.right_to_left_enter, false)
        } else {
            playAnimation(layoutOption.getChildAt(0), R.anim.left_to_right_pop_exit, true)
            playAnimation(layoutOption.getChildAt(1), R.anim.left_to_right_pop_enter, false)
        }
    }

    private fun playAnimation(view: View, resource: Int, isRemove: Boolean) {
        val animation = AnimationUtils.loadAnimation(itemView.context, resource)
        animation.duration = 400

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                if (isRemove) {
                    Handler(Looper.getMainLooper()).post {
                        layoutOption.removeViewAt(0)
                    }
                }

                (layoutButton.getChildAt(1) as AppCompatTextView).isClickable = true
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        view.startAnimation(animation)
    }

    private fun unCheckOption() {
        val layoutAnswer = layoutOption.getChildAt(0) as LinearLayout

        for (i in 0 until layoutAnswer.childCount) {
            if (layoutAnswer.getChildAt(i) is AppCompatCheckedTextView) {
                (layoutAnswer.getChildAt(i) as AppCompatCheckedTextView).isChecked = false
                ViewCompat.setElevation((layoutAnswer.getChildAt(i) as AppCompatCheckedTextView), SizeHelper.size1.toFloat())
            }
        }


        for (obj in survey.questions[survey.totalAnswer].options) {
            obj.isChecked = false
        }
    }

    private fun setupListener(btnLeft: AppCompatTextView, btnRight: AppCompatTextView) {
        btnLeft.setOnClickListener {
            if (layoutOption.getChildAt(0).animation != null) {
                return@setOnClickListener
            }

            if (survey.totalAnswer == 0) {
                confirmHideSurvey(survey.id)
            } else {
                survey.totalAnswer--
                (layoutHeader.getChildAt(2) as ProgressBar).progress = (survey.totalAnswer + 1)
                changeQuestion(false)
                checkButton(survey)
            }
        }

        btnRight.setOnClickListener {
            if (layoutOption.getChildAt(0).animation != null) {
                return@setOnClickListener
            }

            if (survey.totalAnswer < survey.questions.size - 1) {
                if (isDoneQuestion(survey)) {
                    survey.totalAnswer++
                    (layoutHeader.getChildAt(2) as ProgressBar).progress = (survey.totalAnswer + 1)

                    changeQuestion(true)
                    checkButton(survey)
                } else {
                    ToastUtils.showShortError(itemView.context, R.string.vui_long_chon_cau_tra_loi)
                }
            } else {
                survey.respond_url?.let { respondUrl ->
                    val isDoneQuestion = isDoneQuestion(survey)

                    if (isDoneQuestion) {
                        val respDirectSurvey = mutableListOf<ICReqDirectSurvey>()

                        for (q in survey.questions.indices) {
                            val resp = ICReqDirectSurvey()

                            resp.question_id = q.toLong()

                            for (i in 0 until survey.questions[q].options.size) {
                                if (survey.questions[q].options[i].isChecked) {
                                    resp.option_ids.add(i.toLong())
                                }
                            }

                            respDirectSurvey.add(resp)
                        }

                        val url = APIConstants.defaultHost + if (respondUrl.subSequence(0, 1) == "/") {
                            respondUrl.substring(1, respondUrl.length)
                        } else {
                            respondUrl
                        }

                        answerSurvey(survey.id, url, respDirectSurvey)
                    } else {
                        ToastUtils.showShortError(itemView.context, R.string.vui_long_chon_cau_tra_loi)
                    }
                }
            }
        }
    }

    private fun isDoneQuestion(survey: ICSurvey): Boolean {
        for (option in survey.questions[survey.totalAnswer].options) {
            if (option.isChecked) {
                return true
            }
        }

        return false
    }

    private fun checkButton(obj: ICSurvey) {
        layoutHeader
        (layoutHeader.getChildAt(1) as AppCompatTextView).setText(R.string.cau_hoi_s, "${(obj.totalAnswer + 1)}/${obj.questions.size}")

        if (obj.totalAnswer > 0) {
            (layoutButton.getChildAt(0) as AppCompatTextView).setText(R.string.cau_s, obj.totalAnswer.toString())

            (layoutButton.getChildAt(1) as AppCompatTextView).apply {
                text = if (obj.totalAnswer + 1 == obj.questions.size) {
                    context.getString(R.string.gui)
                } else {
                    context.getString(R.string.tiep_theo_title)
                }
            }
        } else {
            (layoutButton.getChildAt(0) as AppCompatTextView).setText(R.string.bo_qua)
            (layoutButton.getChildAt(1) as AppCompatTextView).setText(R.string.tiep_theo_title)
        }
    }

    private fun getStringText(res: Int): String {
        return itemView.context.getString(res)
    }

    private fun getStringValue(res: Int, value: String): String {
        return itemView.context.getString(res, value)
    }


    private var adsInteraction: AdsRepository? = null

    private fun confirmHideSurvey(adsID: Long) {
        ICheckApplication.currentActivity()?.let { activity ->
            DialogHelper.showConfirm(activity, R.string.bo_qua_khao_sat, R.string.ban_chac_chan_bo_qua_khao_sat_nay_chu, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    hideSurvey(activity, adsID)
                }
            })
        }
    }

    private fun hideSurvey(activity: Activity, adsID: Long) {
        if (NetworkHelper.isNotConnected(activity)) {
            ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        DialogHelper.showLoading(activity)

        if (adsInteraction == null) {
            adsInteraction = AdsRepository()
        }

        adsInteraction!!.hideAds(adsID, object : ICApiListener<Response<Void>> {
            override fun onSuccess(obj: Response<Void>) {
                DialogHelper.closeLoading(activity)
                listener.onHideSurvey(adsID)
            }

            override fun onError(error: ICBaseResponse?) {
                DialogHelper.closeLoading(activity)
                val message = error?.message ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                ToastUtils.showLongError(activity, message)
            }
        })
    }

    private fun answerSurvey(adsID: Long, url: String, listAnswer: MutableList<ICReqDirectSurvey>) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(activity)) {
                ToastUtils.showLongError(activity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            if (adsInteraction == null) {
                adsInteraction = AdsRepository()
            }

            adsInteraction!!.answerAds(adsID, url, listAnswer, object : ICApiListener<ICNone> {
                override fun onSuccess(obj: ICNone) {
                    DialogHelper.closeLoading(activity)
                    listener.onAnsweredSurvey(adsID)

                    Handler().postDelayed({
                        listener.onHideSurvey(adsID)
                    }, 3000)
                }

                override fun onError(error: ICBaseResponse?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message ?: activity.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }
}