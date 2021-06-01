package vn.icheck.android.screen.user.surveydetail.success

import android.view.View
import kotlinx.android.synthetic.main.fragment_answer_survey_success.*
import kotlinx.android.synthetic.main.toolbar_black.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.surveydetail.success.presenter.SurveyDetailSuccessPresenter
import vn.icheck.android.screen.user.surveydetail.success.view.ISurveyDetailSuccessView

/**
 * Created by VuLCL on 10/22/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class SurveyDetailSuccessActivity : BaseActivity<SurveyDetailSuccessPresenter>(), ISurveyDetailSuccessView {

    override val getLayoutID: Int
        get() = R.layout.fragment_answer_survey_success

    override val getPresenter: SurveyDetailSuccessPresenter
        get() = SurveyDetailSuccessPresenter(this)

    override fun onInitView() {
        initToolbar()
        setupView()
        initListener()
    }

    /**
     * Hiển thị tiêu đề của bản tin và ẩn nút quay lại
     */
    private fun initToolbar() {
        txtTitle.setText(R.string.khao_sat_nhan_qua_title)
        imgBack.visibility = View.GONE
    }

    private fun setupView() {
        btnShare.background = ViewHelper.btnPrimaryCorners4(this)
        btnHome.background = ViewHelper.bgWhitePressRadius4StrokeSecondary1(this)
    }

    /**
     * Lắng nghe tương tác của người dùng
     */
    private fun initListener() {
        btnHome.setOnClickListener {
            onBackPressed()
        }

        btnShare.setOnClickListener {
            showLongWarning(R.string.chuc_nang_dang_duoc_xay_dung)
        }
    }
}