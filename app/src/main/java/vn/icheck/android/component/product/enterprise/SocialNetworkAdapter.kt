package vn.icheck.android.component.product.enterprise

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.rText

class SocialNetworkAdapter(val type: String) : RecyclerView.Adapter<SocialNetworkAdapter.SocialNetworkHolderLinear>() {
    val listData = mutableListOf<ICSocialNetworkModel>()

    fun setData(list: MutableList<ICSocialNetworkModel>){
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialNetworkHolderLinear {
        return SocialNetworkHolderLinear(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: SocialNetworkHolderLinear, position: Int) {
        holder.bind(listData[position])
    }

    inner class SocialNetworkHolderLinear(parent: ViewGroup) : BaseViewHolder<ICSocialNetworkModel>(ViewHelper.createItemSocialNetwork(parent.context)) {
        override fun bind(obj: ICSocialNetworkModel) {
            val params = itemView as LinearLayout
            val imgSocialNetwork = params.getChildAt(0) as AppCompatImageView
            val tvSocialNetwork = params.getChildAt(1) as AppCompatTextView

            if (type == "Grid") {
                params.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size8, SizeHelper.size4, 0, 0)
                }
            } else {
                params.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size8, SizeHelper.size4, 0, 0)
                }
            }

            if (!obj.title.isNullOrEmpty()) {
                tvSocialNetwork.text = obj.title
            }
            if (obj.image != null) {
                imgSocialNetwork.setImageResource(obj.image!!)
            }

            params.setOnClickListener {
                when (obj.title) {
                    "Facebook" -> {
                        onOpenWebsite(obj.social)
                    }
                    "Mail" -> {
                        onClickEmailPage(obj.social)
                    }
                    "Website" -> {
                        onOpenWebsite(obj.social)
                    }
                    "Youtube" -> {
                        openYoutubeLink(obj.social)
                    }
                }
            }
        }

        private fun openYoutubeLink(youtubeID: String?) {
            val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$youtubeID"))
            val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$youtubeID"))
            ICheckApplication.currentActivity()?.let { act ->
                try {
                    act.startActivity(intentApp)
                } catch (ex: ActivityNotFoundException) {
                    act.startActivity(intentBrowser)
                }
            }

        }

        private fun onClickEmailPage(email: String?) {
            val mailIntent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse("mailto:?to=${email}")
            mailIntent.data = data
            try {
                itemView.context.startActivity(Intent.createChooser(mailIntent, itemView.context rText R.string.send_mail))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun onOpenWebsite(obj: String?) {
            ICheckApplication.currentActivity()?.let { act ->
                val intent = Intent(act, WebViewActivity::class.java)
                intent.putExtra(Constant.DATA_1, obj)
                act.startActivity(intent)
                act.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
            }
        }
    }
}