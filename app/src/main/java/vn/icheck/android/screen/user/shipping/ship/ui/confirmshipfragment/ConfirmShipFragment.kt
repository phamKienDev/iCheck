package vn.icheck.android.screen.user.shipping.ship.ui.confirmshipfragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.JsonObject
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.FragmentConfirmShipBinding
import vn.icheck.android.databinding.ItemConfirmShipBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.report.ReportActivity
import vn.icheck.android.screen.user.shipping.ship.ShipActivity
import vn.icheck.android.screen.user.shipping.ship.adpter.vm.ShipViewModel
import vn.icheck.android.screen.user.shipping.ship.ui.main.SuccessConfirmShipDialog
import vn.icheck.android.util.ick.*

class ConfirmShipFragment : Fragment() {

    companion object {
        fun newInstance() = ConfirmShipFragment()
    }

    private var _binding: FragmentConfirmShipBinding? = null
    val binding get() = _binding!!
    private val viewModel: ShipViewModel by activityViewModels()
    private var paymentStartInsider = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConfirmShipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.arrayCart.isEmpty()) {
            binding.btnConfirm.setText(R.string.thanh_toan)
        }
        if (viewModel.detailOrderId == 0L) {
            if (savedInstanceState == null) {
                viewModel.getFee().observe(viewLifecycleOwner, {
                    if (it.data?.isJsonObject == true) {
                        val obj = it.data!! as JsonObject
                        val fee = obj.get("deliveryCharges").asLong
                        binding.tvFee.setText(R.string.s_d, TextHelper.formatMoneyPhay(fee))
                        binding.tvTotalFee.setText(R.string.s_d, TextHelper.formatMoneyPhay(fee))
//                        if (paymentStartInsider) {
//                            TekoHelper.tagPaymentStartAndSuccess(fee)
//                            paymentStartInsider = false
//                        }
                    }
                })
            }
            binding.btnConfirm.setOnClickListener {
                showLoadingTimeOut(10000)
                if (viewModel.arrayCart.isNotEmpty()) {
                    viewModel.purchase(binding.edtNotes.text.trim().toString()).observe(viewLifecycleOwner, {
                        dismissLoadingScreen()
                        if (it.statusCode == "200") {
                            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra("id", it.data?.id)
                            })
                            SuccessConfirmShipDialog(it.data?.id ?: 0L).apply {
                                isCancelable = false
                                action = {
                                    viewModel.moveToCart()
                                }
                            }.show(childFragmentManager, null)
                        } else {
                            requireContext().showShortErrorToast(it.message)
                        }
                    })
                } else {
                    viewModel.checkout().observe(viewLifecycleOwner, { checkout ->
                        dismissLoadingScreen()
                        if (checkout.statusCode == "200") {
//                                    SuccessConfirmShipDialog().show(childFragmentManager, null)
                            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra("id", viewModel.detailOrderId)
                            })

                            SuccessConfirmShipDialog(checkout?.data?.id ?: 0L).apply {
                                isCancelable = false
                                action = {
                                    requireActivity().setResult(Activity.RESULT_OK)
                                    requireActivity().finish()
                                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK_TO_SHAKE))
                                }
                            }.show(childFragmentManager, null)
                        } else {
                            requireContext().showShortErrorToast(checkout.message)
                        }

                    })
//                    viewModel.confirmShip().observe(viewLifecycleOwner, {
//                        if (it.statusCode == "200") {
//                            viewModel.checkout().observe(viewLifecycleOwner, { checkout ->
//                                if (checkout.statusCode == "200") {
////                                    SuccessConfirmShipDialog().show(childFragmentManager, null)
//                                    requireActivity().setResult(Activity.RESULT_OK)
//                                    requireActivity().finish()
//                                    SuccessConfirmShipDialog(viewModel.detailOrderId).apply {
//                                        isCancelable = false
//                                    }.show(childFragmentManager, null)
//                                }
//                                dismissLoadingScreen()
//                            })
//                        } else {
//                            requireContext().showShortErrorToast(" Đã xảy ra lỗi vui lòng thử lại sau!")
//                            dismissLoadingScreen()
//                        }
//                    })
                }
            }
            binding.btnBack.setOnClickListener {
                viewModel.moveToChoose()
            }
            binding.tvChange.setOnClickListener {
                viewModel.moveToChoose()
            }
            if (viewModel.getChosen() != -1L) {
                viewModel.arrayAddress.firstOrNull {
                    it?.id == viewModel.getChosen()
                }?.let {
                    binding.tvName simpleText it.getName()
                    val arr = arrayListOf<Char>()
                    arr.addAll(it.phone.toString().toList())
                    if (arr.size > 7) {
                        arr.add(7, ' ')
                        arr.add(4, ' ')
                    }
                    binding.tvPhone simpleText arr.joinToString(separator = "")
                    binding.tvAddress simpleText it.getFullAddress()
                }
            }
            binding.tvChange.setOnClickListener {
                viewModel.moveToChoose()
            }
            if (viewModel.arrayCart.isNotEmpty()) {
                binding.businessLogo.loadImageWithHolder(viewModel.shop["avatar"] as String?,R.drawable.ic_icheck_logo)
                binding.tvBusinessName simpleText viewModel.shop["name"] as String?
                val filter = viewModel.arrayCart.filter {
                    it.isSelected
                }
                val total = filter.sumBy {
                    it.quantity!!
                }
                binding.tvQuantity.setText(R.string.d_san_pham, total)
                binding.tvQuantityGift.beGone()
                binding.imgGift.beGone()
                binding.tvGift.beGone()
                binding.divider21.beGone()
                for (item in filter) {
                    val bd = ItemConfirmShipBinding.inflate(layoutInflater)
                    bd.imgGift.loadRoundedImage(item.product?.imageUrl, R.drawable.img_product_shop_default, 4)
                    bd.imgGift.setOnClickListener {
                        IckProductDetailActivity.start(requireActivity(), item.product?.originId
                                ?: 0L)
                    }
                    bd.tvGift simpleText item.product?.name
                    bd.tvQuantityGift simpleText "x${item.quantity}"
                    binding.containter.addView(bd.root, 5)
                }
            } else {
                viewModel.detailRewardResponse?.let {
                    binding.businessLogo.loadImageWithHolder(it.data?.shopImage,R.drawable.ic_icheck_logo)
//                    if (!it.data?.shopName.isNullOrEmpty()) {
//                        binding.tvBusinessName simpleText it.data?.shopName
//                    } else {
//                        binding.tvBusinessName simpleText "iCheck"
//                    }
                    binding.tvBusinessName.setText(R.string.icheck_campaign)
                    binding.tvQuantity.setText(R.string.mot_san_pham)

                    if (!it.data?.image.isNullOrEmpty()) {
                        binding.imgGift.loadRoundedImage(it.data?.image, R.drawable.img_product_shop_default, corner = 4)
                    } else {
                        binding.imgGift.setImageResource(R.drawable.ic_icheck_logo)
                    }
                    binding.tvGift simpleText it.data?.name

                }
            }
            binding.textView91.beVisible()
        } else {
            showLoadingTimeOut(10000)
            binding.btnBack.setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.BACK_TO_SHAKE))
                requireActivity().finish()
            }
            binding.groupDetailOrder.beVisible()
            binding.tvChange.beGone()
            getDetailOrder()
        }

    }

    private fun getDetailOrder() {
        binding.textView26.setText(R.string.thong_tin_don_hang)
        binding.imgStatus.beVisible()
        viewModel.getDetailOrder().observe(viewLifecycleOwner, {
            it.data?.let { detailOrderResponse ->
//                if (paymentStartInsider) {
//                    detailOrderResponse.deliveryCharges?.let { total ->
//                        TekoHelper.tagPaymentStartAndSuccess(total)
//                    }
//                    paymentStartInsider = false
//                }

                binding.businessLogo.loadImageWithHolder(detailOrderResponse.shop?.avatar,R.drawable.ic_icheck_logo)
                binding.tvBusinessName simpleText detailOrderResponse.shop?.name
                binding.tvFee.text = getString(R.string.x_d, detailOrderResponse.deliveryCharges).replace(".", ",")
                binding.tvTotalFee.text =  getString(R.string.x_d, detailOrderResponse.deliveryCharges).replace(".", ",")
                val total = detailOrderResponse.orderItem?.sumBy { item ->
                    item?.quantity!!
                }
                binding.tvQuantity.setText(R.string.d_san_pham, total)
                binding.tvQuantityGift.beGone()
                binding.imgGift.beGone()
                binding.tvGift.beGone()
                binding.divider21.beGone()
                for (item in detailOrderResponse.orderItem!!) {
                    val bd = ItemConfirmShipBinding.inflate(layoutInflater)
                    bd.imgGift.loadRoundedImage(item?.productInfo?.imageUrl, corner = 4)
                    bd.tvGift simpleText item?.productInfo?.name
                    bd.tvQuantityGift simpleText "x${item?.quantity}"
                    bd.imgGift.setOnClickListener {
                        IckProductDetailActivity.start(requireActivity(), item?.productInfo?.originId
                                ?: 0L)
                    }
                    binding.containter.addView(bd.root, 5)
                }
                binding.tvShipTime simpleText "${detailOrderResponse.createdAt?.getHourMinutesTime()}"
                binding.tvShipCode.setText(R.string.ma_don_hang_s, detailOrderResponse.code)
                binding.tvName simpleText detailOrderResponse.shippingAddress?.getName()
                val arr = arrayListOf<Char>()
                arr.addAll(detailOrderResponse.shippingAddress?.phone.toString().toList())
                if (arr.size > 7) {
                    arr.add(7, ' ')
                    arr.add(4, ' ')
                }
                binding.tvPhone simpleText arr.joinToString(separator = "")
                binding.tvAddress simpleText detailOrderResponse.shippingAddress?.getFullAddress()
                if (detailOrderResponse.note.isNullOrEmpty()) {
                    binding.groupNote.beGone()
                } else {
                    val ss = SpannableString(getString(R.string.ghi_chu_s, detailOrderResponse.note))
                    ss.setSpan(ForegroundColorSpan(Color.parseColor("#b4b4b4")), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.groupNote.beVisible()
                    binding.edtNotes.isFocusable = false
                    binding.edtNotes.isFocusableInTouchMode = false

                    binding.edtNotes.setText(ss)
                }

                when (detailOrderResponse.status) {
                    0, 2 -> {
                        binding.imgStatus.setImageResource(R.drawable.img_pending)
                        binding.btnConfirm.setText(R.string.huy_don)
                        binding.btnConfirm.setTextColor(Color.parseColor("#057DDA"))
                        binding.btnConfirm.setBackgroundResource(R.drawable.bg_stroke_blue_corner_4)
                        binding.tvReport.beVisible()
                        binding.btnConfirm.setOnClickListener {
                            DialogHelper.showConfirm(requireContext(),getString(R.string.ban_chac_chan_muon_huy_n_don_hang_nay),null ,getString(R.string.de_sau), getString(R.string.dong_y),true, object : ConfirmDialogListener{
                                override fun onDisagree() {
                                }

                                override fun onAgree() {
                                    viewModel.cancelOrder().observe(viewLifecycleOwner, { res ->
                                        if (res.statusCode == "200") {
                                            viewModel.moveToConfirm()
                                        }
                                    })
                                }
                            })

                        }
                        binding.tvReport.setOnClickListener {
                            ReportActivity.start(ReportActivity.order, detailOrderResponse.id, getString(R.string.bao_loi_don_hang), requireActivity())
                        }
                    }
                    3, 4 -> {
                        binding.imgStatus.setImageResource(R.drawable.img_shipping)
                        binding.btnConfirm.setText(R.string.huy_don)
                        binding.btnConfirm.setTextColor(Color.parseColor("#B4B4B4"))
                        binding.btnConfirm.setBackgroundResource(R.drawable.bg_stroke_gray_corner_4)
                        binding.btnConfirm.alpha = 0.7f
                        binding.tvReport.beVisible()
                        binding.btnConfirm.setOnClickListener(null)
                        binding.tvReport.setOnClickListener {
                            ReportActivity.start(ReportActivity.order, detailOrderResponse.id, getString(R.string.bao_loi_don_hang), requireActivity())
                        }
                        binding.textView91.setText(R.string.thoi_gian_cap_nhat)
                        binding.tvShipTime simpleText detailOrderResponse.updatedAt?.getHourMinutesTime()
                    }
                    5 -> {
                        binding.imgStatus.setImageResource(R.drawable.img_shipped)
                        binding.btnConfirm.setText(R.string.danh_gia_don_hang)
                        binding.btnConfirm.setTextColor(Color.parseColor("#B4B4B4"))
                        binding.btnConfirm.setBackgroundResource(R.drawable.bg_stroke_gray_corner_4)
                        binding.textView91.setText(R.string.thoi_gian_nhan_hang)
                        binding.tvShipTime simpleText detailOrderResponse.completedAt?.getHourMinutesTime()
                    }
                    6, 7 -> {
                        binding.imgStatus.setImageResource(R.drawable.img_cancelled)
                        binding.btnConfirm.setText(R.string.mua_lai_don_nay)
                        binding.btnConfirm.setTextColor(Color.parseColor("#057DDA"))
                        binding.btnConfirm.setBackgroundResource(R.drawable.bg_stroke_blue_corner_4)
                        binding.textView91.setText(R.string.thoi_gian_huy_don)
                        binding.tvShipTime simpleText detailOrderResponse.cancelledAt?.getHourMinutesTime()
                        binding.btnConfirm.setOnClickListener {
                            viewModel.rebuy(detailOrderResponse)
                        }
                        viewModel.rebuyLiveData.observe(viewLifecycleOwner, { id ->
                            val i = Intent(requireContext(), ShipActivity::class.java).apply {
                                putExtra(Constant.CART, true)
                            }
                            startActivity(i)
                        })
                    }
                }
            }
            binding.textView91.beVisible()
            dismissLoadingScreen()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}