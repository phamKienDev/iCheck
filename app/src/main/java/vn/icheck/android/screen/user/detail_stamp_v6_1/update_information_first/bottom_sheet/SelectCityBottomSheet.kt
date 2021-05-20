package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import vn.icheck.android.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.bottom_sheet_dialog_select_city.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.room.entity.ICProvince
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.adapter.SelectCityBottomSheetAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.presenter.SelectCityBottomSheetPresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.view.ISelectCityView
import vn.icheck.android.util.kotlin.ToastUtils
import java.util.concurrent.TimeUnit

class SelectCityBottomSheet : BottomSheetDialogFragment(), ISelectCityView {

    override fun getTheme(): Int = R.style.BaseBottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    private val presenter = SelectCityBottomSheetPresenter(this)
    private val adapter = SelectCityBottomSheetAdapter(this)
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_dialog_select_city, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            Handler().postDelayed(Runnable {
                val d = dialog as BottomSheetDialog
                val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }, 0)
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setupAddressHelper()
        presenter.getListProvince()

        edtSearch.background=ViewHelper.bgTransparentRadius4StrokeLineColor1(requireContext())

        initRecyclerView()
        listener()
    }

    private fun initRecyclerView() {
        rcvCity.adapter = adapter
    }

    private fun listener() {
        btnClose.setOnClickListener {
            dismiss()
        }

        disposable = RxTextView.afterTextChangeEvents(edtSearch)
                .skipInitialValue()
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.view().text.isNotEmpty()) {
                        adapter.search(it.view().text.toString().trim())
                    } else {
                        adapter.search("")
                    }
                }
    }

    override fun onSetListProvince(list: MutableList<ICProvince>) {
        adapter.setData(list)
    }

    override fun onMessageClicked() {
        presenter.getListProvince()
    }

    override fun onItemClicked(item: ICProvince) {
        dismiss()
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SELECT_CITY, item))
    }

    override fun showError(message: String?) {
        if (adapter.isListNotEmpty) {
            ToastUtils.showShortError(context, message)
        } else {
            adapter.setError(context?.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }

    override val mContext: Context?
        get() = context
}