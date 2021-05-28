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
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICBuyEpin
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity

class SuccessBuyEpinFragment : Fragment() {

    private var code: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_success_epin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_again.background= ViewHelper.bgWhiteRadius40StrokeSecondary1(requireContext())

        arguments?.let {
            it.getSerializable("data")?.let { data ->
                tv_serial.text = (data as ICBuyEpin.EpinData).serial
                tv_code.text = data.pin
                code = data.pin
                tv_total.text = String.format("%,dđ", data.denomination.toLong())
                tv_msp.text = it.getString("msp")
                tv_notify.text = String.format("Quý khách đã mua thành công \n mã thẻ điện thoại %s",
                        it.getString("msp"))
            }
        }
        btn_charge.setOnClickListener {
            code?.let {
                val chargePhone = Intent(Intent.ACTION_DIAL)
                chargePhone.setData(Uri.parse("tel:" + Uri.encode("*100*$it#")))
                startActivity(chargePhone)
            }
        }
        imageView30.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Số serie", tv_serial.text)
            clipboard.setPrimaryClip(clip)
        }
        imageView31.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Mã thẻ", tv_code.text)
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