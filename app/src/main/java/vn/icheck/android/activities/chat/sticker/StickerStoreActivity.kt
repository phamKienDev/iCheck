package vn.icheck.android.activities.chat.sticker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R

class StickerStoreActivity : AppCompatActivity() {

    lateinit var stickerViewModel: StickerViewModel
    lateinit var stickerAdapter: StickerAdapter
    var requestCode: Int = 0

    companion object {
        fun start(activity: Activity, requestCode: Int) {
            val i = Intent(activity, StickerStoreActivity::class.java)
            activity.startActivityForResult(i, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticker_store)
        stickerViewModel = ViewModelProvider(this).get(StickerViewModel::class.java)
        val rcv = findViewById<RecyclerView>(R.id.rcv_sticker_store)
        stickerViewModel.stickerPackage.observe(this, Observer {
            val arr = arrayListOf<StickerView>()
            for (item in it.data) {
                arr.add(StickerView(item.id, item.thumbnail).apply {
                    this.total = item.count
                    this.name = item.name
                })
            }
            stickerAdapter = StickerAdapter(arr, 3)
            rcv.adapter = stickerAdapter
            rcv.layoutManager = LinearLayoutManager(this)
        })
    }

    fun onBack(v: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}
