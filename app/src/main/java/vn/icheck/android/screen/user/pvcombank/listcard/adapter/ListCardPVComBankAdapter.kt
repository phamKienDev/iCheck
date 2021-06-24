package vn.icheck.android.screen.user.pvcombank.listcard.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemCardBankBinding
import vn.icheck.android.databinding.ItemErrorPvcombankBinding
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.user.pvcombank.listcard.callbacks.CardPVComBankListener
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.ToastUtils

class ListCardPVComBankAdapter(private val listener: CardPVComBankListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICListCardPVBank>()

    private var errorCode = 0

    private val itemType = 1
    private val errorType = 2

    fun setListData(list: MutableList<ICListCardPVBank>) {
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setLockCard(id: String, position: Int, isLock: Boolean) {
        for (i in listData) {
            if (i.cardId == id) {
                i.isLock = isLock
                notifyItemChanged(position)
            }
        }
    }

    fun setDefaultCard(cardId: String, pos: Int) {
        for (i in listData) {
            if (i.cardId == cardId) {
                i.isDefault = true
                notifyItemChanged(pos)
            } else {
                i.isDefault = false
            }
        }
    }

    fun showHide(cardId: String?, isShow: Boolean, position: Int, cardFull: String) {
        for (i in listData) {
            if (i.cardId == cardId) {
                i.isShow = isShow
                if (cardFull.isNotEmpty()) {
                    i.cardMasking = cardFull
                }
                notifyItemChanged(position)
            } else {
                i.isDefault = false
            }
        }
    }

    fun setErrorCode(error: Int) {
        listData.clear()
        errorCode = error
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent)
            else -> ErrorHolder(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            itemType
        } else {
            errorType
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            listData.size
        } else {
            1
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)
            }

            is ErrorHolder -> {
                holder.bind(errorCode)

                holder.itemView.setOnClickListener {
                    listener.onRefresh()
                }
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup, val binding: ItemCardBankBinding = ItemCardBankBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICListCardPVBank>(binding.root) {
        private var expDate = ""
        private var validFrom = ""

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICListCardPVBank) {
            listener(obj)

            if (!obj.expDate.isNullOrEmpty() && obj.expDate!!.length == 6) {
                val repYear = obj.expDate!!.substring(0, 4)
                val lastYear = repYear.substring(2, 4)
                val repMonth = obj.expDate!!.substring(4, 6)
                expDate = "$repMonth/$lastYear"

                val vFrom = (repYear.toLong() - 5).toString()
                val lastVFrom = vFrom.substring(2, 4)
                validFrom = "$repMonth/$lastVFrom"
            } else {
                expDate = ""
                validFrom = ""
            }

            if (obj.isShow) {
                binding.btnShowHide.setImageResource(R.drawable.ic_eye_off_white_24px)
                binding.tvMoney.setText(R.string.s_d, TextHelper.formatMoney(obj.avlBalance ?: "0"))
                binding.tvCardNumber.text = obj.cardMasking ?: getString(R.string.dang_cap_nhat)
                binding.tvName.text = obj.embossName ?: getString(R.string.dang_cap_nhat)
                binding.tvDateEnd.text = expDate
                binding.tvCCV.setText(R.string.ccv)

                binding.tvAvlBalance.setText(R.string.s_d, TextHelper.formatMoney(obj.avlBalance ?: "0"))
                binding.tvNumberCard.text = obj.cardMasking ?: getString(R.string.dang_cap_nhat)
                binding.tvExpDate.text = expDate
            } else {
                binding.btnShowHide.setImageResource(R.drawable.ic_eye_on_white_24px)
                binding.tvMoney.setText(R.string.d)
                binding.tvCardNumber.text = "**** **** **** ****"
                binding.tvDateEnd.text = "**/**"
                binding.tvName.text = "**********"
                binding.tvCCV.setText(R.string.ccv)

                binding.tvAvlBalance.text = "**********"
                binding.tvNumberCard.text = "**** **** **** ****"
                binding.tvExpDate.text = "**/**"
            }

            updateWidth(binding.tvDateEnd)
            updateWidth(binding.tvCCV)
        }

        private fun updateWidth(textView: AppCompatTextView) {
            textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    textView.layoutParams.apply {
                        width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    }
                    textView.requestLayout()
                }
            })
        }

        private fun listener(item: ICListCardPVBank) {
            binding.btnShowHide.setOnClickListener {
                listener.onClickShowOrHide(item, absoluteAdapterPosition)
            }

            binding.btnCopyCard.setOnClickListener {
                if (item.isShow) {
                    val clipboard = itemView.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(itemView.context.getString(R.string.ma_the), binding.tvNumberCard.text.toString())
                    clipboard.setPrimaryClip(clip)
                    ToastUtils.showShortSuccess(itemView.context, itemView.context.getString(R.string.da_copy_ma_the))
                } else {
                    ToastUtils.showShortError(itemView.context, itemView.context.getString(R.string.ban_can_phai_hien_thi_day_du_thong_tin))
                }
            }

            binding.btnEndDate.setOnClickListener {
                if (item.isShow) {
                    val clipboard = itemView.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(itemView.context.getString(R.string.han_su_dung), binding.tvExpDate.text.toString())
                    clipboard.setPrimaryClip(clip)
                    ToastUtils.showShortSuccess(itemView.context, itemView.context.getString(R.string.da_copy_han_su_dung))
                } else {
                    ToastUtils.showShortError(itemView.context, itemView.context.getString(R.string.ban_can_phai_hien_thi_day_du_thong_tin))
                }
            }

            binding.btnUseDefault.setOnClickListener {
                listener.onClickUseDefaulCard(item, absoluteAdapterPosition)
            }

            binding.tvLockCard.setOnClickListener {
                listener.onClickLockCard(item, absoluteAdapterPosition)
            }

            binding.tvUnLockCard.setOnClickListener {
                listener.onClickUnLockCard(item, absoluteAdapterPosition)
            }

            binding.tvChangePassword.setOnClickListener {
                listener.onClickChangePassword(item, absoluteAdapterPosition)
            }

            binding.tvInfoCard.setOnClickListener {
                if (item.cardMasking?.contains("*") == true) {
                   listener.onClickShow(item, absoluteAdapterPosition)
                }
            }
            binding.tvActionAuthen.setOnClickListener {
                listener.onAuthenCard(item)
            }
        }
    }

    inner class ErrorHolder(parent: ViewGroup, val binding: ItemErrorPvcombankBinding = ItemErrorPvcombankBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<Int>(binding.root) {

        override fun bind(obj: Int) {
            when (obj) {
                Constant.ERROR_EMPTY -> {
                    binding.btnTryAgain.visibility = View.INVISIBLE
                    binding.imgIcon.setImageResource(0)
                    binding.tvTitle.text = getString(R.string.ban_khong_co_the_nao)
                }

                Constant.ERROR_SERVER -> {
                    binding.btnTryAgain.visibility = View.VISIBLE
                    binding.imgIcon.setImageResource(R.drawable.ic_error_request)
                    binding.tvTitle.text = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                }

                Constant.ERROR_INTERNET -> {
                    binding.btnTryAgain.visibility = View.VISIBLE
                    binding.imgIcon.setImageResource(R.drawable.ic_error_network)
                    binding.tvTitle.text = getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai)
                }
            }
        }
    }
}