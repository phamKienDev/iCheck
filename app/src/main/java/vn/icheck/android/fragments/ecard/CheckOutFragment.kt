package vn.icheck.android.fragments.ecard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICBuyEpin
import vn.icheck.android.util.kotlin.ToastUtils

class CheckOutFragment : Fragment() {

    var msp: String? = ""
    var fee: String = ""
    var mspId = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            msp = it.getString("msp")
            fee = it.getString("denomition", "")
            mspId = it.getInt("id", -1)
            tv_msp.text = msp
            tv_type.text = String.format("%,d đ", fee.toLong())
            tv_fee.text = "Miễn phí"
            tv_total.text = String.format("%,d iCoin", fee.toLong())
        }
        tv_icoin.text = String.format("Số dư hiện tại: %,d iCoin", SessionManager.getCoin())

        img_icoin.background = ViewHelper.bgWhiteStrokeSecondary1Corners4(img_icoin.context)

        txtTitle.setText(R.string.thanh_toan)
        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btn_checkout.setOnClickListener {
            btn_checkout.isEnabled = false
            DialogHelper.showLoading(this@CheckOutFragment)
            val requestBody = hashMapOf<String, Any>()
            requestBody.put("service_id", mspId)
            requestBody.put("denomination", fee)
            ICNetworkClient.getApiClient().postBuyEpin(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ICBuyEpin> {
                    override fun onSuccess(t: ICBuyEpin) {
                        DialogHelper.closeLoading(this@CheckOutFragment)
                        btn_checkout.isEnabled = true
                        if (t.statusCode != null && t.statusCode != 200 && t.message != null) {
                            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        } else {
                            t.epinData?.let {
                                val successBuyEpinFragment = SuccessBuyEpinFragment()
                                val bundle = Bundle()
                                bundle.putSerializable("data", it)
                                bundle.putString("msp", msp!!)
                                successBuyEpinFragment.arguments = bundle
                                activity!!.supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.right_to_left_enter,
                                        R.anim.right_to_left_exit,
                                        R.anim.left_to_right_pop_enter,
                                        R.anim.left_to_right_pop_exit
                                    )
                                    .replace(R.id.content, successBuyEpinFragment)
                                    .commit()
                            }
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        DialogHelper.closeLoading(this@CheckOutFragment)
                        Log.e("e", "err", e)
                        btn_checkout.isEnabled = true
                        ToastUtils.showLongError(context, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                })
        }
    }

}