package vn.icheck.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.icheck.android.R
import vn.icheck.android.activities.product.contribute.ContributeProductActivity
import vn.icheck.android.adapters.base.BaseHolder

class ContributeImageAdapter(val data: List<ContributeProductActivity.ContributeImageChild>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val FRONT_HOLDER = 1
        const val BACK_HOLDER = 2
        const val FRONT_IMAGE = 3
        const val BACK_IMAGE = 4
        const val ADD_MORE = 5
        const val NORMAL_IMAGE = 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FRONT_HOLDER -> FrontHolder.create(parent)
            BACK_HOLDER -> BackHolder.create(parent)
            FRONT_IMAGE -> FrontImgHolder.create(parent)
            BACK_IMAGE -> BackImgHolder.create(parent)
            ADD_MORE -> AddOtherHolder.create(parent)
            else -> FrontHolder.create(parent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data.get(index = position).type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            FRONT_IMAGE -> (holder as FrontImgHolder).bind(data[position])
            BACK_IMAGE -> (holder as BackImgHolder).bind(data[position])
        }
    }

    class AddOtherHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.findViewById<ViewGroup>(R.id.root).setOnClickListener {
                ContributeProductActivity.instance?.pickImage(itemViewType)
            }
        }

        companion object {
            fun create(parent: ViewGroup): AddOtherHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.add_other_img, parent, false)
                return AddOtherHolder(view)
            }
        }
    }

    class FrontImgHolder(view: View) : BaseHolder(view) {

        fun bind(contributeImageChild: ContributeProductActivity.ContributeImageChild) {
            Glide.with(view.context.applicationContext)
                    .load(contributeImageChild.url)
                    .placeholder(R.drawable.update_product_holder)
                    .into(view.findViewById(R.id.img_front))
            if (contributeImageChild.canReplace) {
                if (adapterPosition == 0) {
                    inviView(R.id.btn_delete)
                } else {
                    setOnClick(R.id.btn_delete, View.OnClickListener {
                        ContributeProductActivity.instance?.removeImage(adapterPosition)
                    })
                }
                setOnClick(R.id.img_change, View.OnClickListener {
                    ContributeProductActivity.instance?.changeImage(adapterPosition)
                })
            } else {
                inviView(R.id.btn_delete)
                inviView(R.id.img_change)
            }
        }

        companion object {
            fun create(parent: ViewGroup): FrontImgHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.front_img_holder, parent, false)
                return FrontImgHolder(view)
            }
        }
    }

    class BackImgHolder(view: View) : BaseHolder(view) {

        fun bind(contributeImageChild: ContributeProductActivity.ContributeImageChild) {
            Glide.with(view.context.applicationContext)
                    .load(contributeImageChild.url)
                    .placeholder(R.drawable.update_product_holder)
                    .into(view.findViewById(R.id.img_front))
            if (contributeImageChild.canReplace) {
                if (adapterPosition == 1) {
                    inviView(R.id.btn_delete)
                } else {
                    showView(R.id.btn_delete)
                    setOnClick(R.id.btn_delete, View.OnClickListener {
                        ContributeProductActivity.instance?.removeImage(adapterPosition)
                    })
                }
                setOnClick(R.id.img_change, View.OnClickListener {
                    ContributeProductActivity.instance?.changeImage(adapterPosition)
                })
            } else {
                inviView(R.id.btn_delete)
                inviView(R.id.img_change)
            }
        }

        companion object {
            fun create(parent: ViewGroup): BackImgHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.back_img_holder, parent, false)
                return BackImgHolder(view)
            }
        }
    }

    class BackHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.findViewById<ViewGroup>(R.id.root).setOnClickListener {
                ContributeProductActivity.instance?.pickImage(itemViewType)
            }
        }

        companion object {
            fun create(parent: ViewGroup): BackHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.back_contribute_holder, parent, false)
                return BackHolder(view)
            }
        }
    }

    class FrontHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.findViewById<ViewGroup>(R.id.root).setOnClickListener {
                ContributeProductActivity.instance?.pickImage(itemViewType)
            }
        }

        companion object {
            fun create(parent: ViewGroup): FrontHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.front_contribute_holder, parent, false)
                return FrontHolder(view)
            }
        }
    }
}