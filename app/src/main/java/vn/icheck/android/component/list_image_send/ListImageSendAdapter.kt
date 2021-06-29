package vn.icheck.android.component.list_image_send


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image_send_product_question.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.kotlin.WidgetUtils.loadImageFromVideoFile
import java.io.File

class ListImageSendAdapter(val horizontalListener: IListImageSendListener, val isGridLayout: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData = mutableListOf<Any>()

    private val fileType = 1
    private val urlType = 2

    fun addData(list: MutableList<File>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(obj: String) {
        listData.add(obj)
        notifyDataSetChanged()
    }

    fun addItem(obj: File) {
        listData.add(obj)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteData() {
        listData.clear()
        notifyDataSetChanged()
    }

    val getlistData: MutableList<Any>
        get() {
            return listData
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == fileType) {
            FileHolder(LayoutInflater.from(parent.context).inflate(if (isGridLayout) {
                R.layout.item_image_send_product_question_2
            } else {
                R.layout.item_image_send_product_question
            }, parent, false))
        } else {
            UrlHolder(LayoutInflater.from(parent.context).inflate(if (isGridLayout) {
                R.layout.item_image_send_product_question_2
            } else {
                R.layout.item_image_send_product_question
            }, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position] is File) {
            fileType
        } else {
            urlType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileHolder) {
            holder.bind(listData[position] as File)
        }else if (holder is UrlHolder) {
            holder.bind(listData[position] as String)
        }
    }

    inner class FileHolder(view: View) : BaseViewHolder<File>(view) {
        override fun bind(obj: File) {
            try {
                itemView.imgImageMess.loadImageFromVideoFile(obj,null,SizeHelper.size4)
                if (obj.absolutePath.contains(".mp4")) {
                    itemView.btnPlay.beVisible()
                }else{
                    itemView.btnPlay.beInvisible()
                }
            } catch (e: Exception) {
                itemView.imgImageMess.beGone()
            }

            itemView.imgCloseImage.setOnClickListener {
                horizontalListener.onClickDeleteImageSend(adapterPosition)
            }
        }
    }

    inner class UrlHolder(view: View) : BaseViewHolder<String>(view) {
        override fun bind(obj: String) {
            try {
                WidgetUtils.loadImageUrlRounded(itemView.imgImageMess, obj, SizeHelper.size4)
                if (obj.contains(".mp4")) {
                    itemView.btnPlay.beVisible()
                }else{
                    itemView.btnPlay.beInvisible()
                }
            } catch (e: Exception) {
                itemView.imgImageMess.beGone()
            }

            itemView.imgCloseImage.setOnClickListener {
                horizontalListener.onClickDeleteImageSend(adapterPosition)
            }
        }
    }

    interface IListImageSendListener {
        fun onClickDeleteImageSend(position: Int)
    }
}