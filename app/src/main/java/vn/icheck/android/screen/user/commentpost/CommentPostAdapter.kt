package vn.icheck.android.screen.user.commentpost

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.CommentPostHolder
import vn.icheck.android.component.commentpost.CommentPostMoreHolder
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.component.commentpost.ICommentPostView
import vn.icheck.android.network.models.ICCommentPost

class CommentPostAdapter(val callback: ICommentPostView) : RecyclerViewCustomAdapter<Any>(callback) {

    private val itemType = 3
    private val optionType = 5

    fun addComment(obj: ICCommentPost) {
        if (listData.isNotEmpty()) {
            listData.add(0, obj)
            notifyItemInserted(0)
            notifyItemChanged(0, itemCount)
        } else {
            listData.add(obj)
            notifyDataSetChanged()
        }
    }

    fun addListReplies(list: MutableList<ICCommentPost>) {
        if (list.isNotEmpty()) {
            val questionId = list[0].parentID

            for (i in listData.size - 1 downTo 0) {
                if (listData[i] is ICCommentPostMore) {
                    if ((listData[i] as ICCommentPostMore).parentID == questionId) {
                        (listData[i] as ICCommentPostMore).isLoading = false

                        if (list[0].id != -1L) {
                            (listData[i] as ICCommentPostMore).currentCount += list.size
                            (listData[i] as ICCommentPostMore).offset += list.size

                            if ((listData[i] as ICCommentPostMore).currentCount >= (listData[i] as ICCommentPostMore).totalCount) {
                                listData.removeAt(i)
                            }

                            listData.addAll(i, list)
                            notifyDataSetChanged()
                        } else {
                            notifyItemChanged(i)
                        }
                        return
                    }
                }
            }
        }
    }

    fun addAnswer(obj: ICCommentPost): Int? {
        for (i in listData.size - 1 downTo 0) {
            when (listData[i]) {
                is ICCommentPost -> {
                    if ((listData[i] as ICCommentPost).id == obj.parentID) {
                        listData.add(i + 1, obj)
                        notifyItemInserted(i + 1)
                        notifyItemChanged(i + 1, itemCount)
                        return i
                    }
                }
                is ICCommentPostMore -> {
                    if ((listData[i] as ICCommentPostMore).parentID == obj.parentID) {
                        if ((listData[i] as ICCommentPostMore).notIDs.isNotEmpty()) {
                            (listData[i] as ICCommentPostMore).notIDs += ",${obj.id}"
                        } else {
                            (listData[i] as ICCommentPostMore).notIDs = obj.id.toString()
                        }
                    }
                }
            }
        }
        return null
    }

    fun deleteComment(obj: ICCommentPost) {
        if (obj.parentID == null) {
            deleteQuestion(obj)
        } else {
            deleteAnswer(obj)
        }
    }

    private fun deleteQuestion(obj: ICCommentPost) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICCommentPost) {
                if ((listData[i] as ICCommentPost).id == obj.id) {
                    if ((listData[i] as ICCommentPost).replyCount > 0) {
                        deleteAllAnswer((listData[i] as ICCommentPost).id)
                    }
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                    notifyItemChanged(if (i > 0) {
                        i - 1
                    } else 0, itemCount)
                    return
                }
            }
        }
    }

    private fun deleteAllAnswer(parentID: Long) {
        for (i in listData.size - 1 downTo 0) {
            when (listData[i]) {
                is ICCommentPost -> {
                    if ((listData[i] as ICCommentPost).parentID == parentID) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
                is ICCommentPostMore -> {
                    if ((listData[i] as ICCommentPostMore).parentID == parentID) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
            }
        }
    }

    private fun deleteAnswer(obj: ICCommentPost) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICCommentPost) {
                if ((listData[i] as ICCommentPost).id == obj.id) {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                    notifyItemChanged(if (i > 0) {
                        i - 1
                    } else 0, itemCount)
                    return
                }
            }
        }
    }

    fun updateComment(obj: ICCommentPost) {
        for (i in listData.size - 1 downTo 0) {
            listData[i].apply {
                when (this) {
                    is ICCommentPost -> {
                        if (id == obj.id) {
                            listData[i] = obj
                            notifyItemChanged(i)
                            return
                        }
                    }
                }
            }
        }
    }

    fun showOrHideAnswer(involveType: String?) {
        if (!involveType.isNullOrEmpty()) {
            for(i in 0 until listData.size){
                if(listData[i] is ICCommentPost){
                    (listData[i] as ICCommentPost).involveType=involveType
                }
            }
            notifyDataSetChanged()
        }
    }

    private fun removeItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position] is ICCommentPost) {
            itemType
        } else {
            optionType
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemType) {
            CommentPostHolder.create(parent, callback)
        } else {
            CommentPostMoreHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (holder) {
            is CommentPostHolder -> {
                holder.bind(listData[position] as ICCommentPost)
            }
            is CommentPostMoreHolder -> {
                holder.bind(listData[position] as ICCommentPostMore, object : ItemClickListener<ICCommentPostMore> {
                    override fun onItemClick(position: Int, item: ICCommentPostMore?) {
                        item?.let {
                            callback.onLoadMoreChildComment(item)
                            notifyItemChanged(position)
                        }
                    }
                })
            }
        }
    }
}