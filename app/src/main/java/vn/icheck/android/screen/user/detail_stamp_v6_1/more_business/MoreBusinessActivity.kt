package vn.icheck.android.screen.user.detail_stamp_v6_1.more_business

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_more_business.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectDistributor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectVendor
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.adapter.MoreProductVerifiedInBusinessAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.presenter.MoreBusinessPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.view.IMoreBusinessView
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.kotlin.ContactUtils

class MoreBusinessActivity : BaseActivityMVVM(), IMoreBusinessView {

    val presenter = MoreBusinessPresenter(this@MoreBusinessActivity)

    private var adapterSuggestion = MoreProductVerifiedInBusinessAdapter(this,StampDetailActivity.isVietNamLanguage)

    private var objVendor: ICObjectVendor? = null
    private var objDistributor: ICObjectDistributor? = null

    private var phone: String? = null

    private var mId: Long? = null
    private val requestPhone = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_business)
        onInitView()
    }

    fun onInitView() {
        presenter.getDataIntent(intent)
        initRecyclerViewMoreProduct()
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerViewMoreProduct() {
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

//        val verticalDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
//        val verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider_more_business_stamp) as Drawable
//        verticalDecoration.setDrawable(verticalDivider)
//        recyclerView.addItemDecoration(verticalDecoration)
//
//        val horizontalDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
//        val horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider_more_business_stamp) as Drawable
//        horizontalDecoration.setDrawable(horizontalDivider)
//        recyclerView.addItemDecoration(horizontalDecoration)

        recyclerView.adapter = adapterSuggestion

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == adapterSuggestion.listData.size) {
                    2
                } else {
                    if (position < adapterSuggestion.listData.size && adapterSuggestion.listData[position] == null) {
                        2
                    } else {
                        1
                    }
                }
            }
        }
    }

    override fun onClickPhone(phone: String) {
        this.phone = phone

        if (phone != getString(R.string.dang_cap_nhat)) {
            if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                ContactUtils.callFast(this@MoreBusinessActivity, phone)
            }
        }
    }

    override fun onClickEmail(email: String) {
        if (email != getString(R.string.dang_cap_nhat)) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            startActivity(Intent.createChooser(intent, "Send To"))
        }
    }

    override fun onClickWebsite(website: String) {
        if (website != getString(R.string.dang_cap_nhat)) {
            var webpage = Uri.parse(website)

            if (!website.startsWith("http://") && !website.startsWith("https://")) {
                webpage = Uri.parse("http://${website}")
            }

            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    override fun showError(errorMessage: String) {
        showLongErrorToast(errorMessage)
    }

    override val mContext: Context
        get() = this@MoreBusinessActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@MoreBusinessActivity, isShow)
    }

    override fun onLoadMore() {
        if (objVendor != null) {
            presenter.onGetMoreDataVendor(mId, true)
        } else {
            presenter.onGetMoreDataDistributor(mId, true)
        }
    }

    override fun onItemClick(item: ICObjectListMoreProductVerified) {
        if (!item.sku.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, item.sku!!)
        }
    }

    override fun onClickTryAgain() {
        if (objVendor != null) {
            presenter.onGetMoreDataVendor(mId)
        } else {
            presenter.onGetMoreDataDistributor(mId)
        }
    }

    override fun onGetDataIntentVendorSuccess(itemVendor: ICObjectVendor) {
        objVendor = itemVendor

        mId = itemVendor.id

        adapterSuggestion.addHeaderVendor(objVendor, null)

        txtTitle.text = itemVendor.name
    }

    override fun onGetDataIntentDistributorSuccess(itemDistributor: ICObjectDistributor) {
        objDistributor = itemDistributor

        mId = itemDistributor.id

        adapterSuggestion.addHeaderVendor(null, objDistributor)

        txtTitle.text = itemDistributor.name
    }

    override fun onGetDataProductVerifiedDistributorSuccess(products: MutableList<ICObjectListMoreProductVerified>, loadMore: Boolean) {
        if (loadMore)
            adapterSuggestion.addListData(products)
        else
            adapterSuggestion.setListData(products)
    }

    override fun onGetDataIntentError(typeError: Int) {
        when (typeError) {
            Constant.ERROR_EMPTY -> {
                adapterSuggestion.setError(Constant.ERROR_EMPTY)
            }

            Constant.ERROR_INTERNET -> {
                adapterSuggestion.setError(Constant.ERROR_INTERNET)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                phone?.let { ContactUtils.callFast(this@MoreBusinessActivity, it) }
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}
