package vn.icheck.android.screen.user.mygift.fragment.my_gift

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_my_gift.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.list_box_gift.ListShakeGridBoxActivity
import vn.icheck.android.util.kotlin.ToastUtils

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */
class MyGiftFragment : BaseFragmentMVVM() {
    companion object {
        var INSTANCE: MyGiftFragment? = null
        var count = 0
    }

    lateinit var viewModel: MyGiftViewModel
    val adapter = MyGiftAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_gift, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        INSTANCE = this
        viewModel = ViewModelProvider(this).get(MyGiftViewModel::class.java)

        initRecyclerView()
        initSwipeLayout()
        initListener()

    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        adapter.setOnClickItemListener(object :ItemClickListener<ICItemReward>{
            override fun onItemClick(position: Int, item: ICItemReward?) {
                val intent = Intent(context,ListShakeGridBoxActivity::class.java)
                intent.putExtra(Constant.DATA_1,item?.id)
                intent.putExtra(Constant.DATA_2,1)
                startActivity(intent)
            }
        })
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListMyGiftBox(false)
    }

    private fun initListener() {
        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            if (adapter.isEmpty()) {
                it.message?.let { it1 -> adapter.setError(it1, it.icon) }
            } else {
                ToastUtils.showLongError(context, it.message)
            }
        })
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            tvCountGift.text = "Hộp quà: ${it.count}"

            if (!it.isLoadMore) {
                adapter.setData(it.listRewardItem)
            } else {
                adapter.addData(it.listRewardItem)
            }
        })
    }

    fun onLoadMore() {
        viewModel.getListMyGiftBox(true)
    }
    
}