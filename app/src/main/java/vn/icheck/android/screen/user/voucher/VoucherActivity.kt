package vn.icheck.android.screen.user.voucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM

class VoucherActivity : BaseActivityMVVM() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voucher)

        initView()
    }

    private fun initView(){
        txtTitle.text = "Siêu hội Voucher"

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}
