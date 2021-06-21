package vn.icheck.android.loyalty.screen.loyalty_customers.history

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_loyalty_points_history.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.screen.loyalty_customers.history.adapter.FilterAdapter
import vn.icheck.android.loyalty.screen.loyalty_customers.history.adapter.PointsHistoryAdapter
import vn.icheck.android.loyalty.screen.loyalty_customers.history.dialog.MonthYearPickerDialog
import vn.icheck.android.loyalty.screen.loyalty_customers.history.viewmodel.GiftShopViewModel
import java.util.*

class LoyaltyPointsHistoryActivity : BaseActivityGame() {
    private val arrayRadio = arrayListOf<RadioButton>()
    private lateinit var filterAdapter: FilterAdapter
    private val giftShopViewModel by viewModels<GiftShopViewModel>()
    private val pointsHistoryAdapter = PointsHistoryAdapter()

    override val getLayoutID: Int
        get() = R.layout.activity_loyalty_points_history

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        giftShopViewModel.userId = intent.getLongExtra("id", 0L)
        arrayRadio.add(button_all)
        arrayRadio.add(btn_earn)
        arrayRadio.add(btn_change)
        radioGroup3.setOnCheckedChangeListener { group, checkedId ->
            for (item in arrayRadio) {
                item.setTextColor(Color.parseColor("#757575"))
            }
            val btn = arrayRadio.firstOrNull {
                it.id == checkedId
            }
            btn?.setTextColor(Color.parseColor("#212121"))
            when (btn?.id) {
                button_all.id -> {
                    giftShopViewModel.setTypeAll()
                }
                btn_earn.id -> {
                    giftShopViewModel.setTypeGrant()
                }
                btn_change.id -> {
                    giftShopViewModel.setTypeSpend()
                }
            }
            giftShopViewModel.setOnlyFilter(btn?.text.toString())
            filterAdapter.notifyDataSetChanged()
        }

        filterAdapter = FilterAdapter(giftShopViewModel.arrFilter) {
            root_motion.transitionToEnd()
        }
        tv_filter_type.adapter = filterAdapter
        img_clear.setOnClickListener {
            if (root_motion.currentState == root_motion.endState) {
                reset()
                root_motion.transitionToStart()
            }
        }
        textView33.setOnClickListener {
            if (root_motion.currentState == root_motion.startState) {
                root_motion.transitionToEnd()
            }
        }

        tv_done.setOnClickListener {
            if (root_motion.currentState == root_motion.endState) {
                root_motion.transitionToStart()
                lifecycleScope.launch {
                    giftShopViewModel.filter().collectLatest {
                        pointsHistoryAdapter.submitData(it)
                    }
                }
            }
        }
        btn_back.setOnClickListener {
            finish()
        }
        edt_time.setOnClickListener {
            val monthYearPickerDialog = MonthYearPickerDialog.newInstance(Calendar.getInstance().get(Calendar.MONTH) + 1,
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.YEAR))
            monthYearPickerDialog.setListener { view, year, month, dayOfMonth ->
                val months = if (month + 1 < 10) {
                    "0${month}"
                } else {
                    "$month"
                }
                giftShopViewModel.calendar.set(year, month, dayOfMonth)
                val s = getString(R.string.thang_s_d,months,year)
                giftShopViewModel.setTime()
                edt_time.setText(s)
                giftShopViewModel.addOnlyFilter(s)
                filterAdapter.notifyDataSetChanged()
            }
            monthYearPickerDialog.show(supportFragmentManager, null)
        }
        edt_time.addTextChangedListener {
            if (it?.trim().toString().isNotEmpty()) {
                tv_reset.setBackgroundResource(R.drawable.bg_blue_corner_18)
                tv_reset.setTextColor(Color.parseColor("#0064B1"))

            } else {
                tv_reset.setBackgroundResource(R.drawable.bg_dark_gray_corner_18)
                tv_reset.setTextColor(Color.WHITE)
            }
        }
        tv_reset.setOnClickListener {
            reset()
        }
        rcv_transactions.adapter = pointsHistoryAdapter
        giftShopViewModel.countLiveData.observe(this, Observer {
            tv_total_transactions.setText(R.string.lich_su_giao_dich_d, it)
        })
        lifecycleScope.launch {
            giftShopViewModel.getTransactionsHistory().collectLatest {
                pointsHistoryAdapter.submitData(it)

            }
        }
    }

    private fun reset() {
        edt_time.setText("")
        giftShopViewModel.calendar = Calendar.getInstance()
        button_all.isChecked = true
        if (giftShopViewModel.arrFilter.size == 2) {
            giftShopViewModel.arrFilter.removeAt(1)
        }
        filterAdapter.notifyDataSetChanged()
    }
}