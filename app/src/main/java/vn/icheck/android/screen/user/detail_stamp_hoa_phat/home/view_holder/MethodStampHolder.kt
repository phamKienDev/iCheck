package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.method_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICViewType
import vn.icheck.android.util.kotlin.WidgetUtils

class MethodStampHolder(parent: ViewGroup, val headerImagelistener: SlideHeaderStampHoaPhatListener) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.method_stamp, parent, false)) {

    private val size35 = SizeHelper.dpToPx(35)
    private val size54 = SizeHelper.dpToPx(54)

    private var selectedLevel = 0
    private var currentPos = 0

    private var info = 0
    private var stepBuild = 0
    private var certificate = 0

    init {
        itemView.imageInfor.setOnClickListener {
            headerImagelistener.scrollWithViewType(ICViewType.INFORMATION_PRODUCT_STAMP)
            onSelect(info, stepBuild, certificate, 1)
        }

        itemView.tvSub1.setOnClickListener {
            headerImagelistener.scrollWithViewType(ICViewType.INFORMATION_PRODUCT_STAMP)
            onSelect(info, stepBuild, certificate, 1)
        }

        itemView.imgStepBuild.setOnClickListener {
            headerImagelistener.scrollWithViewType(ICViewType.STEP_BUILD_PRODUCT_STAMP)
            onSelect(info, stepBuild, certificate, 2)
        }

        itemView.tvSub2.setOnClickListener {
            headerImagelistener.scrollWithViewType(ICViewType.STEP_BUILD_PRODUCT_STAMP)
            onSelect(info, stepBuild, certificate, 2)
        }

        itemView.imgCccn.setOnClickListener {
            headerImagelistener.scrollWithViewType(ICViewType.CERTIFICATE_STAMP)
            onSelect(info, stepBuild, certificate, 3)
        }

        itemView.tvSub3.setOnClickListener {
            headerImagelistener.scrollWithViewType(ICViewType.CERTIFICATE_STAMP)
            onSelect(info, stepBuild, certificate, 3)
        }
    }

    private fun onSelect(info: Int, stepBuild: Int, certificate: Int, pos: Int) {
        if (selectedLevel != pos) {
            unSelectLevel(info, stepBuild, certificate)

            when {
                currentPos < info -> onSelectInfo(true)
                currentPos < stepBuild -> onSelectStepBuild(true)
                currentPos < certificate -> onSelectCertificate(true)
            }

            currentPos = pos

            when (pos) {
                1 -> {
                    onSelectInfo(true)
                }
                2 -> {
                    onSelectStepBuild(true)
                }
                3 -> {
                    onSelectCertificate(true)
                }
            }

            selectedLevel = pos
        }

    }

    private fun unSelectLevel(info: Int, stepBuild: Int, certificate: Int) {
        when (selectedLevel) {
            1 -> {
                onSelectInfo(false)
            }
            2 -> {
                onSelectStepBuild(false)
            }
            3 -> {
                onSelectCertificate(false)
            }
        }

        when {
            currentPos < info -> onSelectInfo(false)
            currentPos < stepBuild -> onSelectStepBuild(false)
            currentPos < certificate -> onSelectCertificate(false)
        }
    }

    private fun onSelectInfo(isSelected: Boolean) {
        val start: Int
        val end: Int

        if (isSelected) {
            itemView.imageInfor.setImageResource(R.drawable.ic_method_stamp1_big)
            start = itemView.imageInfor.width
            end = size54
        } else {
            itemView.imageInfor.setImageResource(R.drawable.ic_method_stamp1_small)
            start = itemView.imageInfor.width
            end = size35
        }
        WidgetUtils.changeValueAnimation(start, end, 200, ValueAnimator.AnimatorUpdateListener {
            val layoutParams = itemView.imageInfor.layoutParams as ConstraintLayout.LayoutParams

            if (isSelected){
                layoutParams.setMargins(0,SizeHelper.size8,0,SizeHelper.size6)
            } else {
                layoutParams.setMargins(0,SizeHelper.size23,0,SizeHelper.size6)
            }
            layoutParams.width = it.animatedValue as Int
            itemView.imageInfor.layoutParams = layoutParams
        })
    }

    private fun onSelectStepBuild(isSelected: Boolean) {
        val start: Int
        val end: Int

        if (isSelected) {
            itemView.imgStepBuild.setImageResource(R.drawable.ic_method_stamp2_big)
            start = itemView.imgStepBuild.width
            end = size54
        } else {
            itemView.imgStepBuild.setImageResource(R.drawable.ic_method_stamp2_small)
            start = itemView.imgStepBuild.width
            end = size35
        }
        WidgetUtils.changeValueAnimation(start, end, 200, ValueAnimator.AnimatorUpdateListener {
            val layoutParams = itemView.imgStepBuild.layoutParams as ConstraintLayout.LayoutParams
            if (isSelected){
                layoutParams.setMargins(0,SizeHelper.size8,0,SizeHelper.size6)
            } else {
                layoutParams.setMargins(0,SizeHelper.size23,0,SizeHelper.size6)
            }
            layoutParams.width = it.animatedValue as Int
            itemView.imgStepBuild.layoutParams = layoutParams
        })
    }

    private fun onSelectCertificate(isSelected: Boolean) {
        val start: Int
        val end: Int

        if (isSelected) {
            itemView.imgCccn.setImageResource(R.drawable.ic_method_stamp3_big)
            start = itemView.imgCccn.width
            end = size54
        } else {
            itemView.imgCccn.setImageResource(R.drawable.ic_method_stamp3_small)
            start = itemView.imgCccn.width
            end = size35
        }
        WidgetUtils.changeValueAnimation(start, end, 200, ValueAnimator.AnimatorUpdateListener {
            val layoutParams = itemView.imgCccn.layoutParams as ConstraintLayout.LayoutParams
            if (isSelected){
                layoutParams.setMargins(0,SizeHelper.size8,0,SizeHelper.size6)
            } else {
                layoutParams.setMargins(0,SizeHelper.size23,0,SizeHelper.size6)
            }
            layoutParams.width = it.animatedValue as Int
            itemView.imgCccn.layoutParams = layoutParams
        })
    }
}