package vn.icheck.android.screen.user.suggest_topic

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_suggest_topic.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.network.models.ICSuggestTopic
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.suggest_page.SuggestPageActivity
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.kotlin.StatusBarUtils

class SuggestTopicActivity : BaseActivityMVVM(), ISuggestTopicView {

    lateinit var adapter: SuggestTopicAdapter
    lateinit var viewModel: SuggestTopicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_topic)
        viewModel = ViewModelProvider(this).get(SuggestTopicViewModel::class.java)

        initView()
        initRecyclerView()
        listenerData()
        DialogHelper.showLoading(this)
    }

    private fun initView() {
        StatusBarUtils.setOverStatusBarDark(this)
        textView25.background=vn.icheck.android.ichecklibs.ViewHelper.bgWhiteCornersTop16(this)
        btn_skip.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                finish()
                startClearTopActivity(HomeActivity::class.java)
            }
        }
    }

    private fun initRecyclerView() {
        adapter = SuggestTopicAdapter(this)
        rcv_suggest_topic.layoutManager = CustomGridLayoutManager(this, 3, RecyclerView.VERTICAL, false, true).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getListData.isEmpty()) {
                        3
                    } else {
                        1
                    }
                }
            }
        }
        rcv_suggest_topic.adapter = adapter
    }

    private fun listenerData() {
        viewModel.getListTopic()

        viewModel.onError.observe(this, Observer {
            DialogHelper.closeLoading(this)
            if (!adapter.isEmpty) {
                showShortErrorToast(it.message)
            } else {
                textView25.text = ""
                viewShadow.beVisible()
                btn_skip.layoutParams = ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                btn_tiep_tuc.beGone()
                it.message?.let { it1 -> adapter.setError(it.icon, it1, R.string.thu_lai) }
            }

        })

        viewModel.onSetTopicData.observe(this, Observer {
            DialogHelper.closeLoading(this)
            textView25.text = getString(R.string.chon_it_nhat_3_chu_de)
            viewShadow.beVisible()
            btn_skip.beVisible()
            btn_tiep_tuc.beVisible()
            adapter.setListData(it)
        })

        viewModel.onAddTopicData.observe(this, Observer {
            adapter.addListData(it)
        })
    }

    override fun onGetListTopicSelected(list: MutableList<ICSuggestTopic>) {
        if (list.size >= 3) {
            btn_tiep_tuc.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(this@SuggestTopicActivity)
            btn_tiep_tuc.isEnabled = true
        } else {
            btn_tiep_tuc.background = ContextCompat.getDrawable(this, R.drawable.bg_darkgray2_corners_4)
            btn_tiep_tuc.isEnabled = false
        }

        btn_tiep_tuc.setOnClickListener {
            val listId = arrayListOf<Int>()
            for (i in list) {
                i.id?.let { it1 -> listId.add(it1) }
            }

            val intent = Intent(this, SuggestPageActivity::class.java)
            intent.putIntegerArrayListExtra(Constant.DATA_1, listId)
            startActivity(intent)
            overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }
    }

    override fun onMessageClicked() {
        DialogHelper.showLoading(this)
        viewModel.getListTopic()
    }

    override fun onLoadMore() {
        viewModel.getListTopic(true)
    }
}
