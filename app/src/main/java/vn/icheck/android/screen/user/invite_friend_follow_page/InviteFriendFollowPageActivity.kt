package vn.icheck.android.screen.user.invite_friend_follow_page

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_invite_friend2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICFriendNofollowPage
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils
import java.util.concurrent.TimeUnit

class InviteFriendFollowPageActivity : BaseActivityMVVM(), InviteFriendFollowPageCallback {
    lateinit var adapter: InviteFriendFollowPageAdapter
    lateinit var viewModel: InviteFriendViewFollowPageModel

    private val listSelected = mutableListOf<ICUser>()
    private var isInvite = true
    lateinit var dispose: Disposable

    private var preferencesInvite = "preferencesInvite"
    private var keyPreferencesInvite = "keyPreferencesInvite"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var isFirst = true

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_friend2)

        viewModel = ViewModelProvider(this).get(InviteFriendViewFollowPageModel::class.java)
        sharedPreferences = getSharedPreferences(preferencesInvite, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        initView()
        initSwipeLayout()
        initRecyclerView()
        listenerData()
    }

    private fun initSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorPrimary))
        swipeLayout.setOnRefreshListener {
            findListUser(edtSearch.text.toString())
        }
    }

    private fun initView() {
        img_back.setOnClickListener {
            onBackPressed()
        }

        dispose = RxTextView.textChangeEvents(edtSearch)
                .skipInitialValue()
                .distinctUntilChanged()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { key ->
                    findListUser(key.text().toString())
                }

        tvInvite.setOnClickListener {
            viewModel.inivitUserFollowPage(listSelected)
        }

        img_clear.setOnClickListener {
            edtSearch.setText("")
        }
    }

    private fun initRecyclerView() {
        adapter = InviteFriendFollowPageAdapter(this, listSelected)
        rcvContent.adapter = adapter
        rcvContent.layoutManager = LinearLayoutManager(this)
    }

    private fun listenerData() {
        viewModel.getData(intent)

        viewModel.onSetData.observe(this, {
            swipeLayout.isRefreshing = false
            if (!it.rows.isNullOrEmpty()) {
                adapter.addItem(it.also {
                    it.listHideIds = sharedPreferences.getStringSet(keyPreferencesInvite, HashSet<String>())
                    it.pageId = viewModel.pageId
                })
                adapter.addListData(it.rows as MutableList<Any>)
                view46.beVisible()
                tvInvite.beVisible()
            } else {
                if (isFirst) {
                    edtSearch.beGone()
                    adapter.setEmpity(R.drawable.img_user_not_friend, rText(R.string.danh_sach_ban_be_trong), rText(R.string.ket_ban_de_cung_chia_se_nhung_thong_tin_huu_ich_ve_san_pham_chinh_hang_nhe))
                } else {
                    edtSearch.beVisible()
                    adapter.setEmpity(R.drawable.ic_search_90dp, null, rText(R.string.khong_ket_qua_tim_kiem))
                }
                view46.beGone()
                tvInvite.beGone()
            }
            isFirst = false
            checkCountInvite(it)
        })

        viewModel.onAddData.observe(this, {
            adapter.addListData(it as MutableList<Any>)
        })

        viewModel.onState.observe(this, {
            when (it) {
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

        viewModel.onInvitationSuccess.observe(this, {
            DialogHelper.showDialogSuccessBlack(this, rText(R.string.ban_da_gui_loi_moi_thanh_cong))
            Handler().postDelayed({
                finish()
            }, 1800)
        })

        viewModel.onError.observe(this, {
            swipeLayout.isRefreshing = false
            DialogHelper.closeLoading(this)
            if (!adapter.isEmpty) {
                ToastUtils.showShortError(this, it.message)
            } else {
                adapter.setError(it)
            }
        })
    }

    private fun checkCountInvite(it: ICFriendNofollowPage) {
        if (it.invitationCount ?: 0 >= it.maxInvitationCount ?: 0) {
            isInvite = false
            tvInvite.text = getString(R.string.ban_da_vuot_qua_so_lan_moi_trong_ngay)
            tvInvite.setBackgroundResource(R.drawable.bg_gray_b4_corners_4)
            tvInvite.isClickable = false
        }
    }

    private fun findListUser(key: String) {
        if (key.isEmpty()) {
            img_clear.beGone()
        } else {
            img_clear.beVisible()
        }
        listSelected.clear()
        viewModel.getFriendNofollowPage(key)
    }

    override fun getListSeleted(selected: MutableList<ICUser>) {
        if (isInvite) {
            if (!selected.isNullOrEmpty()) {
                tvInvite.background = ContextCompat.getDrawable(this, R.drawable.bg_corners_4_light_blue_solid)
                tvInvite.isEnabled = true
            } else {
                tvInvite.background = ContextCompat.getDrawable(this, R.drawable.bg_gray_b4_corners_4)
                tvInvite.isEnabled = false
            }
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.HIDE_CONTAINER_FOLLOW_PAGE -> {
                val listIds = sharedPreferences.getStringSet(keyPreferencesInvite, HashSet<String>())
                listIds?.add(viewModel.pageId.toString())
                editor.putStringSet(keyPreferencesInvite, listIds)
                editor.commit()
            }
            else -> {
            }
        }
    }

    override fun onMessageClicked() {
        viewModel.getFriendNofollowPage(null)
    }

    override fun onLoadMore() {
        viewModel.getFriendNofollowPage(edtSearch.text.toString(), true)
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.dispose()
    }
}
