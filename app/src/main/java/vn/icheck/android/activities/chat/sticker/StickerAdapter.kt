package vn.icheck.android.activities.chat.sticker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.feature.social.SocialRepository
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.util.ui.GlideUtil

class StickerAdapter(var listSticker: ArrayList<StickerView>, var size: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onStickerClick: OnStickerClick? = null
    lateinit var stickerPackagesDao: StickerPackagesDao

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        stickerPackagesDao = AppDatabase.getDatabase(parent.context).stickerPackagesDao()
        return when (size) {
            1 -> StickerHolder.create(parent, onStickerClick)
            2 -> StickerBigHolder.create(parent, onStickerClick)
            else -> StickerStoreHolder.create(parent)
        }
//        return if (size == 1) StickerHolder.create(parent, onStickerClick) else
//            StickerBigHolder.create(parent, onStickerClick)
    }

    override fun getItemCount(): Int {
        return listSticker.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (size) {
            1 -> (holder as StickerHolder).bind(listSticker[position])
            2 -> (holder as StickerBigHolder).bind(listSticker[position])
            3 -> (holder as StickerStoreHolder).bind(listSticker[position])
        }
//        if (size == 1) {
//            (holder as StickerHolder).bind(listSticker[position])
//        } else {
//            (holder as StickerBigHolder).bind(listSticker[position])
//        }
    }

    class StickerStoreHolder(view: View) : BaseHolder(view) {

        private val socialRepository = SocialRepository()
        lateinit var stickerAdapter: StickerAdapter
        val stickerPackagesDao = AppDatabase.getDatabase(view.context).stickerPackagesDao()

        fun checkId(id: String): Boolean {
            for (item in stickerPackagesDao.getAllStickerPackges()) {
                if (id == item.id) {
                    return true
                }
            }
            return false
        }

        fun bind(stickerView: StickerView) {
            val rcv = getRcv(R.id.rcv_stickers)
            GlideUtil.loading(stickerView.image, getImg(R.id.img_sticker_pack))
            getVg(R.id.root).background=vn.icheck.android.ichecklibs.ViewHelper.bgWhiteRadius12(getVg(R.id.root).context)
            getTv(R.id.tv_pack_name).text = stickerView.name
            getTv(R.id.tv_total).text = String.format("%d nhãn dán", stickerView.total)
            val button = view.findViewById<TextView>(R.id.btn_delete)

            if (checkId(stickerView.id)) {
                button.text = "Xóa"
                button.setBackgroundResource(R.drawable.button_remove)
            } else {
                button.text = "Thêm"
                button.setBackgroundResource(R.drawable.button_add)
            }


            setOnClick(R.id.btn_delete, View.OnClickListener {
                if (checkId(stickerView.id)) {
                    button.setBackgroundResource(R.drawable.button_add)
                    button.text = "Thêm"
                    stickerPackagesDao.deleteStickPackages(stickerView.id)
                } else {
                    button.text = "Xóa"
                    button.setBackgroundResource(R.drawable.button_remove)
                    stickerPackagesDao.insertStickerPackages(StickerPackages(
                            stickerView.id, stickerView.name, stickerView.image, stickerView.total))
                }
            })
            setOnClick(R.id.root, View.OnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        view.findViewById<View>(R.id.div).visibility = View.VISIBLE
                        rcv.visibility = View.VISIBLE
                        if (!::stickerAdapter.isInitialized) {
                            val result = socialRepository.getStickers(stickerView.id)
                            val arr = arrayListOf<StickerView>()
                            for (item in result.data) {
                                arr.add(StickerView(item.id, item.image))
                            }
                            stickerAdapter = StickerAdapter(arr, 2)

                            rcv.adapter = stickerAdapter
                            rcv.layoutManager = GridLayoutManager(view.context, 4)
                        }
                    } catch (e: Exception) {
                        Log.e("e", "${e.message}")
                    }
                }
            })
        }

        companion object {
            fun create(parent: ViewGroup): StickerStoreHolder {
                return StickerStoreHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.sticker_store_holder, parent, false))
            }
        }
    }


    class StickerBigHolder(view: View, val onStickerClick: OnStickerClick?) : BaseHolder(view) {

        fun bind(stickerView: StickerView) {
            GlideUtil.loading(stickerView.image, getImg(R.id.img_sticker))
            setOnClick(R.id.img_sticker, View.OnClickListener {
                if (stickerView.packageId.isEmpty()) {
                    onStickerClick?.getSticker(stickerView.id)
                } else {
                    onStickerClick?.sendSticker(stickerView)
                }
            })
        }

        companion object {
            fun create(parent: ViewGroup, onStickerClick: OnStickerClick?): StickerBigHolder {
                return StickerBigHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.holder_big_sticker, parent, false), onStickerClick)
            }
        }
    }

    class StickerHolder(view: View, val onStickerClick: OnStickerClick?) : BaseHolder(view) {

        fun bind(stickerView: StickerView) {
            GlideUtil.loading(stickerView.image, getImg(R.id.img_sticker))
            setOnClick(R.id.img_sticker, View.OnClickListener {
                if (stickerView.packageId.isEmpty()) {
                    onStickerClick?.getSticker(stickerView.id)
                } else {
                    onStickerClick?.sendSticker(stickerView)
                }
            })
        }

        companion object {
            fun create(parent: ViewGroup, onStickerClick: OnStickerClick?): StickerHolder {
                return StickerHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.holder_sticker, parent, false), onStickerClick)
            }
        }
    }

    interface OnStickerClick {
        fun sendSticker(stickerView: StickerView)
        fun getSticker(id: String)
    }
}