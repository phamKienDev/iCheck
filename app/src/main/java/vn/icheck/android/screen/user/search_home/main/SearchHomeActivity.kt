package vn.icheck.android.screen.user.search_home.main

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.component.tag_view.Tag
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.search_home.result.SearchResultActivity
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.concurrent.TimeUnit


class SearchHomeActivity : BaseActivityMVVM(), View.OnClickListener, IRecyclerViewSearchCallback {

    lateinit var adapter: SearchHomeAdapter
    private var dispose: Disposable? = null

    lateinit var viewModel: SearchHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(SearchHomeViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_v2)
        
        initView()
        initRecyclerView()
        initEditText()
        listenerData()
        viewModel.getData()
    }

    private fun initView() {
        getData(edtSearch.text.toString())

        dispose = RxTextView.textChangeEvents(edtSearch)
                .skipInitialValue()
                .distinctUntilChanged()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { key ->
                    getData(key.text().toString())
                }

        WidgetUtils.setClickListener(this, tv_cancel, img_clear, img_delete_recent)
        tag_view_recent.setOnTagClickListener { i, tagClick ->
            clickTagView(tagClick.text.toString())
        }

        tag_view_popular.setOnTagClickListener { i, tagClick ->
            clickTagView(tagClick.text.toString())
        }

        edtSearch.background= ViewHelper.bgGrayCorners4(this)
    }


    private fun initRecyclerView() {
        adapter = SearchHomeAdapter(this)
        adapter.disableLoadMore()
        rcv_search.adapter = adapter
    }

    private fun initEditText() {
        edtSearch.requestFocus()
        KeyboardUtils.showSoftInput(edtSearch)
        edtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (edtSearch.text.toString().trim().isNotEmpty()) {
                    KeyboardUtils.hideSoftInput(v)
                    startActivity<SearchResultActivity>(Constant.DATA_1, edtSearch.text.toString())
                    edtSearch.setText("")
                }
                true
            } else {
                false
            }
        }
    }

    private fun listenerData() {
        viewModel.onRelatedSearch.observe(this, {
            adapter.setListData(it)
        })

        viewModel.onError.observe(this, {
            it.message?.let { message ->
                if (it.subMessage.isNullOrEmpty())
                    adapter.setError(it.icon, message, R.string.thu_lai)
                else
                    showShortError(message)
            }
        })

        viewModel.onRecentSearch.observe(this, {
            addListSearchRecent()
        })

        viewModel.onDeleteRecentSearch.observe(this, {
            addListSearchRecent()
        })

        viewModel.onPopularSearch.observe(this, {
            addListSearchPopular()
        })

        viewModel.onStatus.observe(this, {
            if (it == ICMessageEvent.Type.ON_SHOW_LOADING) {
                DialogHelper.showLoading(this)
            } else {
                DialogHelper.closeLoading(this)
            }
        })
    }

    private fun getData(key: String) {
        if (key.isEmpty()) {
            rcv_search.visibility = View.GONE
            img_clear.visibility = View.GONE

            addListSearchRecent()
            addListSearchPopular()
        } else {
            container_popular.visibility = View.GONE
            container_recent.visibility = View.GONE
            rcv_search.visibility = View.VISIBLE
            img_clear.visibility = View.VISIBLE

            viewModel.getAutoSearch(edtSearch.text.toString())
        }
    }

    private fun clickTagView(key: String) {
        KeyboardUtils.hideSoftInput(this)
        startActivity<SearchResultActivity>(Constant.DATA_1, key)
        edtSearch.setText("")
    }

    private fun addListSearchRecent() {
        if (viewModel.listRecent.isNullOrEmpty() || edtSearch.text.toString().isNotEmpty()) {
            container_recent.visibility = View.GONE
        } else {
            container_recent.visibility = View.VISIBLE

            val listTag = mutableListOf<Tag>()

            for (i in 0 until viewModel.listRecent.size) {
                val tag = Tag(viewModel.listRecent[i])
                tag.radius = SizeHelper.size4.toFloat()
                tag.layoutColor = resources.getColor(R.color.campaign_space)
                tag.tagTextColor = resources.getColor(R.color.black_blue)
                tag.tagTextSize = 14f
                tag.fontFamily = R.font.barlow_medium
                listTag.add(tag)
            }
            tag_view_recent.removeAllTags()
            tag_view_recent.addTags(listTag)
        }
    }

    private fun addListSearchPopular() {
        if (viewModel.listPopular.isNullOrEmpty() || edtSearch.text.toString().isNotEmpty()) {
            container_popular.visibility = View.GONE
        } else {
            container_popular.visibility = View.VISIBLE

            val listTag = mutableListOf<Tag>()

            for (i in 0 until viewModel.listPopular.size) {
                val tag = Tag(viewModel.listPopular[i])
                tag.radius = SizeHelper.size4.toFloat()
                tag.layoutColor = resources.getColor(R.color.campaign_space)
                tag.tagTextColor = resources.getColor(R.color.black_blue)
                tag.tagTextSize = 14f
                tag.fontFamily = R.font.barlow_medium
                listTag.add(tag)
            }
            tag_view_popular.removeAllTags()
            tag_view_popular.addTags(listTag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose?.dispose()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_cancel -> {
                onBackPressed()
            }
            R.id.img_clear -> {
                edtSearch.setText("")
            }
            R.id.img_delete_recent -> {
                viewModel.deleteRecentSearch()
            }
        }
    }

    override fun onMessageClicked() {
        getData(edtSearch.text.toString())
    }

    override fun onPause() {
        super.onPause()
        KeyboardUtils.hideSoftInput(this)
    }

    override fun onLoadMore() {

    }

    override fun onResume() {
        viewModel.getRecentSearch()
        super.onResume()
    }

    override fun onNotResultClicked() {

    }
}
