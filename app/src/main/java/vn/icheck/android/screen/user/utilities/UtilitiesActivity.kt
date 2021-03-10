package vn.icheck.android.screen.user.utilities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_all_utilitys.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.util.kotlin.ToastUtils

class UtilitiesActivity : AppCompatActivity(), IRecyclerViewCallback {
    lateinit var viewModel: UtilitiesViewModel
    lateinit var adapter: UtilitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_utilitys)

        viewModel = ViewModelProvider(this).get(UtilitiesViewModel::class.java)
        initView()
        initRecyclerView()
        listenerData()
    }

    private fun initView() {
        toolbar.setBackgroundColor(Color.WHITE)
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.text = getString(R.string.tat_ca_tien_ich)
    }

    private fun initRecyclerView() {
        adapter = UtilitiesAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun listenerData() {
        viewModel.onSetData.observe(this, Observer{
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer{
            adapter.addListData(it)
        })

        viewModel.onError.observe(this, Observer{
            if (adapter.getListData.isNotEmpty()) {
                ToastUtils.showShortError(this, it.message)
            } else {
                it.message?.let { message -> adapter.setError(it.icon, message, R.string.thu_lai) }
            }
        })
        viewModel.onState.observe(this, Observer{
            when(it.type){
                ICMessageEvent.Type.ON_SHOW_LOADING->{
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING->{
                    DialogHelper.closeLoading(this)
                }

            }
        })

        viewModel.getAllUtility()
    }

    override fun onMessageClicked() {
        viewModel.getAllUtility()
    }

    override fun onLoadMore() {
        viewModel.getAllUtility(true)
    }
}