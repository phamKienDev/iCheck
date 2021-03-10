package vn.icheck.android.screen.user.detail_post

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.commentpost.*
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICPost

class DetailPostAdapter(val callbackComment: ICommentPostView, val callbackPost: IDetailPostListener) : RecyclerViewCustomAdapter<Any>(callbackComment) {
    private val postType = 4
    private val itemType = 5
    private val optionType = 6

    fun addHeader(obj: ICPost, isUpdate: Boolean = false) {
        if (isUpdate) {
            for (i in 0 until listData.size - 1) {
                if (listData[i] is ICPost) {
                    listData[i] = obj
                    notifyItemChanged(i)
                }
            }
        } else {
            if (listData.isEmpty()) {
                listData.add(obj)
            } else {
                listData.add(0, obj)
            }
            notifyDataSetChanged()
        }
    }


    fun addChildComment(list: MutableList<ICCommentPost>) {
        if (!list.isNullOrEmpty()) {
            val parentId = list[0].commentId

            for (i in listData.size - 1 downTo 0) {
                if (listData[i] is ICCommentPostMore) {
                    if (parentId == (listData[i] as ICCommentPostMore).parentID) {
                        (listData[i] as ICCommentPostMore).currentCount += list.size
                        (listData[i] as ICCommentPostMore).isLoading = false
                        if ((listData[i] as ICCommentPostMore).currentCount >= (listData[i] as ICCommentPostMore).totalCount) {
                            listData.removeAt(i)
                            notifyItemRemoved(i)
                        } else {
                            notifyItemChanged(i)
                        }

                        listData.addAll(i, list)
                        notifyItemRangeInserted(i, list.size)
                    }
                }
            }
        }
    }

    fun addComment(obj: ICCommentPost) {
        if (listData.size > 1) {
            listData.add(1, obj)
            notifyItemInserted(1)
        } else {
            listData.add(obj)
            notifyDataSetChanged()
        }
    }

    fun addChildComment(obj: ICCommentPost) {
        val index = listData.indexOfFirst { it is ICCommentPost && it.id == obj.commentId }
        if (index >= 0) {
            listData.add(index + 1, obj)
            notifyItemInserted(index + 1)
        }
    }

    fun updateComment(obj: ICCommentPost) {
        val index = listData.indexOfFirst { it is ICCommentPost && it.id == obj.id }
        if (index >= 0) {
            listData[index] = obj
            notifyItemChanged(index)
        }
    }

    fun updatePostOrReview(obj: ICPost) {
        if (listData.firstOrNull() is ICPost) {
            listData[0] = obj
            notifyItemChanged(0)
        }
    }

    fun updateListComment(comments: MutableList<ICCommentPost>?) {
        if (!comments.isNullOrEmpty()) {
            for (i in listData.size - 1 downTo 0) {
                if (listData[i] !is ICPost) {
                    listData.removeAt(i)
                }
            }
            listData.addAll(comments)
            notifyDataSetChanged()
        }
    }

    fun deleteComment(obj: ICCommentPost) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICCommentPost) {
                if ((listData[i] as ICCommentPost).commentId != null) {
                    if ((listData[i] as ICCommentPost).commentId == obj.id) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                } else {
                    if ((listData[i] as ICCommentPost).id == obj.id) {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
            } else if (listData[i] is ICCommentPostMore) {
                if ((listData[i] as ICCommentPostMore).parentID == obj.id) {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
            }
        }
    }

    fun showOrHideAnswer(involveType: String?) {
        if (!involveType.isNullOrEmpty()) {
            for (i in 0 until listData.size) {
                if (listData[i] is ICCommentPost) {
                    (listData[i] as ICCommentPost).involveType = involveType
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            postType -> DetailPostHolder.create(parent, callbackPost)
            itemType -> CommentPostHolder.create(parent, callbackComment)
            optionType -> CommentPostMoreHolder(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (holder) {
            is DetailPostHolder -> {
                holder.bind(listData[position] as ICPost)
            }
            is CommentPostHolder -> {
                holder.bind(listData[position] as ICCommentPost)
            }
            is CommentPostMoreHolder -> {
                if (position < listData.size && listData[position] is ICCommentPostMore) {
                    holder.bind(listData[position] as ICCommentPostMore, object : ItemClickListener<ICCommentPostMore> {
                        override fun onItemClick(position: Int, item: ICCommentPostMore?) {
                            item?.let {
                                callbackComment.onLoadMoreChildComment(it)
                                notifyDataSetChanged()
                            }
                        }
                    })
                }
            }
        }
    }

    override fun getItemType(position: Int): Int {
        return when {
            listData[position] is ICPost -> {
                postType
            }
            listData[position] is ICCommentPost -> {
                itemType
            }
            else -> {
                optionType
            }
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            postType -> {
                DetailPostHolder.create(parent, callbackPost)
            }
            itemType -> {
                CommentPostHolder.create(parent, callbackComment)
            }
            optionType -> {
                CommentPostMoreHolder(parent)
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }
}