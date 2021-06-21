package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.accuracy_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDetailStampV6_1
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.rText

class AccuracyStampHolder(parent: ViewGroup) : BaseViewHolder<ICDetailStampV6_1.ICObjectDetailStamp>(LayoutInflater.from(parent.context).inflate(R.layout.accuracy_stamp, parent, false)) {
    @SuppressLint("SetTextI18n")
    override fun bind(obj: ICDetailStampV6_1.ICObjectDetailStamp) {
        if (obj.show_analytic == 0) {
            itemView.layoutSerial.visibility = View.VISIBLE
            itemView.layoutheaderAccuracy.visibility = View.GONE
            itemView.layoutAnalytic.visibility = View.GONE
            val serial = getSerialNumber(obj.count?.prefix, obj.count?.number)
            itemView.tvSerial.rText(R.string.serial_s, serial)
        } else {
            if (obj.scan_message != null) {
                when (obj.scan_message?.is_success) {
                    //fake
                    0 -> {
                        itemView.layoutheaderAccuracy.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_top_red_10_hoa_phat)
                        itemView.tvSubName.text = obj.scan_message?.text
                        itemView.imgAccuracy.setImageResource(R.drawable.ic_stamp_fake_hoa_phat)
                        obj.count?.let {
                            val verifiedSerial = getSerialNumber(it.prefix, it.number)
                            itemView.tvSerial.rText(R.string.serial_s, verifiedSerial)

                            itemView.tvCountScan.text = if (!it.scan_count.toString().isNullOrEmpty()) {
                                it.scan_count.toString()
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }

                            itemView.tvCountUserScan.text = if (!it.people_count.toString().isNullOrEmpty()) {
                                it.people_count.toString()
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }

                        }
                    }
                    //verified
                    1 -> {
                        itemView.layoutheaderAccuracy.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_top_green_10)
                        itemView.tvSubName.text = obj.scan_message?.text
                        itemView.imgAccuracy.setImageResource(R.drawable.ic_verified_stamp_36dp)
                        obj.count?.let {
                            val verifiedSerial = getSerialNumber(it.prefix, it.number)
                            itemView.tvSerial.rText(R.string.serial_s, verifiedSerial)

                            itemView.tvCountScan.text = if (!it.scan_count.toString().isEmpty()) {
                                it.scan_count.toString()
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }

                            itemView.tvCountUserScan.text = if (!it.people_count.toString().isEmpty()) {
                                it.people_count.toString()
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }
                        }
                    }
                    //Guarantee
                    2 -> {
                        itemView.layoutheaderAccuracy.background = ViewHelper.bgWhiteCornersTop10(itemView.context)
                        itemView.tvSubName.setTextColor(Color.parseColor("#434343"))
                        itemView.tvSubName.text = obj.scan_message?.text
                        itemView.imgAccuracy.setImageResource(R.drawable.ic_verified_stamp_36dp)
                        obj.count?.let {
                            val verifiedSerial = getSerialNumber(it.prefix, it.number)
                            itemView.tvSerial.rText(R.string.serial_s, verifiedSerial)

                            itemView.tvCountScan.text = if (!it.scan_count.toString().isNullOrEmpty()) {
                                it.scan_count.toString()
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }

                            itemView.tvCountUserScan.text = if (!it.people_count.toString().isNullOrEmpty()) {
                                it.people_count.toString()
                            } else {
                                itemView.context.getString(R.string.dang_cap_nhat)
                            }
                        }
                    }
                }
            } else {
                obj.count?.let {
                    itemView.layoutheaderAccuracy.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_corner_top_yellow_10_v6_1)
                    itemView.layoutheaderAccuracy.beGone()
                    itemView.layoutAnalytic.beGone()
                    val verifiedSerial = getSerialNumber(it.prefix, it.number)
                    itemView.tvSerial.rText(R.string.serial_s, verifiedSerial)

                    itemView.tvCountScan.text = if (!it.scan_count.toString().isNullOrEmpty()) {
                        it.scan_count.toString()
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }

                    itemView.tvCountUserScan.text = if (!it.people_count.toString().isNullOrEmpty()) {
                        it.people_count.toString()
                    } else {
                        itemView.context.getString(R.string.dang_cap_nhat)
                    }
                }
            }
        }
    }

    private fun getSerialNumber(prefix: String?, number: Long? = 0): String {
        val length = number.toString().length // 2

        var numberSerial = ""

        if (length <= 6) {
            for (i in 0 until (6 - length)) {
                numberSerial += "0"
            }
        }

        return "$prefix - $numberSerial$number"
    }
}