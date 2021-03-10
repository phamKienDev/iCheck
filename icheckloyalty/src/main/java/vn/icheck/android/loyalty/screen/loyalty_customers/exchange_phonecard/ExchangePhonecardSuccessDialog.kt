package vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_exchange_phonecard_success.*
import vn.icheck.android.loyalty.R

class ExchangePhonecardSuccessDialog(val phoneNumber: String?, val service: String?): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_exchange_phonecard_success, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvMessage.text = "Thẻ cào đã được cộng vào tài khoản $phoneNumber"
        tvPoint.text = "Thẻ cào $service"
        imgClose.setOnClickListener {
            dismiss()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,
                R.style.DialogNoKeyboard)
    }
}