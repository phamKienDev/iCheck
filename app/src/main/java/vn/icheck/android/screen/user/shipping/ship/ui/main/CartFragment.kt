package vn.icheck.android.screen.user.shipping.ship.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.FragmentCartBinding
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.shipping.ship.adpter.vm.ShipViewModel
import vn.icheck.android.screen.user.shipping.ship.adpter.adapter.CartItemsAdapter
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.util.ick.*

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    val binding get() = _binding!!
    lateinit var cartItemsAdapter: CartItemsAdapter

    private val viewModel: ShipViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingTimeOut(30000)
        binding.btnConfirm.enable()
        cartItemsAdapter = CartItemsAdapter(viewModel.arrayCart, {
            viewModel.addItemIntoCart(it).observe(viewLifecycleOwner) { response ->
                if (response.statusCode == "200") {
                    for (item in viewModel.arrayCart) {
                        if (item.isSelected) {
                            if (viewModel.arrayCart[it].product?.id == item.product?.id){
                                val totalProduct = (item.quantity ?: 1) + 1
                                val totalPrice = (item.price ?: 0) * totalProduct

                                AppDatabase.getDatabase().productIdInCartDao().updateProduct(totalPrice, totalProduct.toLong(), item.product?.id ?: -1)
                            }
                        }
                    }

                    getCart()
                }
            }
        }, {
            viewModel.removeItemOutOfCart(it).observe(viewLifecycleOwner) { response ->
                if (response.statusCode == "200") {
                    for (item in viewModel.arrayCart) {
                        if (item.isSelected) {
                            if (viewModel.arrayCart[it].product?.id == item.product?.id){
                                val totalProduct = (item.quantity ?: 1) - 1
                                val totalPrice = (item.price ?: 0) * totalProduct

                                AppDatabase.getDatabase().productIdInCartDao().updateProduct(totalPrice, totalProduct.toLong(), item.product?.id ?: -1)
                            }
                        }
                    }
                    getCart()
                }
            }
        })
        cartItemsAdapter.onCheckedChange = {
            viewModel.arrayCart[it].isSelected = !viewModel.arrayCart[it].isSelected
            if (viewModel.noItemSelected()) {
                binding.btnConfirm.disable()
            } else {
                binding.btnConfirm.enable()
            }
            updatePrice()
            null
        }
        cartItemsAdapter.onDelete = {
            viewModel.deleteItemFromCart(it).observe(viewLifecycleOwner) { response ->
                if (response.statusCode == "200") {
                    getCart()
                }
            }
            null
        }
        binding.rcvCart.adapter = cartItemsAdapter
        getCart()
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.btnConfirm.setOnClickListener {
            TrackingAllHelper.tagIcheckItemBuyStart(viewModel.arrayCart)
            viewModel.moveToChoose()
        }
    }

    private fun getCart() {
        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_PRICE))
        viewModel.getCart().observe(viewLifecycleOwner) {
            if (it.statusCode != "200" || it.data.isNullOrEmpty()) {
                AppDatabase.getDatabase().productIdInCartDao().deleteAll()
                binding.btnConfirm.beGone()
                binding.divider16.beGone()
                binding.cartNoItem.beVisible()
                binding.textView26 simpleText "Giỏ hàng"
                binding.groupItems.beGone()
            } else {
                binding.btnConfirm.beVisible()
                binding.businessLogo loadSimpleImage it?.data?.firstOrNull()?.shop?.avatar
                binding.tvBusinessName simpleText it.data?.firstOrNull()?.shop?.name
                val total = it.data?.firstOrNull()?.itemCart
                        ?.map { item ->
                            item?.quantity
                        }
                        ?.toList()
                        ?.sumBy { value ->
                            value ?: 0
                        }
//                binding.tvQuantity simpleText "$total sản phẩm"
                binding.textView26.rText(R.string.gio_hang_d, total)

                val arrNewCart = arrayListOf<ItemCartItem>()
                if (!it.data?.firstOrNull()?.itemCart.isNullOrEmpty()) {
                    for (item in it.data?.firstOrNull()?.itemCart!!) {
                        arrNewCart.add(item!!)
                    }
                }
                if (viewModel.arrayCart.isNotEmpty()) {
                    val filter = viewModel.arrayCart
                            .filter { item ->
                                !item.isSelected
                            }
                    if (filter.isNotEmpty()) {
                        arrNewCart.forEach { item ->
                            if (filter.firstOrNull { fil -> item.product?.id == fil.product?.id } != null) {
                                item.isSelected = false
                            }
                        }
                    }
                }
                viewModel.arrayCart.clear()
                viewModel.arrayCart.addAll(arrNewCart)
                cartItemsAdapter.notifyDataSetChanged()
                updatePrice()
            }
            dismissLoadingScreen()
        }
    }

    private fun updatePrice() {
        var totalPrice = 0L
        var totalProduct = 0
        var total = 0
        for (item in viewModel.arrayCart) {
            total += item.quantity ?: 0
            if (item.isSelected) {

                val price = item.price ?: 0L
                val quantity = item.quantity ?: 0
                totalPrice += price * quantity
                totalProduct += item.quantity ?: 0
            }
        }
        binding.tvQuantity.rText(R.string.d_d_san_pham, totalProduct, total)
        binding.tvPrice.text = rText(R.string.d_xu, totalPrice).replace(".", ",")
    }

}