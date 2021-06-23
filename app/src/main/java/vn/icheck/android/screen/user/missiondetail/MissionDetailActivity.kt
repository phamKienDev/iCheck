package vn.icheck.android.screen.user.missiondetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_mission_detail.*
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.models.ICCompany
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.campaign_onboarding.CampaignOnboardingActivity
import vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list.ListMissionActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.HtmlHelper.setHtmlText

class MissionDetailActivity : BaseActivityMVVM(), View.OnClickListener {
    private lateinit var viewModel: MissionDetailViewModel
    private lateinit var adapterProduct: ApplyMisssionAdapter
    private lateinit var adapterCategory: ApplyMisssionAdapter
    private lateinit var adapterCompany: ApplyMisssionAdapter

    private var missionViewedInsider = true
    private var screenFrom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission_detail)
        StatusBarUtils.setOverStatusBarLight(this)

        screenFrom = intent.getStringExtra(Constant.DATA_2)
        setupView()
        setNestedScrollView()
        setupViewModel()
        WidgetUtils.setClickListener(this, tvAllProduct, tvAllCategory, tvAllCompany, imgBack)
    }

    private fun setupView() {
        btnConfirm.background = ViewHelper.bgPrimaryCorners4(this)
    }

    private fun setNestedScrollView() {
        layoutContent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < layoutToolbarAlpha.height) {
                val alpha = (1f / layoutToolbarAlpha.height) * scrollY
                layoutToolbarAlpha.alpha = alpha
            } else {
                layoutToolbarAlpha.alpha = 1f
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MissionDetailViewModel::class.java)

        viewModel.onMissionDetail.observe(this, { obj ->
            if (missionViewedInsider) {
                TekoHelper.tagMissionClicked(obj.missionName)
                TrackingAllHelper.tagMisssionDetailViewed(obj.campaignId, obj.id)
                missionViewedInsider = false
            }

            WidgetUtils.loadImageUrl(imgLogo, obj.campaignImage, R.drawable.bg_error_campaign)
            txtTitle.text = obj.missionName

            checkState(obj)
            progressBar.isEnabled = false
            tvProgress.text = getString(R.string.tien_do_thuc_hien_nhiem_vu_xxx, "${((obj.currentEvent.toDouble() / obj.totalEvent.toDouble()) * 100).toInt()}%")
            tvProgressTitle.text = obj.missionName
            tvProgressCount.text = ("${obj.currentEvent}/${obj.totalEvent}")
            progressBar.max = obj.totalEvent
            progressBar.progress = obj.currentEvent
            tvProgressState.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (obj.currentEvent >= obj.totalEvent) {
                R.drawable.ic_checkbox_single_on_24dp
            } else {
                R.drawable.ic_checkbox_single_off_24dp
            }, 0)


            if (obj.guide.isNullOrEmpty()) {
                containerGuide.beGone()
            } else {
                containerGuide.beVisible()
                tvGuide.setHtmlText(obj.guide!!)
            }

            initProduct(obj.products)
            initCategory(obj.categories)
            initCompany(obj.companies)

            if (obj.description.isNullOrEmpty()) {
                tvInformation.beGone()
            } else {
                tvInformation.beVisible()
                tvInformation.setHtmlText(obj.description!!)
            }
            tvViewInfomation.setOnClickListener {
                obj.campaignId?.let { it1 -> startActivity<CampaignOnboardingActivity>(Constant.DATA_1, it1) }
            }

            checkButton(obj)
        })

        viewModel.onChangeState.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    showLoading()
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    closeLoading()
                }
                else -> {
                }
            }
        })

        viewModel.onError.observe(this, {
            DialogHelper.showConfirm(this@MissionDetailActivity, it, false, object : ConfirmDialogListener {
                override fun onDisagree() {
                    onBackPressed()
                }

                override fun onAgree() {
                    viewModel.getMissionDetail()
                }
            })
        })

        viewModel.getData(intent)
    }

    private fun checkState(obj: ICMissionDetail) {
        when (obj.finishState) {
            0 -> { // Chưa diễn ra
                // Hiển thị thời gian còn lại
                tvEnded.visibility = View.GONE
                layoutHeader.visibility = View.VISIBLE
                tvTimeTilte.text = getString(R.string.thoi_gian_dien_ra)
                tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(obj.beginAt)

                tvGift.visibility = View.VISIBLE
                tvGift.text = getString(R.string.xxx_luot_mo_qua, obj.totalBox)

                if (obj.image.isNullOrEmpty()) {
                    imgIcon.setImageResource(Constant.getMissionInprogressIcon(obj.event))
                } else {
                    WidgetUtils.loadImageUrl(imgIcon, obj.image)
                }
                progressBar.progressDrawable = ViewHelper.progressGrayBackgroundColorLineCorners10(this)
            }
            1 -> { // Đang tham gia
                // Hiển thị thời gian còn lại
                tvEnded.visibility = View.GONE
                layoutHeader.visibility = View.VISIBLE
                tvTimeTilte.text = getString(R.string.thoi_gian_con_lai)
                tvDate.text = TimeHelper.convertDateTimeSvToCurrentDateV2(obj.endAt)

                tvGift.visibility = View.VISIBLE
                tvGift.text = getString(R.string.xxx_luot_mo_qua, obj.totalBox)

                if (obj.image.isNullOrEmpty()) {
                    imgIcon.setImageResource(Constant.getMissionInprogressIcon(obj.event))
                } else {
                    WidgetUtils.loadImageUrl(imgIcon, obj.image)
                }
                progressBar.progressDrawable = ViewHelper.progressbarAccentYellowMission(this)
            }
            2 -> { // Đã hoàn thành
                // Hiển thị thời gian hoàn thành
                tvEnded.visibility = View.GONE
                layoutHeader.visibility = View.VISIBLE
                tvTimeTilte.text = getString(R.string.thoi_gian_hoan_thanh)
                tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(obj.finishedAt)

                tvGift.visibility = View.VISIBLE
                tvGift.text = getString(R.string.xxx_luot_mo_qua, obj.totalBox)

                if (obj.image.isNullOrEmpty()) {
                    imgIcon.setImageResource(Constant.getMissionInprogressIcon(obj.event))
                } else {
                    WidgetUtils.loadImageUrl(imgIcon, obj.image)
                }
                progressBar.progressDrawable = ViewHelper.progressbarAccentYellowMission(this)
            }
            else -> { // Thất bại
                layoutHeader.visibility = View.GONE
                progressBar.isEnabled = false
                tvEnded.visibility = View.VISIBLE
                tvProgress.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getSecondTextColor(this))

                if (obj.image.isNullOrEmpty()) {
                    imgIcon.setImageResource(Constant.getMissionFailedIcon(obj.event))
                } else {
                    WidgetUtils.loadImageUrl(imgIcon, obj.image)
                }
                progressBar.progressDrawable = ViewHelper.progressGrayBackgroundColorLineCorners10(this)
            }
        }
    }

    private fun initProduct(listProduct: MutableList<ICProduct>?) {
        lifecycleScope.launch {
            if (listProduct.isNullOrEmpty()) {
                layoutProduct.beGone()
            } else {
                layoutProduct.beVisible()
                if (listProduct.size > 5) {
                    tvAllProduct.beVisible()
                    rcvProduct.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(310))
                } else {
                    tvAllProduct.beGone()
                    rcvProduct.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }

                for (item in listProduct) {
                    if (!item.image.isNullOrEmpty() && item.image?.startsWith("http") == false) {
                        item.image = ImageHelper.getImageUrl(item.image, ImageHelper.smallSize)
                                ?: ""
                    }
                }

                adapterProduct = ApplyMisssionAdapter()
                rcvProduct.adapter = adapterProduct
                rcvProduct.isNestedScrollingEnabled = false
                adapterProduct.setListData(listProduct as MutableList<Any>)
                adapterProduct.disableLoadMore()
            }
        }
    }

    private fun initCategory(listCategory: MutableList<ICCategory>?) {
        lifecycleScope.launch {
            if (listCategory.isNullOrEmpty()) {
                layoutCategory.beGone()
            } else {
                layoutCategory.beVisible()
                if (listCategory.size > 5) {
                    tvAllCategory.beVisible()
                    rcvCategory.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(310))
                } else {
                    tvAllCategory.beGone()
                    rcvCategory.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }

                for (item in listCategory) {
                    if (!item.image.isNullOrEmpty() && item.image?.startsWith("http") == false) {
                        item.image = ImageHelper.getImageUrl(item.image, ImageHelper.smallSize)
                                ?: ""
                    }
                }

                adapterCategory = ApplyMisssionAdapter()
                rcvCategory.adapter = adapterCategory
                rcvCategory.isNestedScrollingEnabled = false
                adapterCategory.setListData(listCategory as MutableList<Any>)
                adapterCategory.disableLoadMore()
            }
        }
    }

    private fun initCompany(listCompany: MutableList<ICCompany>?) {
        lifecycleScope.launch {
            if (listCompany.isNullOrEmpty()) {
                layoutCompany.beGone()
            } else {
                layoutCompany.beVisible()
                if (listCompany.size > 5) {
                    tvAllCompany.beVisible()
                    rcvCompany.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(310))
                } else {
                    tvAllCompany.beGone()
                    rcvCompany.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                }

                for (item in listCompany) {
                    if (!item.image.isNullOrEmpty() && item.image?.startsWith("http") != true) {
                        item.image = ImageHelper.getImageUrl(item.image, ImageHelper.smallSize)
                                ?: ""
                    }
                }

                adapterCompany = ApplyMisssionAdapter()
                rcvCompany.adapter = adapterCompany
                rcvCompany.isNestedScrollingEnabled = false
                adapterCompany.setListData(listCompany as MutableList<Any>)
                adapterCompany.disableLoadMore()
            }
        }
    }

    private fun checkButton(obj: ICMissionDetail) {
        //0 - chưa diễn ra, 1-Đang tham gia, 2-Đã hoàn thành, 3-Không hoàn thành
        when (obj.finishState) {
            0, 3 -> {
                layoutButton.visibility = View.VISIBLE
                btnConfirm.text = getString(R.string.xem_thu_thach_khac)
                btnConfirm.setOnClickListener {
                    if (screenFrom != null) {
                        onBackPressed()
                    } else {
                        viewModel.missionData?.campaignId?.let { id -> ListMissionActivity.show(this, id) }
                    }
                }
            }
            2 -> {
                layoutButton.visibility = View.VISIBLE
                btnConfirm.text = getString(R.string.mo_qua_ngay)

                btnConfirm.setOnClickListener {
                    if (viewModel.missionData?.campaignId != null) {
                        val intent = Intent(this, ListShakeGridBoxActivity::class.java)
                        intent.putExtra(Constant.DATA_1, viewModel.missionData!!.campaignId!!)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            else -> {
                if (!obj.buttonName.isNullOrEmpty()) {
                    layoutButton.visibility = View.VISIBLE
                    btnConfirm.text = obj.buttonName

                    btnConfirm.setOnClickListener {
                        FirebaseDynamicLinksActivity.startDestinationUrl(this, obj.buttonTarget)
                        TrackingAllHelper.tagMisssionDetailCtaClicked(viewModel.missionData?.campaignId, viewModel.missionData?.id)
                        TekoHelper.tagMissionCTAClick(obj.missionName)
                    }
                } else {
                    layoutButton.visibility = View.GONE
                }
            }
        }
    }

    private fun showLoading() {
        layoutContent.visibility = View.INVISIBLE
        layoutLoading.visibility = View.VISIBLE
    }

    private fun closeLoading() {
        layoutContent.visibility = View.VISIBLE
        layoutLoading.visibility = View.INVISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
            R.id.tvAllProduct -> {
                if (tvAllProduct.text == getString(R.string.xem_tat_ca)) {
                    WidgetUtils.changeViewHeight(rcvProduct, rcvProduct.layoutParams.height, viewModel.missionData!!.products!!.size * SizeHelper.dpToPx(62), 300)
                    tvAllProduct.text = getString(R.string.thu_gon)
                    tvAllProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_blue_24dp, 0)
                } else {
                    WidgetUtils.changeViewHeight(rcvProduct, rcvProduct.layoutParams.height, SizeHelper.dpToPx(310), 300)
                    tvAllProduct.text = getString(R.string.xem_tat_ca)
                    tvAllProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24dp, 0)
                }
            }
            R.id.tvAllCategory -> {
                if (tvAllCategory.text == getString(R.string.xem_tat_ca)) {
                    WidgetUtils.changeViewHeight(rcvCategory, rcvCategory.layoutParams.height, viewModel.missionData!!.categories!!.size * SizeHelper.dpToPx(62), 300)
                    tvAllCategory.text = getString(R.string.thu_gon)
                    tvAllCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_blue_24dp, 0)
                } else {
                    WidgetUtils.changeViewHeight(rcvCategory, rcvCategory.layoutParams.height, SizeHelper.dpToPx(310), 300)
                    tvAllCategory.text = getString(R.string.xem_tat_ca)
                    tvAllCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24dp, 0)
                }
            }
            R.id.tvAllCompany -> {
                if (tvAllCompany.text == getString(R.string.xem_tat_ca)) {
                    WidgetUtils.changeViewHeight(rcvCompany, rcvCompany.layoutParams.height, viewModel.missionData!!.companies!!.size * SizeHelper.dpToPx(62), 300)
                    tvAllCompany.text = getString(R.string.thu_gon)
                    tvAllCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_blue_24dp, 0)
                } else {
                    WidgetUtils.changeViewHeight(rcvCompany, rcvCompany.layoutParams.height, SizeHelper.dpToPx(310), 300)
                    tvAllCompany.text = getString(R.string.xem_tat_ca)
                    tvAllCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24dp, 0)
                }
            }
        }
    }

    override fun onBackPressed() {
        Intent().apply {
            putExtra(Constant.DATA_1, viewModel.missionData)
            setResult(RESULT_OK, this)
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMissionDetail(true)
    }
}