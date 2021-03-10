package vn.icheck.android.screen.user.detail_stamp_v5.more_business_v5

import android.Manifest
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_more_business_v5.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectBusinessV6
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectOtherProductV6
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_v5.more_business_v5.adapter.MoreProductVerifiedInBusinessV5Adapter
import vn.icheck.android.screen.user.detail_stamp_v5.more_business_v5.presenter.MoreBusinessV5Presenter
import vn.icheck.android.screen.user.detail_stamp_v5.more_business_v5.view.IMoreBusinessV5View
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.ui.layout.CustomGridLayoutManager
import vn.icheck.android.util.kotlin.ContactUtils

class MoreBusinessV5Activity : BaseActivity<MoreBusinessV5Presenter>(), IMoreBusinessV5View {

    override val getLayoutID: Int
        get() = R.layout.activity_more_business_v5

    override val getPresenter: MoreBusinessV5Presenter
        get() = MoreBusinessV5Presenter(this)

    private var adapterSuggestion = MoreProductVerifiedInBusinessV5Adapter(this)

    private val requestPhone = 1

    override fun onInitView() {
        initRecyclerViewMoreProduct()
        presenter.getDataIntent(intent)
        listener()
    }

    private fun listener() {
        layoutPhone.setOnClickListener {
            if (tvPhone.text.toString() != getString(R.string.dang_cap_nhat)) {
                if (PermissionHelper.checkPermission(this, Manifest.permission.CALL_PHONE, requestPhone)) {
                    ContactUtils.callFast(this@MoreBusinessV5Activity, tvPhone.text.toString())
                }
            }
        }

        layoutMail.setOnClickListener {
            if (tvEmail.text.toString() != getString(R.string.dang_cap_nhat)) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, tvEmail.text.toString())
                startActivity(Intent.createChooser(intent, "Send To"))
            }
        }

        layoutWeb.setOnClickListener {
            if (tvWebsite.text.toString() != getString(R.string.dang_cap_nhat)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tvWebsite.text.toString()))
                startActivity(intent)
            }
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerViewMoreProduct() {
        adapterSuggestion = MoreProductVerifiedInBusinessV5Adapter(this)
        val layoutManager = CustomGridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false, false, false)
        rcvProductVerified.layoutManager = layoutManager

        val verticalDecoration = DividerItemDecoration(rcvProductVerified.context,DividerItemDecoration.HORIZONTAL)
        val verticalDivider = ContextCompat.getDrawable(this,R.drawable.vertical_divider_more_business_stamp) as Drawable
        verticalDecoration.setDrawable(verticalDivider)
        rcvProductVerified.addItemDecoration(verticalDecoration)

        val horizontalDecoration = DividerItemDecoration(rcvProductVerified.context,DividerItemDecoration.VERTICAL)
        val horizontalDivider  = ContextCompat.getDrawable(this,R.drawable.horizontal_divider_more_business_stamp) as Drawable
        horizontalDecoration.setDrawable(horizontalDivider)
        rcvProductVerified.addItemDecoration(horizontalDecoration)

        rcvProductVerified.adapter = adapterSuggestion

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapterSuggestion.listData.isNullOrEmpty()) {
                    2
                } else {
                    1
                }
            }
        }
    }

    override fun onGetDataIntentVendorSuccess(item: ICBarcodeProductV1.VendorPage) {
        txtTitle.text = if (!item.name.isNullOrEmpty()) item.name else getString(R.string.dang_cap_nhat)
        tvName.text = if (!item.name.isNullOrEmpty()) item.name else getString(R.string.dang_cap_nhat)
        tvPhone.text = if (!item.phone.isNullOrEmpty()) item.phone else getString(R.string.dang_cap_nhat)
        tvAddress.text = if (!item.address.isNullOrEmpty()) item.address else getString(R.string.dang_cap_nhat)
        tvEmail.text = if (!item.email.isNullOrEmpty()) item.email else getString(R.string.dang_cap_nhat)
        tvWebsite.text = if (!item.website.isNullOrEmpty()) item.website else getString(R.string.dang_cap_nhat)
    }

    override fun onGetDataIntentDistributorSuccess(item: ICObjectBusinessV6, otherProduct: MutableList<ICObjectOtherProductV6>?) {
        txtTitle.text = if (!item.company_name.isNullOrEmpty()) item.company_name else getString(R.string.dang_cap_nhat)
        tvName.text = if (!item.company_name.isNullOrEmpty()) item.company_name else getString(R.string.dang_cap_nhat)
        tvPhone.text = if (!item.phone.isNullOrEmpty()) item.phone else getString(R.string.dang_cap_nhat)
        tvAddress.text = if (!item.address.isNullOrEmpty()) item.address else getString(R.string.dang_cap_nhat)
        tvEmail.text = if (!item.company_email.isNullOrEmpty()) item.company_email else getString(R.string.dang_cap_nhat)
        tvWebsite.text = if (!item.company_website.isNullOrEmpty()) item.company_website else getString(R.string.dang_cap_nhat)

        if (otherProduct.isNullOrEmpty()) {
            adapterSuggestion.setError(Constant.ERROR_EMPTY)
        } else {
            adapterSuggestion.setListData(otherProduct)
        }
    }

    override fun onClickItem(item: ICObjectOtherProductV6) {
        if (!item.sku.isNullOrEmpty()) {
            IckProductDetailActivity.start(this, item.sku!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPhone) {
            if (PermissionHelper.checkResult(grantResults)) {
                ContactUtils.callFast(this@MoreBusinessV5Activity, tvPhone.text.toString())
            } else {
                showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}
