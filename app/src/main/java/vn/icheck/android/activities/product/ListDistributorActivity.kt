package vn.icheck.android.activities.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list_distributor.*
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.activities.base.BaseICActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.bookmark.ICBookmarkPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class ListDistributorActivity : BaseICActivity() {

    companion object {
        var instance: ListDistributorActivity? = null
        fun start(productId: Long?, activity: Activity) {
            val intent = Intent(activity, ListDistributorActivity::class.java)
            if (productId != null) {
                intent.putExtra("productId", productId)
            }
            activity.startActivity(intent)
        }
    }

    lateinit var pageAdapter: PageAdapter
    val listChild = mutableListOf<PageChild>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_distributor)
        instance = this
        requiresLogin = false
        lifecycleScope.launch {
            try {
                val id = intent.getLongExtra("productId", 0L)
                val result = ICNetworkClient.getSimpleApiClient().getDistributors(id)
                progress_loading.visibility = View.GONE
                rcv_product.visibility = View.VISIBLE
                for (item in result.rows) {
                    listChild.add(PageChild(item))
                }
                pageAdapter = PageAdapter(listChild)
                rcv_product.adapter = pageAdapter
                rcv_product.layoutManager = LinearLayoutManager(this@ListDistributorActivity)
            } catch (e: Exception) {
                progress_loading.visibility = View.GONE
                error_network.visibility = View.VISIBLE
            }
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun showPadeDetail(page: ICBookmarkPage.Rows) {
        ActivityUtils.startActivity<PageDetailActivity, Long>(this@ListDistributorActivity, Constant.DATA_1, page.page_id!!)
    }

    class PageChild(val page: ICBookmarkPage.Rows)

    class PageAdapter(val listData: List<PageChild>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return PageHolder.create(parent)
        }

        override fun getItemCount(): Int {
            return listData.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PageHolder).bind(listData[position])
        }

        class PageHolder(val view: View) : RecyclerView.ViewHolder(view) {

            fun bind(pageChild: PageChild) {
                val page = pageChild.page.page
                view.findViewById<ViewGroup>(R.id.root).setOnLongClickListener {
                    //                    ListDistributorActivity.instance?.confirmDelete(adapterPosition)
                    return@setOnLongClickListener true
                }
//                view.findViewById<TextView>(R.id.tv_owner_name).text = page.name

                if (page.verified != null) {
                    if (page.verified) {
                        view.findViewById<LinearLayout>(R.id.owner_name_vg).background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_owner_verified)
                    } else {
                        view.findViewById<LinearLayout>(R.id.owner_name_vg).background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_owner)
                    }
                } else {
                    view.findViewById<LinearLayout>(R.id.owner_name_vg).background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_owner)
                }

                if (pageChild.page.title != null) {
                    if (!pageChild.page.title.title.isNullOrEmpty() && !page.name.isNullOrEmpty()) {
                        if (!pageChild.page.title.title.isNullOrEmpty() && page.name.isNullOrEmpty()) {
                            view.findViewById<TextView>(R.id.tv_owner_name).text = pageChild.page.title.title
                        } else if (pageChild.page.title.title.isNullOrEmpty() && !page.name.isNullOrEmpty()) {
                            view.findViewById<TextView>(R.id.tv_owner_name).text = page.name
                        } else {
                            view.findViewById<TextView>(R.id.tv_owner_name).text = page.name + "\n" + pageChild.page.title.title
                        }
                    } else {
                        view.findViewById<TextView>(R.id.tv_owner_name).text = itemView.context.getText(R.string.dang_cap_nhat)
                    }
                } else {
                    view.findViewById<TextView>(R.id.tv_owner_name).text = itemView.context.getText(R.string.dang_cap_nhat)
                }

                if (page.gln_code != null) {
                    view.findViewById<TextView>(R.id.tv_owner_mst).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.tv_owner_mst).text = "MST: ${page.gln_code}"
                } else {
                    view.findViewById<TextView>(R.id.tv_owner_mst).visibility = View.GONE
                }

                view.findViewById<TextView>(R.id.tv_owner_address).text = page.address
                if (!page.website.isNullOrEmpty()) {
                    view.findViewById<TextView>(R.id.tv_owner_website).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.tv_owner_website).text = page.website
                } else {
                    view.findViewById<TextView>(R.id.tv_owner_website).visibility = View.GONE
                }
                if (!page.phone.isNullOrEmpty()) {
                    view.findViewById<TextView>(R.id.tv_owner_phone).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.tv_owner_phone).text = page.phone
                } else {
                    view.findViewById<TextView>(R.id.tv_owner_phone).visibility = View.GONE
                }
                if (!page.email.isNullOrEmpty()) {
                    view.findViewById<TextView>(R.id.tv_owner_mail).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.tv_owner_mail).text = page.email
                } else {
                    view.findViewById<TextView>(R.id.tv_owner_mail).visibility = View.GONE
                }
                view.findViewById<ViewGroup>(R.id.root).setOnClickListener {
                    instance?.showPadeDetail(pageChild.page)
                }
            }

            companion object {
                fun create(parent: ViewGroup): PageHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.bookmark_page_holder, parent, false)
                    return PageHolder(view)
                }
            }
        }
    }
}
