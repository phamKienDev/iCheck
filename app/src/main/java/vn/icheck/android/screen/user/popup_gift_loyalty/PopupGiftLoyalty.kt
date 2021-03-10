package vn.icheck.android.screen.user.popup_gift_loyalty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_popup_gift_loyalty.*
import vn.icheck.android.R
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.home.MyGiftWareHouseActivity

class PopupGiftLoyalty: AppCompatActivity() {
    companion object {
        fun start(title: String?, message: String?, context: Context?) {
            val start = android.content.Intent(context, PopupGiftLoyalty::class.java)
            start.putExtra("title", title)
            start.putExtra("message", message)
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            start.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context?.startActivity(start)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_gift_loyalty)

        linearLayout.visibility = View.VISIBLE

        val intent = intent

        tvTitle.text = intent.getStringExtra("title")
        tvMess1.text = intent.getStringExtra("message")

        btnClose.setOnClickListener {
            linearLayout.visibility = View.GONE
            finish()
            this.overridePendingTransition(0, 0)
        }

        tvXemKhoQua.setOnClickListener {
            val intent = android.content.Intent(this, MyGiftWareHouseActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.overridePendingTransition(0, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}