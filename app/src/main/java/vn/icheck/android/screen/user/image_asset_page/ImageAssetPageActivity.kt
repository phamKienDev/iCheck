package vn.icheck.android.screen.user.image_asset_page

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_image_asset_page.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICMediaPage
import vn.icheck.android.screen.user.media_in_post.MediaInPostActivity
import vn.icheck.android.ichecklibs.util.showShortErrorToast

class ImageAssetPageActivity : BaseActivityMVVM(), IImageAssetPageView {

    private lateinit var viewModel: ImageAssetPageViewmodel
    private var adapter = ImageAssetPageAdapter(this)

    private var idPage = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_asset_page)
        viewModel = ViewModelProvider(this).get(ImageAssetPageViewmodel::class.java)
        initView()
        initRecyleview()
        listenerGetData()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        txtTitle.text = "Ảnh của " + intent.getStringExtra(Constant.DATA_1)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyleview() {
        val manager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.listData.isNullOrEmpty()) {
                    3
                } else {
                    1
                }
            }
        }
    }

    private fun listenerGetData() {
        adapter.disableLoadMore()

        if (intent.getLongExtra(Constant.DATA_2, 0L) != 0L) {
            idPage = intent.getLongExtra(Constant.DATA_2, 0L)
            viewModel.onGetImagePage(idPage)
        }

        viewModel.isLoadMoreData.observe(this,  {
            adapter.removeDataWithoutUpdate()
        })

        viewModel.listData.observe(this,  {
            adapter.addListData(it)
        })

        viewModel.statusCode.observe(this,  {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this@ImageAssetPageActivity, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                        }

                        override fun onAgree() {
                            viewModel.onGetImagePage(idPage)
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

        viewModel.errorData.observe(this, {
            when (it) {
                Constant.ERROR_EMPTY -> {
                    if (!adapter.isListNotEmpty)
                        adapter.setErrorCode(Constant.ERROR_EMPTY)
                }

                Constant.ERROR_SERVER -> {
                    if (adapter.isListNotEmpty){
                        showShortErrorToast(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }else{
                        adapter.setErrorCode(Constant.ERROR_SERVER)

                    }
                }

                Constant.ERROR_INTERNET -> {
                    if (adapter.isListNotEmpty) {
                        showShortErrorToast(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    }else{
                        adapter.setErrorCode(Constant.ERROR_INTERNET)

                    }
                }
            }
        })
    }

    override fun onClickImage(item: ICMediaPage) {
        item.postId?.let { MediaInPostActivity.start(it, this) }
    }

    override fun onLoadMore() {
        viewModel.onGetImagePage(idPage, true)
    }

    override fun onRefresh() {
        viewModel.onGetImagePage(idPage)
        adapter.disableLoadMore()
    }
}