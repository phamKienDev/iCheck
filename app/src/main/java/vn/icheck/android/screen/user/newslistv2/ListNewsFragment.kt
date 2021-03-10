package vn.icheck.android.screen.user.newslistv2

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_news.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.newslistv2.adapter.NewsListV2Adapter
import vn.icheck.android.screen.user.newslistv2.viewmodel.NewsListViewModel
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils

class ListNewsFragment : BaseFragmentMVVM(), IRecyclerViewCallback {
    private lateinit var viewModel: NewsListViewModel
    private val adapter = NewsListV2Adapter(this)

    override val getLayoutID: Int
        get() = R.layout.fragment_list_news

    companion object {
        fun newInstance(isShowBack: Boolean): ListNewsFragment {
            val fragment = ListNewsFragment()

            val bundle = Bundle()
            bundle.putBoolean(Constant.DATA_1, isShowBack)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRecyclerView()
        initSwipe()
        getDataSuccess()
        getDataError()
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[NewsListViewModel::class.java]

        if (arguments?.getBoolean(Constant.DATA_1) == false) {
            imgBack.setImageResource(R.drawable.ic_leftmenu_24_px)
            layoutContainer.setPadding(0, getStatusBarHeight, 0, 0)

            imgBack.setOnClickListener {
                activity?.let {
                    if (it is HomeActivity) {
                        it.openSlideMenu()
                    }
                }
            }
        } else {
            imgBack.setImageResource(R.drawable.ic_back_blue_v2_24px)

            imgBack.setOnClickListener {
                ActivityUtils.finishActivity(requireActivity())
            }
        }

        txtTitle.setText(R.string.icheck_thoi_bao)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun initSwipe() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }

        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        if (swipeLayout != null) {
            swipeLayout.isRefreshing = true
        }
        viewModel.getNewsList(false)
    }

    private fun getDataSuccess() {
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            if (!it.isLoadMore) {
                adapter.setData(it.listData)
            } else {
                adapter.addData(it.listData)
            }
        })
    }

    private fun getDataError() {
        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(requireContext(), it.message)
            }
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getNewsList(true)
    }
}