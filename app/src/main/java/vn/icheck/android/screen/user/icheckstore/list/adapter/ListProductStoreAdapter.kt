package vn.icheck.android.screen.user.icheckstore.list.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICStoreiCheck
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.icheckstore.view.IGiftStoreView
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.WidgetUtils

class ListProductStoreAdapter constructor(val view: IGiftStoreView, val listener: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICStoreiCheck>()

    private val itemType = 1
    private val itemLoadMore = 2

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_error_emty_history_topup
    private var isLoading = false
    private var isLoadMore = false

    private fun checkLoadMore(listCount: Int) {
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
        mMessageError = ""
    }

    fun setError(error: String, icon: Int) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        iconMessage = icon
        mMessageError = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: List<ICStoreiCheck>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<ICStoreiCheck>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setLoadMore(position: Int): Boolean {
        return getItemViewType(position) == itemLoadMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent)
            itemLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isEmpty()) {
            if (mMessageError != null) {
                1
            } else {
                0
            }
        } else {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                itemLoadMore
            }
        } else {
            if (mMessageError != null) {
                ICViewTypes.MESSAGE_TYPE
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, getString(R.string.hien_tai_chua_co_san_pham_nao), getString(R.string.vui_long_quay_lai_sau_de_doi_nhung_san_pham_chinh_hang_tu_cac_thuong_hieu_uy_tin_nhat), -1)
                } else {
                    holder.bind(iconMessage, mMessageError!!)

                    holder.listener(View.OnClickListener {
                        listener.onMessageClicked()
                    })
                }
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICStoreiCheck>(createItemiCheckStore(parent.context)) {
        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICStoreiCheck) {
            (itemView as LinearLayout).run {
                (getChildAt(0) as AppCompatImageView).let {
                    if (!obj.imageUrl.isNullOrEmpty()) {
                        WidgetUtils.loadImageFitCenterUrl(it, obj.imageUrl, R.drawable.bg_error_emty_attachment)
                    } else {
                        it.scaleType = ImageView.ScaleType.CENTER_CROP
                        it.setImageResource(R.drawable.bg_error_emty_attachment)
                    }
                }

                val tvICoin = (getChildAt(1) as LinearLayout).getChildAt(1) as AppCompatTextView
                val tvName = getChildAt(2) as AppCompatTextView
                val btnAction = getChildAt(3) as AppCompatTextView

                if (obj.addToCart) {
                    btnAction.isEnabled = false
                    btnAction.setTextColor(ColorManager.getSecondTextColor(context))
                    btnAction.setText(R.string.da_co_trong_gio_hang)
                    btnAction.background = vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(context)
                } else {
                    btnAction.isEnabled = true
                    btnAction.setTextColor(ContextCompat.getColor(context, R.color.white))
                    btnAction.setText(R.string.them_vao_gio_hang)
                    btnAction.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
                }

                tvICoin.setText(R.string.s_xu ,TextHelper.formatMoneyPhay(obj.price))

                tvName.text = if (!obj.name.isNullOrEmpty()) {
                    obj.name
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }

                itemView.setOnClickListener {
                    view.onClickItem(obj)
                }

                btnAction.setOnClickListener {
                    if (!SessionManager.isUserLogged) {
                        view.onLogin()
                    } else {
                        obj.addToCart = true
                        btnAction.setTextColor(ColorManager.getSecondTextColor(context))
                        btnAction.setText(R.string.da_co_trong_gio_hang)
                        btnAction.background = vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(context)
                        view.onExchangeGift(obj)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun createItemiCheckStore(context: Context): View {
        return LinearLayout(context).also { layoutParams ->
            layoutParams.layoutParams = ViewHelper.createLayoutParams()
            layoutParams.orientation = LinearLayout.VERTICAL
            layoutParams.setBackgroundColor(ColorManager.getAppBackgroundWhiteColor(layoutParams.context))
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL

            layoutParams.addView(AppCompatImageView(context).also { img ->
                img.id = R.id.imgProduct
                img.layoutParams = RelativeLayout.LayoutParams(SizeHelper.size84, SizeHelper.size84).also {
                    it.topMargin = SizeHelper.size16
                    it.leftMargin = SizeHelper.size5
                    it.rightMargin = SizeHelper.size5
                }
                img.scaleType = ImageView.ScaleType.FIT_CENTER
            })

            layoutParams.addView(LinearLayout(context).also { params ->
                params.layoutParams = ViewHelper.createLayoutParams().also {
                    it.topMargin = SizeHelper.size16
                    it.leftMargin = SizeHelper.size18
                    it.rightMargin = SizeHelper.size18
                }
                params.orientation = LinearLayout.HORIZONTAL

                params.addView(View(context).also { v ->
                    val layoutParams1 = ViewHelper.createLayoutParams(0, SizeHelper.size1, 1f)
                    layoutParams1.gravity = Gravity.BOTTOM
                    layoutParams1.setMargins(0, 0, 0, SizeHelper.size4)
                    v.layoutParams = layoutParams1
                    v.setBackgroundColor(ColorManager.getLineColor(context))
                })

                params.addView(AppCompatTextView(context).also { poin ->
                    poin.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                        it.leftMargin = SizeHelper.size4
                    }
                    poin.setTextColor(ColorManager.getAccentYellowColor(context))
                    poin.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
                    poin.textSize = 16f
                    poin.isSingleLine = true
                    poin.ellipsize = TextUtils.TruncateAt.END
                    poin.includeFontPadding = false
                })

                params.addView(View(context).also { v ->
                    val layoutParams2 = ViewHelper.createLayoutParams(0, SizeHelper.size1, 1f)
                    layoutParams2.setMargins(SizeHelper.size4, 0, 0, SizeHelper.size4)
                    layoutParams2.gravity = Gravity.BOTTOM
                    v.layoutParams = layoutParams2
                    v.setBackgroundColor(ColorManager.getLineColor(context))
                })
            })

            layoutParams.addView(AppCompatTextView(context).also { nameProduct ->
                nameProduct.layoutParams = ViewHelper.createLayoutParams().also {
                    it.topMargin = SizeHelper.size10
                    it.leftMargin = SizeHelper.size12
                    it.rightMargin = SizeHelper.size12
                }
                nameProduct.minLines = 2
                nameProduct.maxLines = 2
                nameProduct.ellipsize = TextUtils.TruncateAt.END
                nameProduct.includeFontPadding = false
                nameProduct.typeface = ViewHelper.createTypeface(context, R.font.barlow_medium)
                nameProduct.textSize = 14f
                nameProduct.setTextColor(ColorManager.getNormalTextColor(context))
            })

            layoutParams.addView(AppCompatTextView(context).also { btn ->
                btn.layoutParams = ViewHelper.createLayoutParams().also {
                    it.topMargin = SizeHelper.size16
                    it.leftMargin = SizeHelper.size12
                    it.rightMargin = SizeHelper.size12
                    it.bottomMargin = SizeHelper.size10
                }
                btn.setPadding(0, SizeHelper.size4, 0, SizeHelper.size4)
                btn.setText(R.string.them_vao_gio_qua)
                btn.setTextColor(ContextCompat.getColor(context, R.color.white))
                btn.includeFontPadding = false
                btn.isSingleLine = true
                btn.gravity = Gravity.CENTER
                btn.ellipsize = TextUtils.TruncateAt.END
                btn.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(btn.context)
                btn.typeface = ViewHelper.createTypeface(context, R.font.barlow_semi_bold)
                btn.textSize = 14f
            })
        }
    }
}