package vn.icheck.android.screen.user.list_trending_products

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.util.ick.simpleStartActivity

class ListTrendingProductsActivity : BaseActivityMVVM() {

    companion object{

        fun create(activity: Activity, categoryId: Long? = null) {
            val i = Intent(activity, ListTrendingProductsActivity::class.java)
            i.putExtra("categoryId", categoryId)
            activity.startActivity(i)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_trending_products)
    }
}