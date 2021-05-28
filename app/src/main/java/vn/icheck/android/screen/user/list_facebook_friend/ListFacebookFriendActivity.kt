package vn.icheck.android.screen.user.list_facebook_friend

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_list_facebook_friend.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import java.util.concurrent.TimeUnit

class ListFacebookFriendActivity : BaseActivityMVVM(), IRecyclerViewCallback {
    lateinit var adapter: ListFacebookFriendAdapter
    lateinit var viewModel: ListFacebookFriendViewModel

    var dispose: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_facebook_friend)

        viewModel = ViewModelProvider(this).get(ListFacebookFriendViewModel::class.java)
        initView()
        initSwipeLayout()
        initRecylerView()
        listenerData()
    }


    private fun initView() {
        imgBack.setImageResource(R.drawable.ic_back_blue_v2_24px)
        imgBack.setOnClickListener {
            onBackPressed()
        }
        img_clear.setOnClickListener {
            edtFind.setText("")
        }
        getData()

        txtTitle.text = getString(R.string.danh_sach_ban_be_facebook)
        txtTitle.typeface = ViewHelper.createTypeface(this, R.font.barlow_semi_bold)

        edtFind.setText(edtFind.text.toString())
        dispose = RxTextView.afterTextChangeEvents(edtFind)
                .skipInitialValue()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getData()
                }

    }

    private fun initSwipeLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorSecondary), ContextCompat.getColor(this, R.color.colorPrimary))
        swipeLayout.setOnRefreshListener {
            getData()
        }
    }

    private fun initRecylerView() {
        adapter = ListFacebookFriendAdapter(this)
        rcvList.adapter = adapter
    }

    fun getData() {
        if (edtFind.text.toString().isEmpty()) {
            img_clear.beGone()
        } else {
            img_clear.beVisible()
        }
        swipeLayout.isRefreshing = true
        viewModel.getListFriend(edtFind.text.toString())
    }


    private fun listenerData() {
        viewModel.onSetData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            if (!it.rows.isNullOrEmpty()) {
                tvCount.text = "${TextHelper.formatMoneyComma(it.count)} Bạn bè"
                adapter.setListData(it.rows)
            } else {
                tvCount.text = ""
                adapter.setError(R.drawable.ic_search_90dp, getString(R.string.khong_ket_qua_tim_kiem), -1)
            }
        })

        viewModel.onAddData.observe(this, Observer {
            adapter.addListData(it)
        })

        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty) {
                it.message?.let { it1 -> adapter.setError(it.icon, it1, R.string.thu_lai) }
            } else {
                showShortErrorToast(it.message)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        dispose = null
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getListFriend(edtFind.text.toString(), true)
    }
}