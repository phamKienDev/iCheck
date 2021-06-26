package vn.icheck.android.screen.user.list_friend_in_wall

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_list_friend_of_wall.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.screen.dialog.ReportDialog
import vn.icheck.android.screen.dialog.ReportSuccessDialog
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.screen.user.wall.USER_ID
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.ick.forceShowKeyboard
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.util.ick.forceHideKeyboard
import vn.icheck.android.util.ick.simpleText
import java.util.concurrent.TimeUnit

class ListFriendOfWallActivity : BaseActivityMVVM(), ListFriendListener {

    private val viewModel: ListFriendOfWallViewModel by viewModels()

    private val adapter = ListFriendOfWallAdapter(this)

    private var key: String? = null

    private var positionList: Int? = null

    private var nameItem: String? = null

    private var itemId: Long? = null

    private lateinit var dialog: ReportDialog

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_friend_of_wall)
        initView()
        listener()
        initRecyclerView()
        listenerGetData()
        viewModel.getListFriend(uid = intent.getLongExtra(USER_ID, 0L))
        initTextListener()
    }

    private fun initView() {
        txtTitle.setText(R.string.danh_sach_ban_be)
        edtSearch.background=ViewHelper.bgGrayCorners4(this)
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        rcv_follow.layoutManager = LinearLayoutManager(this)
        rcv_follow.adapter = adapter
    }

    private fun initTextListener() {
        disposable = RxTextView.afterTextChangeEvents(edtSearch).skipInitialValue().debounce(400, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            key = it.view().text.toString()
            adapter.clearListData()
            viewModel.getListFriend(it.view().text.toString(), false, uid = intent.getLongExtra(USER_ID, 0L), isSearch = true)

            edtSearch.requestFocus()
            edtSearch.post {
                this.forceShowKeyboard(edtSearch)
            }
        }
    }

    private fun listenerGetData() {
        runOnUiThread {
            viewModel.invitationFriend.observe(this) {
                when (it) {
                    1 -> {
                        adapter.updateState(positionList!!, 3)
                    }
                    2 -> {
                        adapter.updateState(positionList!!, 4)
                    }
                    3 -> {
                        adapter.updateState(positionList!!, 5)
                    }
                }
            }

            viewModel.listData.observe(this, Observer {
                adapter.addListData(it)
                tv_total_friend.visibility = View.VISIBLE
                tv_total_friend simpleText if (viewModel.friendCount > 0) getString(R.string.d_ban_be, viewModel.friendCount) else getString(R.string.ban_be)
            })

            viewModel.isLoadMoreData.observe(this, Observer {
                adapter.removeDataWithoutUpdate()
            })

            viewModel.unFriend.observe(this, Observer {
                lifecycleScope.launch {
                    delay(100)
                    this@ListFriendOfWallActivity.showShortSuccessToast( getString(R.string.ban_da_huy_ket_ban_voi_s, nameItem))
                    adapter.removeItem(positionList!!)
                    setResult(RESULT_OK)
                }
            })

            viewModel.followOrUnFollow.observe(this, Observer {
                lifecycleScope.launch {
                    delay(100)
                    this@ListFriendOfWallActivity.showShortSuccessToast( getString(R.string.ban_da_bo_theo_doi_s, nameItem))
                }
            })

            viewModel.listReport.observe(this, Observer {
                dialog = ReportDialog(it, R.string.bao_cao_nguoi_dung_nay, R.string.mo_ta_noi_dung_bao_cao)
                dialog.show(supportFragmentManager, dialog.tag)

                dialog.setListener(object : ReportDialog.DialogClickListener {
                    override fun buttonClick(position: Int, listReason: MutableList<Int>, message: String, listMessage: MutableList<String>) {
                        viewModel.sendReportuser(itemId, listReason, listMessage, message)
                    }
                })
            })

            viewModel.reportSuccess.observe(this, Observer {
                dialog.dismiss()
                val dialogFragment = ReportSuccessDialog(this)
                dialogFragment.show(it, "")
            })

            viewModel.statusCode.observe(this, Observer {
                when (it) {
                    ICMessageEvent.Type.ON_NO_INTERNET -> {
                        DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                            override fun onDisagree() {
                            }

                            override fun onAgree() {
                                viewModel.getListFriend(uid = intent.getLongExtra(USER_ID, 0L))
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

            viewModel.errorDataPut.observe(this, Observer {
                when (it) {
                    Constant.ERROR_SERVER -> {
                        showShortErrorToast(resources.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }

                    Constant.ERROR_INTERNET -> {
                        showShortErrorToast(resources.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                    }

                    Constant.ERROR_UNKNOW -> {
                        showShortErrorToast(resources.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }
            })

            viewModel.errorData.observe(this, Observer {
                tv_total_friend.visibility = View.GONE
                when (it) {
                    Constant.ERROR_EMPTY_SEARCH -> {
                        adapter.setErrorCode(Constant.ERROR_EMPTY_SEARCH)
                    }

                    Constant.ERROR_EMPTY -> {
                        adapter.setErrorCode(Constant.ERROR_EMPTY)
                    }

                    Constant.ERROR_SERVER -> {
                        adapter.setErrorCode(Constant.ERROR_SERVER)
                    }

                    Constant.ERROR_INTERNET -> {
                        adapter.setErrorCode(Constant.ERROR_INTERNET)
                    }

                    Constant.ERROR_UNKNOW -> {
                        adapter.setErrorCode(Constant.ERROR_SERVER)
                    }
                }
            })
        }
    }

    override fun onLoadMore() {
        runOnUiThread {
            viewModel.getListFriend(key, true, uid = intent.getLongExtra(USER_ID, 0L))
        }
    }

    override fun onRefresh() {
        runOnUiThread {
            viewModel.getListFriend(uid = intent.getLongExtra(USER_ID, 0L))
        }
    }

    override fun clickUser(item: ICUserFollowWall) {
        KeyboardUtils.hideSoftInput(this)
        IckUserWallActivity.create(item.id, this)
    }

    override fun goToChat(item: ICUserFollowWall) {
        ChatSocialDetailActivity.createRoomChat(this@ListFriendOfWallActivity, item.id ?: -1, "user")
    }

    override fun goToAddFriend(item: ICUserFollowWall, position: Int) {
        if (item.id != null) {
            positionList = position
            viewModel.friendInvitation(item.id!!, null)
        } else {
            showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }

    override fun goToAcceptFriend(item: ICUserFollowWall, position: Int) {
        if (item.id != null) {
            positionList = position
            viewModel.friendInvitation(item.id!!, 2)
        } else {
            showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }

    override fun goToRefuseFriend(item: ICUserFollowWall, position: Int) {
        if (item.id != null) {
            positionList = position
            viewModel.friendInvitation(item.id!!, 3)
        } else {
            showShortError(R.string.co_loi_xay_ra_vui_long_thu_lai)
        }
    }

    override fun showBottomSheetMoreAction(item: ICUserFollowWall, position: Int) {
        this.forceHideKeyboard()

        positionList = position
        nameItem = item.getUserName()
        itemId = item.id

        object : MoreListFriendDialog(this, item) {
            override fun onClickUnfollow() {
                dialog.dismiss()
                viewModel.unFollowFriend(item.id)
            }

            override fun onClickUnFriend() {
                dialog.dismiss()
                showDialogConfirmUnFriend(item)
            }

            override fun onClickReportUser() {
                dialog.dismiss()
                viewModel.getWrongReport()
            }
        }.show()
    }

    private fun showDialogConfirmUnFriend(item: ICUserFollowWall) {
        object : ConfirmDialog(this@ListFriendOfWallActivity, getString(R.string.ban_co_chac_chan_huy_ket_ban_voi_s_chu, item.getUserName()), null, getString(R.string.de_sau), getString(R.string.dong_y), true) {
            override fun onDisagree() {
                dismiss()
            }

            override fun onAgree() {
                viewModel.unFriend(item.id)
                dismiss()
            }

            override fun onDismiss() {

            }
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }
}