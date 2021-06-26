package vn.icheck.android.fragments.ecard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_purchase_card.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.adapters.McardAdapter
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.topup.TopupServices
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class PurchaseCardFragment : Fragment() {
    private val mcardAdapter = McardAdapter()
    private val denomitionAdapter = McardAdapter()

    companion object {
        var INSTANCE: PurchaseCardFragment? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        INSTANCE = this
        return inflater.inflate(R.layout.fragment_purchase_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        getTopService()
        setupListener()
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.mua_ma_the_dien_thoai)

        imgBack.setOnClickListener {
            ActivityUtils.finishActivity(activity)
        }

        imgAction.visibility = View.VISIBLE
        imgAction.setImageResource(R.drawable.ic_history_mission_24px)

        imgAction.setOnClickListener {
            ActivityUtils.startActivity<HistoryCardActivity>(this)
        }
    }

    private fun setupRecyclerView() {
        rcv_msp.adapter = mcardAdapter

        rcv_denomination.adapter = denomitionAdapter
    }

    private fun getTopService() {
        layoutContent.visibility = View.GONE
        layoutLoading.visibility = View.VISIBLE

        ICNetworkClient.getApiClient().getTopupsServices("EPIN")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<TopupServices> {
                    override fun onSuccess(t: TopupServices) {
                        if (layoutContent == null)
                            return

                        layoutContent.visibility = View.VISIBLE
                        layoutLoading.visibility = View.GONE

                        mcardAdapter.setMCardData(t.rows)
                        denomitionAdapter.setDenominationData(0, t.rows)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        if (layoutContent == null)
                            return

                        layoutContent.visibility = View.GONE
                        layoutLoading.visibility = View.GONE

                        DialogHelper.showConfirm(context, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : ConfirmDialogListener {
                            override fun onDisagree() {
                                ActivityUtils.finishActivity(activity)
                            }

                            override fun onAgree() {
                                getTopService()
                            }
                        })
                    }
                })
    }

    private fun setupListener() {
        btn_checkout.setOnClickListener {
            val service = mcardAdapter.getService ?: return@setOnClickListener

            val mspId = service.id
            val provider = service.provider
            val denomition = service.denomination[denomitionAdapter.getSelectedPosition]

            if (!provider.isNullOrEmpty() && !denomition.isNullOrEmpty()) {
                val checkOutFragment = CheckOutFragment()
                val bundle = Bundle()
                bundle.putString("msp", provider)
                bundle.putString("denomition", denomition)
                bundle.putInt("id", mspId)
                checkOutFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit, R.anim.left_to_right_pop_enter, R.anim.left_to_right_pop_exit)
                        .replace(R.id.content, checkOutFragment)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    fun changeMCard(servicePosition: Int) {
        denomitionAdapter.setServicePosition(servicePosition)

        btn_checkout.setBackgroundResource(R.drawable.bg_checkout_border_35)
        btn_checkout.isEnabled = false

        tv_total.text = getString(R.string.s_d, "0")
    }

    fun changePrice(price: String) {
        btn_checkout.background=ViewHelper.bgSecondaryCorners35(requireContext())
        btn_checkout.isEnabled = true

        tv_total.text = getString(R.string.s_d, TextHelper.formatMoney(price))
    }
}