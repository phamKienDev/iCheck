package vn.icheck.android.screen.user.bannersurvey

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_banner_survey.*
import kotlinx.android.synthetic.main.toolbar_black.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.bannersurvey.presenter.BannerSurveyPresenter
import vn.icheck.android.screen.user.bannersurvey.view.IBannerSurveyView
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Created by VuLCL on 9/26/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class BannerSurveyActivity : BaseActivityMVVM(), IBannerSurveyView {
    private var isSuccess = false

    val presenter = BannerSurveyPresenter(this@BannerSurveyActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_survey)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        presenter.getData(intent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.khao_sat)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initQuestion() {
        presenter.getAds.survey?.let { survey ->
            val layoutAnswer = LinearLayout(this)
            layoutAnswer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutAnswer.orientation = LinearLayout.VERTICAL

            val txtTitle = LayoutInflater.from(this).inflate(R.layout.item_survey_answer_title, layoutAnswer, false) as AppCompatTextView
            txtTitle.text = survey.questions[survey.totalAnswer].title
            layoutAnswer.addView(txtTitle)

            for (i in 0 until survey.questions[survey.totalAnswer].options.size) {
                val txtAnswer = LayoutInflater.from(this).inflate(R.layout.item_survey_answer, layoutAnswer, false) as AppCompatCheckedTextView
                txtAnswer.text = survey.questions[survey.totalAnswer].options[i].title
                txtAnswer.isChecked = survey.questions[survey.totalAnswer].options[i].isChecked
                ViewCompat.setElevation(txtAnswer, if (txtAnswer.isChecked) {
                    SizeHelper.size4.toFloat()
                } else {
                    SizeHelper.size1.toFloat()
                })

                txtAnswer.setOnClickListener {
                    presenter.getAds.survey?.let { survey ->
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

    private fun unCheckOption() {
        val layoutAnswer = layoutOption.getChildAt(0) as LinearLayout

        for (i in 0 until layoutAnswer.childCount) {
            if (layoutAnswer.getChildAt(i) is AppCompatCheckedTextView) {
                (layoutAnswer.getChildAt(i) as AppCompatCheckedTextView).isChecked = false
                ViewCompat.setElevation((layoutAnswer.getChildAt(i) as AppCompatCheckedTextView), SizeHelper.size1.toFloat())
            }
        }

        presenter.getAds.survey?.let {
            for (obj in it.questions[it.totalAnswer].options) {
                obj.isChecked = false
            }
        }
    }

    private fun setupListener() {
        btnHome.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 1))
            onBackPressed()
        }

        btnLeft.setOnClickListener {
            if (layoutOption.getChildAt(0).animation != null) {
                return@setOnClickListener
            }

            if (btnLeft.text == getString(R.string.bo_qua)) {
                DialogHelper.showConfirm(this@BannerSurveyActivity, R.string.bo_qua_khao_sat, R.string.ban_chac_chan_bo_qua_khao_sat_nay_chu, object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        presenter.hideDirectSurvey()
                    }
                })
            } else {
                presenter.getAds.survey?.let { survey ->
                    if (survey.totalAnswer > 0) {
                        survey.totalAnswer--
                        progressBar.progress = (survey.totalAnswer + 1)
                        changeQuestion(false)
                        checkButton()
                    }
                }
            }
        }

        btnRight.setOnClickListener {
            if (layoutOption.getChildAt(0).animation != null) {
                return@setOnClickListener
            }

            presenter.getAds.survey?.let { survey ->
                if (survey.totalAnswer < survey.questions.size - 1) {
                    if (presenter.isDoneQuestion) {
                        survey.totalAnswer++
                        progressBar.progress = (survey.totalAnswer + 1)

                        changeQuestion(true)
                        checkButton()
                    } else {
                        ToastUtils.showShortError(this, R.string.vui_long_chon_cau_tra_loi)
                    }
                } else {
                    presenter.getAds.respond_url?.let { respondUrl ->
                        val isDoneQuestion = presenter.isDoneQuestion

                        if (isDoneQuestion) {
                            presenter.answerQuestion()
                        } else {
                            showShortError(R.string.vui_long_chon_cau_tra_loi)
                        }
                    }
                }
            }
        }
    }

    private fun checkButton() {
        presenter.getAds.survey?.let { survey ->
            txtQuestionTitle.text = getString(R.string.cau_hoi_s, "${(survey.totalAnswer + 1)}/${survey.questions.size}")
            progressBar.max = survey.questions.size
            progressBar.progress = survey.totalAnswer + 1
        }

        btnLeft.text = presenter.getLeftButtonText
        btnRight.text = presenter.getRightButtonText
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
        val animation = AnimationUtils.loadAnimation(this, resource)
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

    override fun onGetDataError() {
        imgBack.performClick()
    }

    override fun onGetDataSuccess() {
        presenter.getAds.survey?.let { survey ->
            tvName.text = survey.title
            checkButton()
            initQuestion()
            setupListener()
        }
    }

    override fun showLoading() {
        DialogHelper.showLoading(this)
    }

    override fun closeLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onHideSurvey() {
        val intent = Intent()
        intent.putExtra(Constant.DATA_1, presenter.getAds.id)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    override fun onAnsweredSurveySuccess() {
        imgBanner.setImageResource(R.drawable.ic_banner_survey)
        layoutCenter.visibility = View.GONE

        imgBanner.setImageResource(R.drawable.ic_direct_survey_success)
        layoutSuccess.visibility = View.VISIBLE

        imgBack.visibility = View.GONE
        btnLeft.visibility = View.GONE
        btnRight.visibility = View.GONE
        btnHome.visibility = View.VISIBLE

        isSuccess = true
    }

    override fun showError(errorMessage: String) {

        ToastUtils.showShortError(this@BannerSurveyActivity, errorMessage)
    }

    override val mContext: Context
        get() = this@BannerSurveyActivity

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@BannerSurveyActivity, isShow)
    }

    override fun onPause() {
        super.onPause()

        val intent = Intent()

        if (!isSuccess) {
            intent.putExtra(Constant.DATA_1, JsonHelper.toJson(presenter.getAds))
            setResult(Activity.RESULT_CANCELED, intent)
        } else {
            intent.putExtra(Constant.DATA_1, presenter.getAds.id)
            setResult(Activity.RESULT_OK, intent)
        }
    }
}