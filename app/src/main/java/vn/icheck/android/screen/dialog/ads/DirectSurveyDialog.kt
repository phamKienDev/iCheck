package vn.icheck.android.screen.dialog.ads

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.dialog_direct_survey.*
import retrofit2.Response
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.ICReqDirectSurvey
import vn.icheck.android.network.models.ICSurvey
import vn.icheck.android.util.kotlin.ToastUtils

class DirectSurveyDialog(context: Context, private val ads: ICAds) : BaseDialog(context, R.style.DialogTheme) {
    private val adsInteraction = AdsRepository()

    override val getLayoutID: Int
        get() = R.layout.dialog_direct_survey

    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        ads.survey?.let { survey ->
            tvName.text = survey.title
            progressBar.max = survey.questions.size
            progressBar.progress = (survey.totalAnswer + 1)

            layoutOption.removeAllViews()
            initQuestion()
            checkButton(survey)
        }

        setupView()
        setupListener()
    }

    private fun setupView() {
        linearLayout.background=ViewHelper.bgSecondaryCornersTop10(context)
        btnRight.background=ViewHelper.btnSecondaryCorners26(context)
    }

    private fun initQuestion() {
        ads.survey?.let { survey ->
            val layoutAnswer = LinearLayout(context)
            layoutAnswer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutAnswer.orientation = LinearLayout.VERTICAL

            val txtTitle = LayoutInflater.from(context).inflate(R.layout.item_survey_answer_title, layoutAnswer, false) as AppCompatTextView
            txtTitle.text = survey.questions[survey.totalAnswer].title
            layoutAnswer.addView(txtTitle)

            for (i in 0 until survey.questions[survey.totalAnswer].options.size) {
                val txtAnswer = LayoutInflater.from(context).inflate(R.layout.item_survey_answer, layoutAnswer, false) as AppCompatCheckedTextView
                txtAnswer.text = survey.questions[survey.totalAnswer].options[i].title
                txtAnswer.isChecked = survey.questions[survey.totalAnswer].options[i].isChecked
                ViewCompat.setElevation(txtAnswer, if (txtAnswer.isChecked) {
                    SizeHelper.size4.toFloat()
                } else {
                    SizeHelper.size1.toFloat()
                })

                txtAnswer.setOnClickListener {
                    ads.survey?.let { survey ->
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
                }

                layoutAnswer.addView(txtAnswer)
            }

            layoutOption.addView(layoutAnswer)
        }
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
        val animation = AnimationUtils.loadAnimation(context, resource)
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

                btnRight.isClickable = true
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

        ads.survey?.let {
            for (obj in it.questions[it.totalAnswer].options) {
                obj.isChecked = false
            }
        }
    }

    private fun setupListener() {
        btnLeft.setOnClickListener {
            if (layoutOption.getChildAt(0).animation != null) {
                return@setOnClickListener
            }

            ads.survey?.let { survey ->
                if (survey.totalAnswer == 0) {
                    ads.id.let {
                        onHideDirectionSurvey(it)
                    }
                } else {
                    survey.totalAnswer--
                    progressBar.progress = (survey.totalAnswer + 1)
                    changeQuestion(false)
                    checkButton(survey)
                }
            }
        }

        btnRight.setOnClickListener {
            if (layoutOption.getChildAt(0).animation != null) {
                return@setOnClickListener
            }

            ads.survey?.let { survey ->
                if (survey.totalAnswer < survey.questions.size - 1) {
                    if (isDoneQuestion(survey)) {
                        survey.totalAnswer++
                        progressBar.progress = (survey.totalAnswer + 1)

                        changeQuestion(true)
                        checkButton(survey)
                    } else {
                        ToastUtils.showShortError(context, R.string.vui_long_chon_cau_tra_loi)
                    }
                } else {
                    ads.respond_url?.let { respondUrl ->
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

                            answerDirectionSurvey(url, respDirectSurvey)
                        } else {
                            ToastUtils.showShortError(context, R.string.vui_long_chon_cau_tra_loi)
                        }
                    }
                }
            }
        }

        tvClose.setOnClickListener {
            dismiss()
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
        txtQuestionTitle.text = getStringValue(R.string.cau_hoi_xxx, "${(obj.totalAnswer + 1)}/${obj.questions.size}")

        if (obj.totalAnswer > 0) {
            btnLeft.text = getStringValue(R.string.cau_xxx, obj.totalAnswer.toString())

            btnRight.text = if (obj.totalAnswer + 1 == obj.questions.size) {
                getStringText(R.string.gui)
            } else {
                getStringText(R.string.tiep_theo_title)
            }
        } else {
            btnLeft.text = getStringText(R.string.bo_qua)
            btnRight.text = getStringText(R.string.tiep_theo_title)
        }
    }

    private fun getStringText(res: Int): String {
        return context.getString(res)
    }

    private fun getStringValue(res: Int, value: String): String {
        return context.getString(res, value)
    }

    private fun showShortError(message: String) {
        ToastUtils.showShortError(context, message)
    }

    private fun onHideDirectionSurvey(surveyID: Long) {
        DialogHelper.showConfirm(context, R.string.bo_qua_khao_sat, R.string.ban_chac_chan_bo_qua_khao_sat_nay_chu, object : ConfirmDialogListener {
            override fun onDisagree() {

            }

            override fun onAgree() {
                hideDirectSurvey(surveyID)
            }
        })
    }

    fun hideDirectSurvey(surveyID: Long) {
        if (NetworkHelper.isNotConnected(context)) {
            showShortError(context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        DialogHelper.showLoading(this)

        adsInteraction.hideAds(surveyID, object : ICApiListener<Response<Void>> {
            override fun onSuccess(obj: Response<Void>) {
                DialogHelper.closeLoading(this@DirectSurveyDialog)
                Handler().postDelayed({
                    dismiss()
                }, 300)
            }

            override fun onError(error: ICBaseResponse?) {
                DialogHelper.closeLoading(this@DirectSurveyDialog)
                showShortError(context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun answerDirectionSurvey(url: String, list: MutableList<ICReqDirectSurvey>) {
        if (NetworkHelper.isNotConnected(context)) {
            showShortError(context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        DialogHelper.showLoading(this)

        adsInteraction.answerAds(ads.id, url, list, object : ICApiListener<ICNone> {
            override fun onSuccess(obj: ICNone) {
                DialogHelper.closeLoading(this@DirectSurveyDialog)
                layoutTop.visibility = View.GONE
                layoutBottom.visibility = View.VISIBLE

                Handler().postDelayed({
                    try {
                        dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 3000)
            }

            override fun onError(error: ICBaseResponse?) {
                DialogHelper.closeLoading(this@DirectSurveyDialog)
                showShortError(context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            }
        })
    }
}