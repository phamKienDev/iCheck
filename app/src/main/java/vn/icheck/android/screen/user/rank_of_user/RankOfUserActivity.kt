package vn.icheck.android.screen.user.rank_of_user

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_rank_of_user.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.screen.user.history_accumulate_points.HistoryAccumulatePointActivity
import vn.icheck.android.screen.user.list_campaign.ListCampaignActivity
import vn.icheck.android.screen.user.mygift.MyGiftActivity
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class RankOfUserActivity : BaseActivityMVVM() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_of_user)

        StatusBarUtils.setOverStatusBarLight(this)
        initView()
        initViewModel()
        listener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        seekBar.progressDrawable=ViewHelper.progressWhiteBackgroundGrayCorners4(this)
        seekBar.isEnabled = false
        seekBar.setOnTouchListener { view, motionEvent ->
            false
        }

        tvBenefit.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this))
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel() {
        SessionManager.session.user?.rank?.let {
            val score = TextHelper.formatMoneyPhay(it.score)

            tvPointCurrent.setText(R.string.diem_hien_tai_s, score)

            if (SessionManager.session.user?.avatar.isNullOrEmpty()) {
                imgAvatar.borderWidth = 0
                imgAvatar.setImageResource(R.drawable.ic_avatar_default_84dp)
            } else {
                imgAvatar.borderWidth = SizeHelper.size1
                WidgetUtils.loadImageUrl(imgAvatar, SessionManager.session.user?.avatar, R.drawable.ic_avatar_default_84dp, R.drawable.ic_avatar_default_84dp)
            }

            if (it.nextTarget != null && it.score != null) {
                if (it.level == 4) {
                    seekBar.max = 100
                    seekBar.progress = 100
                } else {
                    seekBar.max = it.nextTarget
                    seekBar.progress = it.score
                }
            }

            when (it.level) {
                Constant.USER_LEVEL_STANDARD -> {
                    tvPoint.setText(R.string.s_diem, score)
                    tvPoint.setTextColor(Color.parseColor("#ff5757"))
                    tvNameRank.setText(R.string.thanh_vien_chuan)
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_standard_36dp)
                    goToHistory.setImageResource(R.drawable.ic_arrow_right_standard_8)
                    imgBanner.setImageResource(R.drawable.ic_account_level_standard)
                }
                Constant.USER_LEVEL_SILVER -> {
                    tvPoint.setText(R.string.s_diem, score)
                    tvPoint.setTextColor(Color.parseColor("#696969"))
                    tvNameRank.setText(R.string.thanh_vien_bac)
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_silver_36dp)
                    goToHistory.setImageResource(R.drawable.ic_arrow_right_silver_8)
                    imgBanner.setImageResource(R.drawable.ic_account_level_silver)
                }
                Constant.USER_LEVEL_GOLD -> {
                    tvPoint.setText(R.string.s_diem, score)
                    tvPoint.setTextColor(Color.parseColor("#ffb800"))
                    tvNameRank.setText(R.string.thanh_vien_vang)
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_gold_36dp)
                    goToHistory.setImageResource(R.drawable.ic_arrow_right_gold_8)
                    imgBanner.setImageResource(R.drawable.ic_account_level_gold)
                }
                Constant.USER_LEVEL_DIAMOND -> {
                    tvPoint.setText(R.string.s_diem, score)
                    tvPoint.setTextColor(Color.parseColor("#057dda"))
                    tvNameRank.setText(R.string.thanh_vien_kim_cuong)
                    img_rank_user.setImageResource(R.drawable.ic_leftmenu_avatar_diamond_36dp)
                    goToHistory.setImageResource(R.drawable.ic_arrow_right_diamond_8)
                    imgBanner.setImageResource(R.drawable.ic_account_level_diamond)
                }
            }

            val avaiablePoint = TextHelper.formatMoneyPhay(it.nextTarget - it.score)
            when (it.level) {
                1 -> {
                    imgLevel.setImageResource(R.drawable.ic_point_silver_24_px)
                    tvMessageUpRank.text = Html.fromHtml("${getString(R.string.diem_tich_luy_dung_de_tang_han_thanh_vien_cua_ban_tich_luy_them)} <font color=#434343><b>${avaiablePoint}</b></font> điểm để đạt Thành viên Bạc")
                }
                2 -> {
                    imgLevel.setImageResource(R.drawable.ic_point_gold_24_px)
                    tvMessageUpRank.text = Html.fromHtml("${getString(R.string.diem_tich_luy_dung_de_tang_han_thanh_vien_cua_ban_tich_luy_them)} <font color=#434343><b>${avaiablePoint}</b></font> điểm để đạt Thành viên Vàng")
                }
                3 -> {
                    imgLevel.setImageResource(R.drawable.ic_point_diamond_24_px)
                    tvMessageUpRank.text = Html.fromHtml("${getString(R.string.diem_tich_luy_dung_de_tang_han_thanh_vien_cua_ban_tich_luy_them)} <font color=#434343><b>${avaiablePoint}</b></font> điểm để đạt Thành viên Kim Cương")
                }
                4 -> {
                    imgLevel.setImageResource(R.drawable.ic_point_diamond_24_px)
                    tvMessageUpRank.typeface = ResourcesCompat.getFont(this, R.font.barlow_semi_bold)
                    tvMessageUpRank.text = Html.fromHtml(getString(R.string.chuc_mung_ban_da_la_thanh_vien_kim_cuong))
                }
            }
        }

//        viewModel.message.observe(this, {
//            showShortError(it)
//            onBackPressed()
//        })

//        viewModel.statusCode.observe(this, {
//            when (it) {
//                ICMessageEvent.Type.ON_NO_INTERNET -> {
//                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
//                        override fun onDisagree() {
//                            onBackPressed()
//                        }
//
//                        override fun onAgree() {
////                            viewModel.getRankUser()
//                        }
//                    })
//                }
//                ICMessageEvent.Type.ON_SHOW_LOADING -> {
//                    DialogHelper.showLoading(this)
//                }
//                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
//                    DialogHelper.closeDialog()
//                }
//                else -> {
//                }
//            }
//        })
    }

    private fun listener() {
        tvPoint.setOnClickListener {
            startActivity<HistoryAccumulatePointActivity>()
        }

        goToHistory.setOnClickListener {
            startActivity<HistoryAccumulatePointActivity>()
        }

        tvMyGift.setOnClickListener {
            startActivity<MyGiftActivity>()
        }

        tvMission.setOnClickListener {
            startActivity<ListCampaignActivity>()
        }

        tvBenefit.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this))
        tvBenefit.setOnClickListener {
            DialogHelper.showLoading(this)
            SettingHelper.getSystemSetting("ranking-support.benefit-url", "ranking-support", object : ISettingListener {
                override fun onRequestError(error: String) {
                    DialogHelper.closeLoading(this@RankOfUserActivity)

                }


                override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                    DialogHelper.closeLoading(this@RankOfUserActivity)
                    WebViewActivity.start(this@RankOfUserActivity, list?.firstOrNull()?.value, null, getString(R.string.quyen_loi_thanh_vien))
                }
            })
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        btn_how_to_rank_up.setOnClickListener {
            SettingHelper.getSystemSetting("ranking-support.direction", "ranking-support", object : ISettingListener {
                override fun onRequestError(error: String) {
                    logDebug(error)
                }

                override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                    WebViewActivity.start(this@RankOfUserActivity, list?.firstOrNull()?.value, null, getString(R.string.cach_tinh_diem))
                }
            })
//            WebViewActivity.start(this, "https://icheck.vn/ranking-support.suport-url", null,"Cách tích điểm")
        }
        tv_frequently_asked.setOnClickListener {
            SettingHelper.getSystemSetting("ranking-support.support-url", "ranking-support", object : ISettingListener {
                override fun onRequestError(error: String) {
                    logDebug(error)
                }

                override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                    WebViewActivity.start(this@RankOfUserActivity, list?.firstOrNull()?.value, null, getString(R.string.cau_hoi_thuong_gap))
                }
            })
        }
    }
}