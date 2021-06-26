package vn.icheck.android.loyalty.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_receive_gift_success.*
import kotlinx.android.synthetic.main.item_receive_gift_success.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.PermissionHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKGift
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.gift_detail_from_app.GiftDetailFromAppActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.accept_ship_gift.AcceptShipGiftActivity
import vn.icheck.android.loyalty.screen.web.WebViewActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk


class DialogReceiveGiftSuccess(
        private val activity: Activity,
        private val title: String?,
        private val nameGift: String?,
        private val message: String?,
        private val nameCampaign: String?,
        private val image: String?,
        private val listGift: MutableList<ICKGift>?,
        private val winnerId: Long,
        private val isCoin: Boolean = true,
        private val isVoucher: Boolean = true
) : DialogFragment() {


    companion object {
        fun showDialogReceiveGiftSuccess(activity: FragmentActivity,
                                         title: String?,
                                         nameGift: String?,
                                         message: String?,
                                         nameCampaign: String?,
                                         image: String?,
                                         listGift: MutableList<ICKGift>?,
                                         winnerId: Long,
                                         isCoin: Boolean = true,
                                         isVoucher: Boolean = true) {
            DialogReceiveGiftSuccess(activity, title, nameGift, message, nameCampaign, image, listGift, winnerId, isCoin, isVoucher).show(activity.supportFragmentManager, null)
        }
    }

    private val requestShare = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_receive_gift_success, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onInitView()
    }

    @SuppressLint("SetTextI18n")
    fun onInitView() {
        btnClose.setOnClickListener {
            dismiss()
        }

        if (listGift.isNullOrEmpty()) {
            layoutGift.setVisible()
            recyclerView.setGone()
        } else {
            if (listGift.size > 1) {
                layoutGift.setGone()
                recyclerView.setVisible()
            } else {
                layoutGift.setVisible()
                recyclerView.setGone()
            }
        }

        tvTitle.apply {
            text = title ?: context.getString(R.string.chuc_mung_ban)
        }

        WidgetHelper.loadImageUrl(imgGift, image)

        tvGift.text = nameGift ?: getString(R.string.dang_cap_nhat)

        tvMessage.apply {
            text = message ?: context.getString(R.string.cam_on_ban_da_tham_gia_su_kien)
        }

        tvNameCampaign.text = nameCampaign ?: getString(R.string.dang_cap_nhat)

        recyclerView.layoutManager = LinearLayoutManager(context)

        btnCTCC.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG

            setOnClickListener {
                startActivity(Intent(activity, WebViewActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, message)
                    putExtra(ConstantsLoyalty.DATA_3, context.getString(R.string.thong_tin_chuong_trinh))
                })
            }
        }

        btnViewGift.run {
            when {
                isCoin -> {
                    text = context.getString(R.string.quan_ly_xu)

                    setOnClickListener {
                        dismiss()
                        LoyaltySdk.openActivity("point_transitions")
                    }
                }
                isVoucher -> {
                    text = context.getString(R.string.nhan_qua)

                    setOnClickListener {
                        dismiss()
                        startActivity(Intent(requireContext(), AcceptShipGiftActivity::class.java).apply {
                            putExtra(ConstantsLoyalty.DATA_2, winnerId)
                            putExtra(ConstantsLoyalty.TYPE, 3)
                        })
                    }
                }
                else -> {
                    if (listGift.isNullOrEmpty()) {
                        text = context.getString(R.string.xem_qua)

                        setOnClickListener {
                            dismiss()
                            ActivityHelper.startActivity<GiftDetailFromAppActivity, Long>(requireActivity(), ConstantsLoyalty.DATA_1, winnerId)
                        }
                    } else {
                        if (listGift.size > 1) {
                            recyclerView.adapter = DialogReceiveGiftAdapter(listGift)

                            text = context.getString(R.string.xem_kho_qua)

                            setOnClickListener {
                                dismiss()
                                LoyaltySdk.openActivity("my_rewards")
                            }
                        } else {
                            text = context.getString(R.string.xem_qua)

                            setOnClickListener {
                                dismiss()
                                ActivityHelper.startActivity<GiftDetailFromAppActivity, Long>(requireActivity(), ConstantsLoyalty.DATA_1, winnerId)
                            }
                        }
                    }
                }
            }
        }

        btnShare.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestShare)
        }
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun shareGift(activity: Activity) {
        lifecycleScope.launch {
            delay(240)

            val viewGroup = (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
                val dialog = LayoutInflater.from(activity).inflate(R.layout.dialog_receive_gift_success, this, false)
                addView(dialog)

                dialog.findViewById<LinearLayout>(R.id.root_shot).setBackgroundColor(ContextCompat.getColor(activity, R.color.black_10))

                if (listGift.isNullOrEmpty()) {
                    dialog.findViewById<ConstraintLayout>(R.id.layoutGift).setVisible()
                    dialog.findViewById<RecyclerView>(R.id.recyclerView).setGone()
                } else {
                    if (listGift.size > 1) {
                        dialog.findViewById<ConstraintLayout>(R.id.layoutGift).setGone()
                        dialog.findViewById<RecyclerView>(R.id.recyclerView).setVisible()
                    } else {
                        dialog.findViewById<ConstraintLayout>(R.id.layoutGift).setVisible()
                        dialog.findViewById<RecyclerView>(R.id.recyclerView).setGone()
                    }
                }

                dialog.findViewById<AppCompatTextView>(R.id.tvTitle).apply {
                    text = title ?: context.getString(R.string.chuc_mung_ban)
                }

                WidgetHelper.loadImageUrl(dialog.findViewById<AppCompatImageView>(R.id.imgGift), image)

                dialog.findViewById<AppCompatTextView>(R.id.tvGift).apply {
                    text = nameGift ?: context.getString(R.string.dang_cap_nhat)
                }

                dialog.findViewById<AppCompatTextView>(R.id.tvMessage).apply {
                    text = message ?: context.getString(R.string.cam_on_ban_da_tham_gia_su_kien)
                }

                dialog.findViewById<AppCompatTextView>(R.id.tvNameCampaign).apply {
                    text = nameCampaign ?: context.getString(R.string.dang_cap_nhat)
                }

                dialog.findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(context)


                dialog.findViewById<AppCompatTextView>(R.id.btnViewGift).run {
                    if (isCoin) {
                        text = context.getString(R.string.quan_ly_xu)
                    } else {
                        if (listGift.isNullOrEmpty()) {
                            text = context.getString(R.string.xem_qua)
                        } else {
                            if (listGift.size > 1) {
                                dialog.findViewById<RecyclerView>(R.id.recyclerView).adapter = DialogReceiveGiftAdapter(listGift)

                                text = context.getString(R.string.xem_kho_qua)
                            } else {
                                text = context.getString(R.string.xem_qua)
                            }
                        }
                    }
                }
            }

            delay(400)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "*/*"
            val mBitmap = screenShot(viewGroup)
            viewGroup.removeView(viewGroup.findViewById<ViewGroup>(R.id.root_shot))
            val path = MediaStore.Images.Media.insertImage(activity.contentResolver, mBitmap, getString(R.string.anh_chup_man_hinh), null)
            val uri = Uri.parse(path)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.chuc_mung_s_da_chung_qua_khi_tham_gia_su_kien_cua_icheck, SessionManager.session.user?.name))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.chia_se_qua_app)))
        }
    }

    private fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestShare) {
            if (PermissionHelper.checkResult(grantResults)) {
                shareGift(activity)
            } else {
                ToastHelper.showLongWarning(requireContext(), R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }
}

class DialogReceiveGiftAdapter(val listData: MutableList<ICKGift>) : RecyclerView.Adapter<DialogReceiveGiftAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogReceiveGiftAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: DialogReceiveGiftAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKGift>(R.layout.item_receive_gift_success, parent) {
        override fun bind(obj: ICKGift) {
            WidgetHelper.loadImageUrl(itemView.imgGift, obj.image?.medium)

            itemView.tvNameGift.text = obj.name ?: getString(R.string.dang_cap_nhat)
        }
    }
}