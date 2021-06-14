package vn.icheck.android.screen.user.option_edit_information_public

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_edit_information_public.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.USER_WALL_BROADCAST
import vn.icheck.android.constant.USER_WALL_EDIT_PERSONAL
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.wall.ICUserPublicInfor
import vn.icheck.android.util.kotlin.StatusBarUtils

class EditInformationPublicActivity : BaseActivityMVVM(), IEditInforPublicView {

    private val viewModel: EditInformationPublicViewModel by viewModels()

    private lateinit var adapter: ListPrivacyPublicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_information_public)
        initView()
        initRecyclerView()
        listener()
        listenerGetData()
        viewModel.getPublicInfor()
    }

    private fun initView() {
        StatusBarUtils.setOverStatusBarLight(this)
        txtTitle.text = "Chỉnh sửa thông tin công khai"
    }

    private fun initRecyclerView() {
        adapter = ListPrivacyPublicAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnUpdateInfor.apply {
            background = ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                setResult(RESULT_OK)
                sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                    putExtra(USER_WALL_BROADCAST, USER_WALL_EDIT_PERSONAL)
                })
                finish()
            }
        }
    }

    private fun listenerGetData() {
        viewModel.nullInfor.observe(this, Observer {
            if (it == true) {
                layoutEmptyInformation.visibility = View.VISIBLE
                layoutData.visibility = View.GONE
            }
        })

        viewModel.data.observe(this, Observer {
            layoutEmptyInformation.visibility = View.GONE
            layoutData.visibility = View.VISIBLE
            adapter.setListData(it)
        })

        viewModel.positionResult.observe(this, Observer {
            adapter.setStateClick(it)
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            viewModel.getPublicInfor()
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(this, Observer {
            when (it) {
                Constant.ERROR_SERVER -> {
                    adapter.notifyDataSetChanged()
                    showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_INTERNET -> {
                    adapter.notifyDataSetChanged()
                    showShortError(R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau)
                }
            }
        })
    }

    override fun onClick(item: ICUserPublicInfor, checked: Boolean, position: Int) {
        lifecycleScope.launch {
            delay(300)
            viewModel.updateInforPublic(item.privacyElementId, checked,position)
        }
    }
}