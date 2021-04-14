package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_open_reward_box_v2.*
import kotlinx.coroutines.isActive
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.NotEnoughPointListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.constant.CAMPAIGN_ID
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.LOGO
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.network.models.ICGridBoxShake
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.screen.user.campaign_onboarding.CampaignOnboardingActivity
import vn.icheck.android.screen.user.gift_campaign.GiftOfCampaignActivity
import vn.icheck.android.screen.user.gift_history.v2.GiftHistoryV2Activity
import vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list.ListMissionActivity
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.bottom_sheet.AddMoreTurnBottomSheet
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.bottom_sheet.MoreShakeBottomSheet
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.adapter.BoxGiftAdapter
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.viewmodel.ListRewardBoxV2ViewModel
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake.ShakeGiftActivity
import vn.icheck.android.screen.user.winner_campaign.WinnerCampaignActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.kotlin.StatusBarUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ListShakeGridBoxActivity : BaseActivityMVVM() {

    companion object {
        var numberGiftUser = 0
    }

    private val adapter = BoxGiftAdapter(this)
    private lateinit var viewModel: ListRewardBoxV2ViewModel
    private var objGrid: ICGridBoxShake? = null

    private val requestCount = 1
    private val requestShakeBox = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_reward_box_v2)
        StatusBarUtils.setOverStatusBarLight(this)
        viewModel = ViewModelProvider(this).get(ListRewardBoxV2ViewModel::class.java)
        viewModel.getDataIntent(intent)
        listenerGetData()
        initRecycleView()
        listener()

        val campaign = intent?.getSerializableExtra(Constant.DATA_1)
        if (campaign != null) {
            if (campaign is String) {
                TrackingAllHelper.tagCampaignHomescreenViewed(campaign)
            } else {
                TrackingAllHelper.tagCampaignHomescreenViewed((campaign as ICCampaign).id)
            }
        }
    }

    private fun initRecycleView() {
        recyclerView.layoutManager = GridLayoutManager(this@ListShakeGridBoxActivity, 3, GridLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.setItemClickListener(object : ItemClickListener<ICGridBoxShake> {
            override fun onItemClick(position: Int, item: ICGridBoxShake?) {
                if (numberGiftUser == 0) {
                    checkShowDialogEmpity()
                } else {
                    TrackingAllHelper.tagOpenGiftboxStarted(viewModel.idCampaign)
                    startShakeBox(item)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun listenerGetData() {
        viewModel.icItemReward.observe(this, {
            it.itemCount?.let { number -> numberGiftUser = number }
            WidgetUtils.loadImageUrl(imgBanner, it.image, R.drawable.bg_error_campaign, R.drawable.bg_error_campaign)
            tvCountGift.text = "${it.itemCount}"
        })

        viewModel.listGridBox.observe(this, {
            adapter.setListData(it)
        })

        viewModel.dataCampaign.observe(this, {
            AddMoreTurnBottomSheet(this@ListShakeGridBoxActivity).show()
        })

        viewModel.dataListMissionActive.observe(this, {
            if (numberGiftUser == 0) {
                checkShowDialogEmpity()
            }
        })

        viewModel.statusCode.observe(this, {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.getDataIntent(intent)
                        }
                    })
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(this, {
            when (it) {
                Constant.ERROR_EMPTY -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }

                Constant.ERROR_SERVER -> {
                    DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                        override fun onDone() {
                            onBackPressed()
                        }
                    })
                }
            }
        })
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        tvAddMoreTurn.setOnClickListener {
            viewModel.idCampaign?.let { id ->
                tvAddMoreTurn.isEnabled = false
                ListMissionActivity.show(this, id)
            }
        }

        btnMore.setOnClickListener {
            btnMore.isEnabled = false
            object : MoreShakeBottomSheet(this@ListShakeGridBoxActivity, viewModel.objCampaign) {
                override fun onClickHistoryShake() {
                    startActivity(Intent(this@ListShakeGridBoxActivity, GiftHistoryV2Activity::class.java).apply {
                        putExtra(Constant.DATA_1, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.id}")
                        putExtra(LOGO, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.image}")
                    })
                    dialog.dismiss()
                }

                override fun onClickHistoryReward() {
                    startActivity(Intent(this@ListShakeGridBoxActivity, GiftOfCampaignActivity::class.java).apply {
                        putExtra(CAMPAIGN_ID, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.id}")
                        putExtra(LOGO, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.image}")
                    })
                    dialog.dismiss()
                }

                override fun onClickTutorial() {
                    startActivity(Intent(this@ListShakeGridBoxActivity, WinnerCampaignActivity::class.java).apply {
                        putExtra(Constant.DATA_1, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.id}")
                    })
                    dialog.dismiss()
                }

                override fun onClickInfor() {
                    val intent = Intent(context, CampaignOnboardingActivity::class.java)
                    intent.putExtra(Constant.DATA_1, viewModel.objCampaign)
                    startActivity(intent)
                    dialog.dismiss()
                }
            }.show()
            Handler().postDelayed({
                btnMore.isEnabled = true
            }, 2000)
        }
    }

    fun startShakeBox(item: ICGridBoxShake?) {
        objGrid = item
        if (SessionManager.isUserLogged) {
            onRequireLoginSuccess(requestShakeBox)
        } else {
            onRequireLogin(requestShakeBox)
        }

    }

    private fun checkShowDialogEmpity() {
        /*TH1: K có hộp quà + có mission thành công ->Bạn đã hết lượt mở quà rồi!
        TH2: K có hộp quà + k có mission thành công ->Bạn chưa có lượt mở quà nào!
        TH3: Lắc hết quà->>Bạn đã hết lượt mở quà rồi!
         */

        if (viewModel.typeCheckDialogGift == 1) {
            if (lifecycleScope.isActive) {
                if (viewModel.isMissionSuccess) {
                    showEmpityGiftDialog("Bạn đã hết lượt mở quà rồi!")
                } else {
                    showEmpityGiftDialog("Bạn chưa có lượt mở quà nào!")
                }
            }
        } else {
            showEmpityGiftDialog("Bạn đã hết lượt mở quà rồi!")
        }
    }

    private fun showEmpityGiftDialog(message: String) {
        DialogHelper.showDialogEmtyBoxGift(this@ListShakeGridBoxActivity, R.drawable.ic_emty_box_shake, message, viewModel.objCampaign!!.id!!, viewModel.listMissionActive, false, object : NotEnoughPointListener {
            override fun onClose() {
            }

            override fun onGiveIcoin() {
                startActivity(Intent(this@ListShakeGridBoxActivity, GiftOfCampaignActivity::class.java).apply {
                    putExtra(CAMPAIGN_ID, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.id}")
                    putExtra(LOGO, "${this@ListShakeGridBoxActivity.viewModel.objCampaign?.image}")
                })
            }
        })
    }

    override fun onResume() {
        tvAddMoreTurn.isEnabled = true
        super.onResume()
    }

    override fun onRequireLoginSuccess(requestCode: Int) {
        super.onRequireLoginSuccess(requestCode)
        when (requestCode) {
            requestShakeBox -> {
                val intent = Intent(this@ListShakeGridBoxActivity, ShakeGiftActivity::class.java)
                intent.putExtra(Constant.DATA_1, objGrid)
                intent.putExtra(Constant.DATA_2, viewModel.objCampaign)
                startActivityForResult(intent, requestCount)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCount) {
                viewModel.getDataIntentWithType(data)
                tvCountGift.text = "$numberGiftUser"
                if (numberGiftUser == 0) {
                    checkShowDialogEmpity()
                }
            } else if (requestCode == ListMissionActivity.REQUEST_LIST_MISSION) {
                val mission = data?.getSerializableExtra(Constant.DATA_1)
                if (mission != null && mission is ICMissionDetail) {
                    mission.campaignId?.let { viewModel.getInfoCampaign(it) }
                }
            }
        }
    }

    override fun onBackPressed() {
        viewModel.objCampaign?.itemCount = numberGiftUser
        Intent().apply {
            putExtra(Constant.DATA_1, viewModel.objCampaign)
            setResult(RESULT_OK, this)
            finish()
        }
        super.onBackPressed()
    }

}