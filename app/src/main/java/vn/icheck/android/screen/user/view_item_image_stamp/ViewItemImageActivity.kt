package vn.icheck.android.screen.user.view_item_image_stamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_view_item_image.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.kotlin.WidgetUtils

class ViewItemImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item_image)

        val image = intent.getStringExtra(Constant.DATA_1)
        WidgetUtils.loadImageFitCenterUrl(imageView, image)

        tvClose.setOnClickListener {
            onBackPressed()
        }
    }
}