package vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.adapter

import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_more_bussiness.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_message_more_business_stamp.view.*
import kotlinx.android.synthetic.main.item_suggest_product_verified_in_business.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectDistributor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectListMoreProductVerified
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectVendor
import vn.icheck.android.screen.user.detail_stamp_v6_1.more_business.view.IMoreBusinessView
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.WidgetUtils

class MoreProductVerifiedInBusinessAdapter(val view: IMoreBusinessView, val isVietNamLanguage: Boolean?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICObjectListMoreProductVerified?>()

    private val headerType = 1
    private val itemType = 2
    private val loadType = 3
    private val emptyType = 4

    private var errCode = 0

    private var isLoadMore = true
    private var isLoading = true

    private var objVendor: ICObjectVendor? = null
    private var objDistributor: ICObjectDistributor? = null

    fun setListData(list: MutableList<ICObjectListMoreProductVerified>) {
        errCode = 0
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
//        for (i in list.size -1 downTo 1){
//            list.removeAt(i)
//        }

        listData.clear()
        listData.add(null)
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICObjectListMoreProductVerified>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errCode = 0

        listData.addAll(list)

        notifyDataSetChanged()
    }

    fun addHeaderVendor(objVendor: ICObjectVendor?, objDistributor: ICObjectDistributor?) {
        if (objVendor != null) {
            this.objVendor = objVendor
        } else {
            this.objDistributor = objDistributor
        }

        if (listData.isNotEmpty())
            listData.add(0, null)
        else
            listData.add(null)

        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        errCode = 0
        isLoadMore = true

        Handler().postDelayed({
            notifyDataSetChanged()
        }, 100)
    }

    fun setError(code: Int) {
        errCode = code
        isLoadMore = false
        isLoading = false
        for (i in listData.size - 1 downTo 1) {
            listData.removeAt(i)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            headerType -> HeaderHolder(inflater.inflate(R.layout.item_header_more_bussiness, parent, false))
            itemType -> ViewHolder(inflater.inflate(R.layout.item_suggest_product_verified_in_business, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> ErrorHolder(inflater.inflate(R.layout.item_message_more_business_stamp, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore || errCode != 0)
                listData.size + 1
            else
                listData.size
        } else {
            if (errCode == 0) {
                0
            } else {
                1
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size)
                if (listData[position] == null) {
                    headerType
                } else {
                    itemType
                }
            else
                if (errCode != 0) {
                    emptyType
                } else {
                    loadType
                }
        } else {
            if (isLoadMore)
                loadType
            else
                emptyType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderHolder -> {
                if (objVendor != null) {
                    holder.setData(objVendor, null)
                } else {
                    holder.setData(null, objDistributor)
                }
            }

            is ViewHolder -> {
                val item = listData[position]
                holder.setData(item!!)

                holder.itemView.setOnClickListener {
                    view.onItemClick(item)
                }
            }

            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }

            is ErrorHolder -> {
                holder.setData(errCode)

                holder.itemView.btnTryAgain.setOnClickListener {
                    view.onClickTryAgain()
                }
            }
        }
    }

    inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(objVendor: ICObjectVendor?, objDistributor: ICObjectDistributor?) {
            if (objVendor != null) {
                if (isVietNamLanguage == false) {
                    itemView.tvName.text = if (!objVendor.name.isNullOrEmpty()) {
                        objVendor.name
                    } else {
                        "updating"
                    }
                } else {
                    itemView.tvName.text = if (!objVendor.name.isNullOrEmpty()) {
                        objVendor.name
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                if (isVietNamLanguage == false) {
                    itemView.tvPhone.text = if (!objVendor.phone.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.tel)} : </font>" + "<b>" + objVendor.phone + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.tel)} : </font>" + "<b>" + itemView.context.rText(R.string.updating) + "</b>")
                    }
                } else {
                    itemView.tvPhone.text = if (!objVendor.phone.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.so_dien_thoai)} : </font>" + "<b>" + objVendor.phone + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.so_dien_thoai)} : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
                    }
                }

                itemView.tvAddress.text = Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.address)} : </font>" +
                        "<b>" + vn.icheck.android.ichecklibs.Constant.getAddress(objVendor.address,
                                objVendor.district, objVendor.city, objVendor.country_name,
                                if (isVietNamLanguage == false) {
                                    itemView.context.rText(R.string.updating)
                                } else {
                                    itemView.context.getString(R.string.dang_cap_nhat)
                                })
                        + "</b>")

//                if (isVietNamLanguage == false) {
//                    itemView.tvAddress.text = if (!objVendor.address.isNullOrEmpty()) {
//                        if (!objVendor.city.isNullOrEmpty()) {
//                            if (!objVendor.district.isNullOrEmpty()) {
//                                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objVendor.address + " ," + objVendor.city + " ," + objVendor.district + "</b>")
//                            } else {
//                                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objVendor.address + " ," + objVendor.city + "</b>")
//                            }
//                        } else {
//                            Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objVendor.address + "</b>")
//                        }
//                    } else {
//                        Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + "updating" + "</b>")
//                    }
//                } else {
//                    itemView.tvAddress.text = if (!objVendor.address.isNullOrEmpty()) {
//                        if (!objVendor.city.isNullOrEmpty()) {
//                            if (!objVendor.district.isNullOrEmpty()) {
//                                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objVendor.address + " ," + objVendor.city + " ," + objVendor.district + "</b>")
//                            } else {
//                                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objVendor.address + " ," + objVendor.city + "</b>")
//                            }
//                        } else {
//                            Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objVendor.address + "</b>")
//                        }
//                    } else {
//                        Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
//                    }
//                }

                if (isVietNamLanguage == false) {
                    itemView.tvEmail.text = if (!objVendor.email.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + objVendor.email + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + itemView.context.rText(R.string.updating) + "</b>")
                    }
                } else {
                    itemView.tvEmail.text = if (!objVendor.email.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + objVendor.email + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
                    }
                }

                if (isVietNamLanguage == false) {
                    itemView.tvWebsite.text = if (!objVendor.website.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + objVendor.website + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + itemView.context.rText(R.string.updating) + "</b>")
                    }
                } else {
                    itemView.tvWebsite.text = if (!objVendor.website.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + objVendor.website + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
                    }
                }

                itemView.layoutPhone.setOnClickListener {
                    view.onClickPhone(objVendor.phone
                            ?: itemView.context.getString(R.string.dang_cap_nhat))
                }

                itemView.layoutMail.setOnClickListener {
                    view.onClickEmail(objVendor.email
                            ?: itemView.context.getString(R.string.dang_cap_nhat))
                }

                itemView.layoutWeb.setOnClickListener {
                    view.onClickWebsite(objVendor.website
                            ?: itemView.context.getString(R.string.dang_cap_nhat))
                }
            } else {
                if (isVietNamLanguage == false) {
                    itemView.tvName.text = if (!objDistributor?.name.isNullOrEmpty()) {
                        objDistributor?.name
                    } else {
                        itemView.context.rText(R.string.updating)
                    }
                } else {
                    itemView.tvName.text = if (!objDistributor?.name.isNullOrEmpty()) {
                        objDistributor?.name
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }

                if (isVietNamLanguage == false) {
                    itemView.tvPhone.text = if (!objDistributor?.phone.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.tel)} : </font>" + "<b>" + objDistributor?.phone + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.tel)} : </font>" + "<b>" + itemView.context.rText(R.string.updating) + "</b>")
                    }
                } else {
                    itemView.tvPhone.text = if (!objDistributor?.phone.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.so_dien_thoai)} : </font>" + "<b>" + objDistributor?.phone + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.so_dien_thoai)} : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
                    }
                }

//                if (isVietNamLanguage == false) {
//                    itemView.tvAddress.text = if (!objDistributor?.address.isNullOrEmpty()) {
//                        Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + objDistributor?.address + " ," + objDistributor?.city + " ," + objDistributor?.district + "</b>")
//                    } else {
//                        Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + "updating" + "</b>")
//                    }
//                } else {
//                    itemView.tvAddress.text = if (!objDistributor?.address.isNullOrEmpty()) {
//                        Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + objDistributor?.address + " ," + objDistributor?.city + " ," + objDistributor?.district + "</b>")
//                    } else {
//                        Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
//                    }
//                }


                itemView.tvAddress.text = Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.address)} : </font>" +
                        "<b>" + vn.icheck.android.ichecklibs.Constant.getAddress(objDistributor?.address,
                        objDistributor?.district, objDistributor?.city, objDistributor?.country_name,
                        if (isVietNamLanguage == false) {
                            itemView.context.rText(R.string.updating)
                        } else {
                            itemView.context.getString(R.string.dang_cap_nhat)
                        })
                        + "</b>")

                if (isVietNamLanguage == false) {
                    itemView.tvEmail.text = if (!objDistributor?.email.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + objDistributor?.email + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + itemView.context.rText(R.string.updating) + "</b>")
                    }
                } else {
                    itemView.tvEmail.text = if (!objDistributor?.email.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + objDistributor?.email + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.email)} : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
                    }
                }

                if (isVietNamLanguage == false) {
                    itemView.tvWebsite.text = if (!objDistributor?.website.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + objDistributor?.website + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + itemView.context.rText(R.string.updating) + "</b>")
                    }
                } else {
                    itemView.tvWebsite.text = if (!objDistributor?.website.isNullOrEmpty()) {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + objDistributor?.website + "</b>")
                    } else {
                        Html.fromHtml("<font color=#434343>${itemView.context.rText(R.string.website)} : </font>" + "<b>" + itemView.context.getString(R.string.dang_cap_nhat) + "</b>")
                    }
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(obj: ICObjectListMoreProductVerified) {
            itemView.background = ContextCompat.getDrawable(itemView.context, Constant.getVerticalProductBackground(obj.item_type))
            obj.image?.let {
                val image = if (it.isNotEmpty()) {
                    if (it.startsWith("http")) {
                        it
                    } else {
                        "http://icheckcdn.net/images/200x200/$it.jpg"
                    }
                } else {
                    null
                }
                WidgetUtils.loadImageUrlRounded10FitCenter(itemView.imgSuggesVerified, image)
            }

            if (isVietNamLanguage == false) {
                itemView.tvNameSuggestVerified.text = if (!obj.name.isNullOrEmpty()) {
                    obj.name
                } else {
                    itemView.context.rText(R.string.updating)
                }
            } else {
                itemView.tvNameSuggestVerified.text = if (!obj.name.isNullOrEmpty()) {
                    obj.name
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }


    inner class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_emty_history_topup)
                    itemView.btnTryAgain.visibility = View.GONE
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    if (isVietNamLanguage == false) {
                        itemView.btnTryAgain rText R.string.try_again
                    } else {
                        itemView.btnTryAgain rText R.string.thu_lai
                    }
                    itemView.btnTryAgain.visibility = View.VISIBLE
                }
            }

            val message = when (errorCode) {
                Constant.ERROR_INTERNET -> {
                    if (isVietNamLanguage == false) {
                        itemView.context.rText(R.string.checking_network_please_try_again)
                    } else {
                        itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    }
                }
                Constant.ERROR_UNKNOW -> {
                    if (isVietNamLanguage == false) {
                        itemView.context.rText(R.string.occurred_please_try_again)
                    } else {
                        itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
                Constant.ERROR_EMPTY -> {
                    if (isVietNamLanguage == false) {
                        itemView.context.rText(R.string.no_data)
                    } else {
                        itemView.context.getString(R.string.khong_co_san_pham_nao)
                    }
                }
                else -> {
                    if (isVietNamLanguage == false) {
                        itemView.context.rText(R.string.occurred_please_try_again)
                    } else {
                        itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    }
                }
            }
            itemView.txtMessage.text = message
        }
    }
}