package vn.icheck.android.screen.user.list_shop_variant.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.holder_list_shop_variant.view.*
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableStartText
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beInvisible
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.models.ICShopVariantV2
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.ServiceShopVariantAdapter
import vn.icheck.android.screen.user.list_shop_variant.view.IListShopVariantView
import vn.icheck.android.util.text.ICheckTextUtils

class ListShopVariantAdapter constructor(val view: IListShopVariantView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICShopVariantV2>()

    private lateinit var adapterService: ServiceShopVariantAdapter

    fun setListData(list: MutableList<ICShopVariantV2>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    val isListNotEmpty: Boolean
        get() {
            return listData.isNotEmpty()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.holder_list_shop_variant, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)

                holder.itemView.layoutLocation.setOnClickListener {
                    view.onClickShowMap(item)
                }

                holder.itemView.layoutLocation2.setOnClickListener {
                    view.onClickShowMap(item)
                }

                holder.itemView.layoutAddToCart.apply {
                    background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
                    setOnClickListener {
                        if (item.id != null) {
                            this@ListShopVariantAdapter.view.onClickAddToCart(item.id!!)
                        }
                    }
                }
            }
        }
    }

    inner class ViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ICShopVariantV2) {
            itemView.viewLocation2.background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(itemView.context)
            itemView.tvChiDuong.fillDrawableStartText(R.drawable.ic_location_item_shop_variant)

            if (listData.isNotEmpty()) {
                if (adapterPosition == 0) {
                    itemView.tvCount.visibility = View.VISIBLE
                    itemView.tvCount.setText(itemView.context.getString(R.string.d_diem_ban_gan_day, listData.size))
                } else {
                    itemView.tvCount.visibility = View.GONE
                }
            }

            itemView.tv_shop_name.text = item.name

            if (item.distance != null) {
                TextHelper.convertMtoKm(item.distance!!, itemView.tv_distance, itemView.context.getString(R.string.khoang_cach))
            }

            itemView.tv_score.text = String.format("%.1f", item.rating).replace(",", ".")

            if (item.isOnline == true && item.isOffline == true) {
                itemView.layoutLocation2.visibility = View.VISIBLE
                itemView.layoutAddToCart.visibility = View.VISIBLE

                itemView.layoutAddToCart.layoutParams = ViewHelper.createLayoutParams32Dp(SizeHelper.size32, 0, 0, 0, 0)
                itemView.tvSubAddToCart.compoundDrawablePadding = 30
            } else {
                itemView.layoutLocation2.visibility = View.GONE

                val param = itemView.layoutAddToCart.layoutParams as LinearLayout.LayoutParams?
                param?.setMargins(SizeHelper.size38, 0, 0, 0)
                itemView.layoutAddToCart.layoutParams = param

                if (item.isOnline == true) {
                    itemView.layoutAddToCart.visibility = View.VISIBLE
                } else {
                    itemView.layoutAddToCart.visibility = View.GONE
                }

                itemView.layoutLocation.apply {
                    background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(context)
                    if (item.isOffline == true) {
                        beVisible()
                    } else {
                        beGone()
                    }
                }
            }

            val listService = mutableListOf<ICServiceShopVariant>()

            if (item.isOffline == true) {
                listService.add(ICServiceShopVariant(0, R.drawable.ic_offline_shop_variant_18dp, itemView.context.getString(R.string.mua_tai_cua_hang), "#eb5757", R.drawable.bg_corner_shop_variant_offline))
            }

            if (item.verified == true) {
                listService.add(ICServiceShopVariant(1, R.drawable.ic_verified_shop_variant_18px, itemView.context.getString(R.string.dai_ly_chinh_hang), "#49aa2d", R.drawable.bg_corner_verified_shop_variant))
            }

            if (item.isOnline == true) {
                listService.add(ICServiceShopVariant(2, R.drawable.ic_online_shop_variant_18px, itemView.context.getString(R.string.ban_online), "#2d9cdb", R.drawable.bg_corner_online_shop_variant))
            }

            if (!listService.isNullOrEmpty()) {
                initAdapterService()
                adapterService.setListData(listService)
            }

//            if (item.saleOff != null && item.price != null) {
//                if (item.saleOff == true && item.price != null) {
//                    if (item.specialPrice != null && item.price != null) {
//                        itemView.tv_sale_price?.visibility = View.VISIBLE
//                        ICheckTextUtils.setSalePrice(itemView.tv_sale_price, item.price!!)
//                        ICheckTextUtils.setPrice(itemView.tv_price, item.specialPrice!!)
//                    } else {
//                        itemView.tv_sale_price?.visibility = View.INVISIBLE
//                        ICheckTextUtils.setPrice(itemView.tv_price, item.price!!)
//                    }
//                } else {
//                    itemView.tv_sale_price?.visibility = View.INVISIBLE
//                    ICheckTextUtils.setPrice(itemView.tv_price, item.price!!)
//                }
//            } else {
//                itemView.tv_sale_price?.visibility = View.INVISIBLE
//                ICheckTextUtils.setPrice(itemView.tv_price, item.price ?: 0)
//            }

            if (item.price != null) {
                itemView.tv_sale_price.beVisible()
                if (item.saleOff == true && item.specialPrice != null) {
                    itemView.tv_price?.beVisible()
                    ICheckTextUtils.setPrice(itemView.tv_sale_price, item.specialPrice!!)
                    ICheckTextUtils.setSalePrice(itemView.tv_price, item.price!!)
                } else {
                    ICheckTextUtils.setPrice(itemView.tv_sale_price, item.price!!)
                    itemView.tv_price?.beInvisible()
                }
            } else {
                itemView.tv_sale_price?.beInvisible()
                itemView.tv_price?.beInvisible()
            }

        }

        private fun initAdapterService() {
            adapterService = ServiceShopVariantAdapter(itemView.context)
            itemView.recyclerView?.layoutManager = FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
            itemView.recyclerView?.adapter = adapterService
        }
    }
}