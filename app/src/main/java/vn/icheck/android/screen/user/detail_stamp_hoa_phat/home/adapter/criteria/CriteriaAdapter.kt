package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.willy.ratingbar.BaseRatingBar
import kotlinx.android.synthetic.main.criteria_child.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.network.models.ProductCriteriaSet

class CriteriaAdapter(val listCriteria: List<CriteriaChild>, @IntRange(from = 0, to = 1) val size: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var criteriaAction: CriteriaAction? = null

    fun setAction(action: CriteriaAction) {
        this.criteriaAction = action
    }

    companion object {
        const val default = 0
        const val small = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (size == 0) CriteriaHolder.create(parent, listCriteria)
        else SmallCriteriaHolder.create(parent, listCriteria, criteriaAction)
    }

    override fun getItemCount(): Int = listCriteria.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (size == default) {
            (holder as CriteriaHolder).bind(listCriteria[position])
        } else {
            (holder as SmallCriteriaHolder).bind(listCriteria[position])
        }
    }

    class CriteriaHolder(view: View, val listCriteria: List<CriteriaChild>) : BaseHolder(view) {

        fun bind(criteriaChild: CriteriaChild) {
            view.criteria_name.text = criteriaChild.productCriteriaSet.criteria.name
            if (criteriaChild.edit) {
                view.rating_bar.setIsIndicator(true)
            }
            view.rating_bar.setOnRatingChangeListener(object : BaseRatingBar.OnRatingChangeListener {
                override fun onRatingChange(ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean) {
                    listCriteria[adapterPosition].point = rating
                }
            })
            if (criteriaChild.point != null) {
                view.rating_bar.rating = criteriaChild.point!!
            }
        }

        companion object {
            fun create(parent: ViewGroup, listCriteria: List<CriteriaChild>): CriteriaHolder {
                return CriteriaHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.criteria_child, parent, false), listCriteria)
            }
        }
    }

    class SmallCriteriaHolder(view: View, val listCriteria: List<CriteriaChild>, val action: CriteriaAction?) : BaseHolder(view) {

        fun bind(criteriaChild: CriteriaChild) {
            view.criteria_name.text = criteriaChild.productCriteriaSet.criteria.name
            if (criteriaChild.edit) {
                view.rating_bar.setIsIndicator(true)
            }
            view.rating_bar.setOnRatingChangeListener(object : BaseRatingBar.OnRatingChangeListener {
                override fun onRatingChange(ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean) {
                    listCriteria[adapterPosition].point = rating
                    action?.onClickCriteria(listCriteria)
                }
            })
            if (criteriaChild.point != null) {
                view.rating_bar.rating = criteriaChild.point!!
            }
        }

        companion object {
            fun create(parent: ViewGroup, listCriteria: List<CriteriaChild>, action: CriteriaAction?): SmallCriteriaHolder {
                return SmallCriteriaHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.criteria_small_child, parent, false), listCriteria, action)
            }
        }
    }

    interface CriteriaAction {
        fun onClickCriteria(listCriteria: List<CriteriaChild>)
    }
}


class CriteriaChild(
        val productCriteriaSet: ProductCriteriaSet,
        var point: Float?,
        var edit: Boolean = true
)