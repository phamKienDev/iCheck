package vn.icheck.android.screen.user.surveydetail.answer

import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detail_survey.*
import kotlinx.android.synthetic.main.toolbar_black.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICQuestions
import vn.icheck.android.screen.user.surveydetail.answer.adapter.DetailSurveyQuestionAdapter
import vn.icheck.android.screen.user.surveydetail.answer.presenter.SurveyDetailPresenter
import vn.icheck.android.screen.user.surveydetail.answer.view.ISurveyDetailView
import vn.icheck.android.screen.user.surveydetail.success.SurveyDetailSuccessActivity

/**
 * Created by VuLCL on 10/21/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SurveyDetailActivity : BaseActivity<SurveyDetailPresenter>(), ISurveyDetailView {
    private var questionAdapter: DetailSurveyQuestionAdapter? = null

    override val getLayoutID: Int
        get() = R.layout.activity_detail_survey

    override val getPresenter: SurveyDetailPresenter
        get() = SurveyDetailPresenter(this)

    override fun onInitView() {
        initToolbar()
        setupView()
        presenter.getData(intent)
        initListener()
    }

    /**
     * Hiển thị tiêu đề của bản tin và lắng nghe người dùng nhấn quay lại
     */
    private fun initToolbar() {
        txtTitle.setText(R.string.khao_sat_nhan_qua_title)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        btnSend.background = ViewHelper.btnPrimaryCorners4(this)
        btnSkip.background= ViewHelper.btnWhiteStrokeSecondary1Corners4(this)
    }

    /**
     * Lắng nghe tương tác của người dùng
     */
    private fun initListener() {
        btnSkip.setOnClickListener {
            DialogHelper.showConfirm(this@SurveyDetailActivity, R.string.bo_qua_khao_sat, R.string.ban_chac_chan_bo_qua_khao_sat_nay_chu, object : ConfirmDialogListener {
                override fun onDisagree() {

                }

                override fun onAgree() {
                    presenter.hideDirectSurvey()
                }
            })
        }

        btnSend.setOnClickListener {
            questionAdapter?.getAnswered?.let { answer ->
                presenter.answerDirectSurvey(answer)
            }
        }
    }

    /**
     * Hiển thị thông báo khi không lấy được ID của ADS truyền sang
     */
    override fun onGetDataError() {
        DialogHelper.showNotification(this@SurveyDetailActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    /**
     * Hiển thị thông báo khi không có kết nối internet
     */
    override fun onNoInternet() {
        DialogHelper.showConfirm(this@SurveyDetailActivity, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getSurveyDetail()
            }
        })
    }

    /**
     * Hiển thị thông báo khi lấy thông tin ADS không thành công
     */
    override fun onGetDetailError() {
        DialogHelper.showConfirm(this@SurveyDetailActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : ConfirmDialogListener {
            override fun onDisagree() {
                onBackPressed()
            }

            override fun onAgree() {
                presenter.getSurveyDetail()
            }
        })
    }

    /**
     * Hiển thị Dialog Loading khi request lên server
     */
    override fun showLoading() {
        DialogHelper.showLoading(this)
    }

    /**
     * Tắt thị Dialog Loading
     */
    override fun closeLoading() {
        DialogHelper.closeLoading(this)
    }

    /**
     * Hiển thị thông tin danh sách câu hỏi
     * @param obj Danh sách câu hỏi
     */
    override fun onGetDetailSuccess(obj: List<ICQuestions>) {
        questionAdapter = DetailSurveyQuestionAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = questionAdapter
        questionAdapter?.setData(obj)
    }

    /**
     * Bỏ qua khảo sát
     */
    override fun onHideDirectSuccess() {
        onBackPressed()
    }

    /**
     * Trả lời thành công, quay lại màn trước
     */
    override fun onAnsweredSuccess() {
        startActivityAndFinish<SurveyDetailSuccessActivity>()
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showLongError(errorMessage)
    }
}