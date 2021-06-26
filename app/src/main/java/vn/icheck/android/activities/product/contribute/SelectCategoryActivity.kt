package vn.icheck.android.activities.product.contribute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_category.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICCategory

class SelectCategoryActivity : BaseActivityMVVM() {

    lateinit var categoryAdapter: CategoryAdapter
    val compositeDisposable = CompositeDisposable()
    private var level = 1

    companion object {
        var instance: SelectCategoryActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)

        instance = this

        txtSearch.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(this)

        categoryAdapter = CategoryAdapter()
        rcv_category.adapter = categoryAdapter
        rcv_category.layoutManager = LinearLayoutManager(this)
        btnBack.setOnClickListener {
            if (level > 1) {
                level--
                queryLevel()
            } else {
                finish()
            }
        }
        queryLevel()

        txtSearch.imeOptions = EditorInfo.IME_ACTION_DONE

        txtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                categoryAdapter.filter.filter(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun queryLevel() {
        val query = hashMapOf<String, Any>()
        query["root"] = level.toString()
        ICNetworkClient.getApiClient().getListCategories(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ICListResponse<ICCategory>> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(t: ICListResponse<ICCategory>) {
                        val list = mutableListOf<CategoryChild>()

                        for (item in t.rows) {
                            list.add(CategoryChild(item))
                        }

                        categoryAdapter.setListData(list)
                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        compositeDisposable.clear()
        instance = null
    }

    fun changeCategory(icCategory: ICCategory) {
        if (level < 3) {
            val query = hashMapOf<String, Any>()
            query["parent_id"] = icCategory.id.toString()
            ICNetworkClient.getApiClient().getListCategories(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ICListResponse<ICCategory>> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onNext(t: ICListResponse<ICCategory>) {
                            if (t.rows.isNotEmpty()) {
                                val list = mutableListOf<CategoryChild>()

                                for (item in t.rows) {
                                    list.add(CategoryChild(item))
                                }

                                categoryAdapter.setListData(list)
                                level++
                            } else {
                                returnResult(icCategory)
                            }
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
        } else {
            returnResult(icCategory)
        }

    }

    private fun returnResult(icCategory: ICCategory) {
        val resultIntent = Intent()
        resultIntent.putExtra("name", icCategory.name)
        resultIntent.putExtra("id", icCategory.id)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    class CategoryChild(val icCategory: ICCategory)

    class CategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
        private val listData = mutableListOf<CategoryChild>()
        private val listDataFilter = mutableListOf<CategoryChild>()

        fun setListData(list: MutableList<CategoryChild>) {
            listData.clear()
            listData.addAll(list)

            listDataFilter.clear()
            listDataFilter.addAll(list)

            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CategoryHolder.create(parent)
        }

        override fun getItemCount(): Int {
            return listDataFilter.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CategoryHolder).bind(listDataFilter[position])
        }

        override fun getFilter(): Filter {
            return filterCategory
        }

        private val filterCategory = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                listDataFilter.clear()

                if (constraint == null || constraint.isEmpty()) {
                    listDataFilter.addAll(listData)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                    for (item in listData) {
                        if (!item.icCategory.name.isNullOrEmpty()) {
                            if (item.icCategory.name!!.toLowerCase().contains(filterPattern)) {
                                listDataFilter.add(item)
                            }
                        }
                    }
                }

                val results = FilterResults()
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }

        class CategoryHolder(val view: View) : RecyclerView.ViewHolder(view) {

            fun bind(categoryChild: CategoryChild) {
                view.findViewById<TextView>(R.id.tv_category_name).text = categoryChild.icCategory.name
                view.findViewById<ViewGroup>(R.id.vg_parent).setOnClickListener {
                    instance?.changeCategory(categoryChild.icCategory)
                }
            }

            companion object {
                fun create(parent: ViewGroup): CategoryHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.category_holder, parent, false)
                    return CategoryHolder(view)
                }
            }
        }
    }
}
