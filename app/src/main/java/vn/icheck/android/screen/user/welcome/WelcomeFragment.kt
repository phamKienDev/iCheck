package vn.icheck.android.screen.user.welcome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fragment_welcome.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.home.HomeActivity

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */

class WelcomeFragment : Fragment() {

    companion object {
        fun newInstance(status: Int): WelcomeFragment {
            val fragment = WelcomeFragment()

            val bundle = Bundle()
            bundle.putInt(Constant.DATA_1, status)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        /**
         * setup design cho viewpager
         */
        when (arguments?.getInt(Constant.DATA_1, -1)) {
            WelcomeActivity.type1 -> {
                view.txtWelcome.textSize = 18f
                view.txtWelcome.text = requireContext().getString(R.string.vi_sao_ban_nen_dung_icheck)
                view.imgWelcome.setImageResource(R.drawable.img_welcome_1)
            }
            WelcomeActivity.type2 -> {
                view.txtWelcome.text = requireContext().getString(R.string.san_pham_chinh_hang_danh_gia_tan_tam)
                view.imgWelcome.setImageResource(R.drawable.img_welcome_2)
            }
            WelcomeActivity.type3 -> {
                view.txtWelcome.text = requireContext().getString(R.string.minh_bach_thong_tin_truy_xuat_nguon_goc)
                view.imgWelcome.setImageResource(R.drawable.img_welcome_3)
            }
//            WelcomeActivity.type4 -> {
//                view.txtWelcome.text = context!!.getString(R.string.quet_mua_nhanh_voi_scan_buy)
//                view.imgWelcome.setImageResource(R.drawable.img_welcome_4)
//            }
            WelcomeActivity.type5 -> {
                view.txtWelcome.text = requireContext().getString(R.string.ung_y_san_pham_mua_ngay_chinh_hang)
                view.imgWelcome.setImageResource(R.drawable.img_welcome_5)
            }
            WelcomeActivity.type6 -> {
                view.btnHome.visibility = View.VISIBLE

                val margin = view.txtWelcome.layoutParams as ViewGroup.MarginLayoutParams
                margin.setMargins(0, 0, 0, SizeHelper.size12)

                view.txtWelcome.text = requireContext().getString(R.string.quet_ma_tich_diem_doi_qua)
                view.imgWelcome.setImageResource(R.drawable.img_welcome_6)

                view.btnHome.setOnClickListener {
                    setupSharedPreferences()
                    activity?.waveLoadingView!!.pauseAnimation()
                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        view.btnHome.background = ViewHelper.bgPrimaryOutline3Corners20(requireContext())
        return view
    }

    /**
     * Lưu lại trạng thái mở app lần đầu tiên
     */
    private fun setupSharedPreferences() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        edit.putBoolean("first_run", false)
        edit.apply()
    }
}