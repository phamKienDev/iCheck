package vn.icheck.android.screen.user.newslistv2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_news.*
import kotlinx.android.synthetic.main.toolbar_blue_v2.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.network.models.ICArticleCategory
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.newslistv2.adapter.NewCategoryAdapter
import vn.icheck.android.screen.user.newslistv2.adapter.NewsListV2Adapter
import vn.icheck.android.screen.user.newslistv2.viewmodel.NewsListViewModel
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.ToastUtils

class ListNewsFragment : BaseFragmentMVVM(), IRecyclerViewCallback {
    private lateinit var viewModel: NewsListViewModel
    private val adapter = NewsListV2Adapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_news, container, false)
    }

    companion object {
        fun newInstance(isShowBack: Boolean, idCategory: Long? = -1): ListNewsFragment {
            val fragment = ListNewsFragment()

            val bundle = Bundle()
            bundle.putBoolean(Constant.DATA_1, isShowBack)
            bundle.putLong(Constant.ID, idCategory ?: -1)
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

        viewModel.idCategory = arguments?.getLong(Constant.ID) ?: -1

        if (arguments?.getBoolean(Constant.DATA_1) == false) {
            imgBack.setImageResource(ViewHelper.setImageColorPrimary(R.drawable.ic_leftmenu_24_px,requireContext()))
            layoutContainer.setPadding(0, getStatusBarHeight, 0, 0)

            imgBack.setOnClickListener {
                activity?.let {
                    if (it is HomeActivity) {
                        it.openSlideMenu()
                    }
                }
            }
        } else {
            imgBack.setImageResource(ViewHelper.setImageColorPrimary(R.drawable.ic_back_blue_v2_24px,requireContext()))

            imgBack.setOnClickListener {
                ActivityUtils.finishActivity(requireActivity())
            }
        }

        txtTitle.setText(R.string.icheck_thoi_bao)

        viewModel.getCategoryNewsList()
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
        viewModel.getNewsList(false, viewModel.idCategory)
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

        viewModel.getCategorySuccess.observe(viewLifecycleOwner, {
            val adapterCategory = NewCategoryAdapter(it)
            recyclerViewCategory.adapter = adapterCategory

            adapterCategory.setListener(object : IClickListener {
                override fun onClick(obj: Any) {
                    if (obj is ICArticleCategory) {
                        viewModel.idCategory = obj.id ?: -1

                        getData()
                    }
                }
            })
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
        viewModel.getNewsList(true, viewModel.idCategory)
    }
}