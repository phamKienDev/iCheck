package vn.icheck.android.loyalty.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import vn.icheck.android.loyalty.screen.gift_voucher.GiftDetailFromAppActivity
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
        private val isCoin: Boolean = true
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
                                         isCoin: Boolean = true) {
            DialogReceiveGiftSuccess(activity, title, nameGift, message, nameCampaign, image, listGift, winnerId, isCoin).show(activity.supportFragmentManager, null)
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

        tvTitle.text = title ?: "Chúc mừng bạn"

        WidgetHelper.loadImageUrl(imgGift, image)

        tvGift.text = nameGift ?: getString(R.string.dang_cap_nhat)

        tvMessage.text = message ?: "Cảm ơn bạn đã tham gia sự kiện"

        tvNameCampaign.text = nameCampaign ?: getString(R.string.dang_cap_nhat)

        recyclerView.layoutManager = LinearLayoutManager(context)


        btnViewGift.run {
            if (isCoin) {
                text = "Quản lý Xu"

                setOnClickListener {
                    dismiss()
                    LoyaltySdk.openActivity("point_transitions")
                }
            } else {
                if (listGift.isNullOrEmpty()) {
                    text = "Xem quà"

                    setOnClickListener {
                        dismiss()
                        ActivityHelper.startActivity<GiftDetailFromAppActivity, Long>(requireActivity(), ConstantsLoyalty.DATA_1, winnerId)
                    }
                } else {
                    if (listGift.size > 1) {
                        recyclerView.adapter = DialogReceiveGiftAdapter(listGift)

                        text = "Xem kho quà"

                        setOnClickListener {
                            dismiss()
                            LoyaltySdk.openActivity("my_rewards")
                        }
                    } else {
                        text = "Xem quà"

                        setOnClickListener {
                            dismiss()
                            ActivityHelper.startActivity<GiftDetailFromAppActivity, Long>(requireActivity(), ConstantsLoyalty.DATA_1, winnerId)
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

                dialog.findViewById<AppCompatTextView>(R.id.tvTitle).text = title ?: "Chúc mừng bạn"

                WidgetHelper.loadImageUrl(dialog.findViewById<AppCompatImageView>(R.id.imgGift), image)

                dialog.findViewById<AppCompatTextView>(R.id.tvGift).text = nameGift
                        ?: getString(R.string.dang_cap_nhat)

                dialog.findViewById<AppCompatTextView>(R.id.tvMessage).text = message
                        ?: "Cảm ơn bạn đã tham gia sự kiện"

                dialog.findViewById<AppCompatTextView>(R.id.tvNameCampaign).text = nameCampaign
                        ?: getString(R.string.dang_cap_nhat)

                dialog.findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(context)


                dialog.findViewById<AppCompatTextView>(R.id.btnViewGift).run {
                    if (isCoin) {
                        text = "Quản lý Xu"
                    } else {
                        if (listGift.isNullOrEmpty()) {
                            text = "Xem quà"
                        } else {
                            if (listGift.size > 1) {
                                dialog.findViewById<RecyclerView>(R.id.recyclerView).adapter = DialogReceiveGiftAdapter(listGift)

                                text = "Xem kho quà"
                            } else {
                                text = "Xem quà"
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
            val path = MediaStore.Images.Media.insertImage(activity.contentResolver, mBitmap, "Anh chup man hinh", null)
            val uri = Uri.parse(path)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Chúc mừng ${SessionManager.session.user?.name} " + "đã trúng quà khi tham gia sự kiện của iCheck!")
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua app"))
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