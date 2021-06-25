package vn.icheck.android.screen.user.detail_stamp_hoa_phat.infor_product

import android.os.Build
import android.os.Bundle
import android.text.Html
import kotlinx.android.synthetic.main.activity_infor_product_hoa_phat.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.text.HtmlImageGetterStamp

class InforProductHoaPhatActivity : BaseActivityMVVM() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infor_product_hoa_phat)

        val title = try {
            intent?.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            getString(R.string.thong_tin_them)
        }

        val content = try {
            intent?.getStringExtra(Constant.DATA_2)
        } catch (e: Exception) {
            null
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.text = title

        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT, HtmlImageGetterStamp(), null)
        } else {
            Html.fromHtml(content, HtmlImageGetterStamp(), null)
        }
        tvContent.text = html
    }
}