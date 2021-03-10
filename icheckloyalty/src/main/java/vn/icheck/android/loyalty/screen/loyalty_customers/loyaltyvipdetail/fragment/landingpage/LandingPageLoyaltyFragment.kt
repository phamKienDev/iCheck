package vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.fragment.landingpage

import android.annotation.SuppressLint
import kotlinx.android.synthetic.main.fragment_landing_page.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame

class LandingPageLoyaltyFragment(private val url: String) : BaseFragmentGame() {
    override val getLayoutID: Int
        get() = R.layout.fragment_landing_page

    @SuppressLint("SetJavaScriptEnabled")
    override fun onInitView() {
        tvDescription.settings.javaScriptEnabled = true
        tvDescription.loadData(url, "text/html; charset=utf-8", "UTF-8")
    }
}