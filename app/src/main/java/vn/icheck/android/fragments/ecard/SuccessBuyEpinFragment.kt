package vn.icheck.android.fragments.ecard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_success_epin.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICBuyEpin
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.ichecklibs.util.setText

class SuccessBuyEpinFragment : Fragment() {

    private var code: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_success_epin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getSerializable("data")?.let { data ->
                tv_serial.text = (data as ICBuyEpin.EpinData).serial
                tv_code.text = data.pin
                code = data.pin
                tv_total.setText(R.string.d_vnd, data.denomination.toLong())
                tv_msp.text = it.getString("msp")
                it.getString("msp")?.let { safe ->
                    tv_notify.setText(R.string.quy_khach_da_mua_thanh_cong_ma_the_dien_thoai_s, safe)
                }
            }
        }
        btn_charge.setOnClickListener {
            code?.let {
                val chargePhone = Intent(Intent.ACTION_DIAL)
                chargePhone.data = Uri.parse("tel:" + Uri.encode("*100*$it#"))
                startActivity(chargePhone)
            }
        }
        imageView30.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.so_serie), tv_serial.text)
            clipboard.setPrimaryClip(clip)
        }
        imageView31.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.ma_the), tv_code.text)
            clipboard.setPrimaryClip(clip)
        }
        btn_again.setOnClickListener {
            activity?.onBackPressed()
        }
        btn_home.setOnClickListener {
            activity?.finish()
        }
        btn_history.setOnClickListener {
            val intent = Intent(context, HistoryCardActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}