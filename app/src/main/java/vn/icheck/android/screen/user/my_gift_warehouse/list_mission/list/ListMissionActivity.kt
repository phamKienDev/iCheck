package vn.icheck.android.screen.user.my_gift_warehouse.list_mission.list

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_gift_list_mission.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.tracking.teko.TekoHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.mission.MissionInteractor
import vn.icheck.android.network.models.ICMission
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.missiondetail.MissionDetailActivity
import vn.icheck.android.screen.user.mygift.MyGiftActivity
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils

class ListMissionActivity : BaseActivityMVVM() {
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var adapter: ListMissionAdapter
    private var behavior: CoordinatorLayout.Behavior<*>? = null

    private val requestMissionDetail = 1
    private var missionSuccess: ICMissionDetail? = null

    companion object {
        const val REQUEST_LIST_MISSION = 18
        fun show(activity: Activity?, campaignId: String) {
            activity?.let {
                if (NetworkHelper.isNotConnected(activity)) {
                    return
                }

                DialogHelper.showLoading(activity)

                MissionInteractor().getListMission(campaignId, object : ICNewApiListener<ICResponse<ICListResponse<ICMission>>> {
                    override fun onSuccess(obj: ICResponse<ICListResponse<ICMission>>) {
                        DialogHelper.closeLoading(activity)
                        val listSuccess = mutableListOf<ICMission>()
                        val listProgress = mutableListOf<ICMission>()

                        for (item in obj.data?.rows ?: mutableListOf()) {
                            if (item.finishState != 2) {
                                listProgress.add(item)
                            } else {
                                listSuccess.add(item)
                            }
                        }

                        if (listSuccess.isNotEmpty()) {
                            listProgress.add(ICMission())
                            listProgress.addAll(listSuccess)
                        }

                        TrackingAllHelper.tagMisssionListViewed(campaignId)
                        TekoHelper.tagMissionImpression()

                        val json = JsonHelper.toJson(listProgress)
                        ActivityUtils.startActivityWithoutAnimation<ListMissionActivity, String>(activity, Constant.DATA_1, json, REQUEST_LIST_MISSION)
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        ToastUtils.showLongError(activity, error?.message
                                ?: activity.getString(R.string.khong_the_truy_cap_vui_long_thu_lai_sau))
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_list_mission)

        setupView()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        val listMission = JsonHelper.parseListMission(intent.getStringExtra(Constant.DATA_1))

        if (!listMission.isNullOrEmpty()) {
            var isSuccessAll = true
            for (mission in listMission) {
                if (mission.finishState != 2 && mission.id.isNotEmpty()) {
                    isSuccessAll = false
                }
            }

            if (isSuccessAll) {
                showBottomSuccess()
            } else {
                showBottomInprogress()
            }
        } else {
            showBottomSuccess()
        }
        setupListener()
    }

    private fun setupView() {
        btnAction.background = ViewHelper.btnPrimaryCorners4(this)
    }

    private fun showBottomSuccess() {
        bottomSheet.beGone()
        layoutSuccess.beVisible()
        setupBottomSheetSuccess()

    }

    private fun showBottomInprogress() {
        bottomSheet.beVisible()
        layoutSuccess.beGone()
        setupBottomSheet()
        setupRecyclerView()
    }

    private fun setupBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Handler().postDelayed({
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }, 200)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (missionSuccess != null) {
                        Intent().apply {
                            putExtra(Constant.DATA_1, missionSuccess)
                            setResult(RESULT_OK, this)
                        }
                    }
                    ActivityUtils.finishActivityWithoutAnimation(this@ListMissionActivity)
                }
            }
        })

        viewHide.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupBottomSheetSuccess() {
        sheetBehavior = BottomSheetBehavior.from(layoutSuccess)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Handler().postDelayed({
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }, 200)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    ActivityUtils.finishActivityWithoutAnimation(this@ListMissionActivity)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val listData = JsonHelper.parseListMission(intent.getStringExtra(Constant.DATA_1))
                ?: mutableListOf()

        adapter = ListMissionAdapter(listData)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        behavior = (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
                    bottomSheet.requestLayout()
                } else {
                    (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior = null
                }
            }
        })
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.OPEN_DETAIL_MISSION -> {
                if (event.data != null && event.data is String) {
                    val intent = Intent(this, MissionDetailActivity::class.java)
                    intent.putExtra(Constant.DATA_1, event.data)
                    intent.putExtra(Constant.DATA_2, "missionList")
                    startActivityForResult(intent, requestMissionDetail)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == requestMissionDetail) {
                val mission = data?.getSerializableExtra(Constant.DATA_1)
                if (mission != null && mission is ICMissionDetail) {
                    adapter.updateMission(mission)
                    if (mission.finishState == 2)
                        missionSuccess = mission
                }
            }
        }
    }

    override fun onBackPressed() {
        if (bottomSheet.isVisible) {
            (bottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
            bottomSheet.requestLayout()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            sheetBehavior.isHideable = true
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }, 100)
    }

    private fun setupListener() {
        imgCloseSuccess.setOnClickListener {
            onBackPressed()
        }
        imgClose.setOnClickListener {
            onBackPressed()
        }
        btnAction.setOnClickListener {
            startActivity<MyGiftActivity>()
        }
    }
}