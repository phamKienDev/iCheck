package vn.icheck.android.screen.user.product_detail.product.my_contribution

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.internal.LinkedTreeMap
import org.json.JSONObject
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemMyContributeBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.util.ick.*

class MyContributionAdapter(val listCategory: List<CategoryAttributesModel>, val maxLine: Int = 0, val maxLength: Int = Int.MAX_VALUE) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = parent.getLayoutInflater()
        return MyContributionHolder(ItemMyContributeBinding.inflate(lf, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyContributionHolder
        if (position % 2 != 0) {
            holder.binding.root.setBackgroundColor(Constant.getAppBackgroundWhiteColor(holder.binding.root.context))
        }
        try {
            val data = listCategory[position]
            holder.binding.tvTitle simpleText data.categoryItem.name
            when (data.categoryItem.type) {
                "group", "htmleditor", "textarea", "text" -> {
                    try {
                        val text = data.values as LinkedTreeMap<String, String>
//                val jsonObject = JSONObject("{\"${text.keys.first()}\":\"${text.values.first()}\"")
                        val jsonObject = JSONObject(text.toMap())
                        holder.binding.tvData.setText(jsonObject.getString("shortContent"))
                    } catch (e: Exception) {
                        try {
                            val text = data.values as HashMap<String, String>
//                val jsonObject = JSONObject("{\"${text.keys.first()}\":\"${text.values.first()}\"")
                            val jsonObject = JSONObject(text.toMap())
                            holder.binding.tvData.setText(jsonObject.getString("shortContent"))
                        } catch (e: Exception) {
                            holder.binding.tvData.setText(data.values.toString())
                        }
                    }
                }
                "image-single" -> {
                    try {
                        holder.binding.tvData.beGone()
                        holder.binding.tvData.beGone()
                        holder.binding.image1.beVisible()

                        holder.binding.image1.loadRoundedImage((data.values as ArrayList<String>).firstOrNull(), R.drawable.error_load_image, corner = 4, width = 100.dpToPx())
                        holder.binding.image1.setOnClickListener {
                            DetailMediaActivity.start(it.context, data.values as ArrayList<String?>)
                        }

                    } catch (e: Exception) {
                    }
                }
                "image" -> {
                    try {
                        holder.binding.tvData.beGone()
                        holder.binding.image1.beVisible()
                        holder.binding.imgBlur.beGone()
                        holder.binding.image1.loadRoundedImage((data.values as ArrayList<String>).firstOrNull(), R.drawable.error_load_image, corner = 4, width = 100.dpToPx())
                        if ((data.values as ArrayList<String>).size == 2) {
                            holder.binding.image2.beVisible()
                            holder.binding.image2.loadRoundedImage((data.values as ArrayList<String>).get(1), R.drawable.error_load_image, corner = 4, width = 100.dpToPx())
                        } else if ((data.values as ArrayList<String>).size > 2) {
                            holder.binding.image2.beVisible()
                            holder.binding.image2.loadRoundedImage((data.values as ArrayList<String>).get(1), R.drawable.error_load_image, corner = 4, width = 100.dpToPx())
                            holder.binding.imgBlur.beVisible()
                            Glide.with(holder.binding.root.context)
                                    .load(ColorDrawable(ResourcesCompat.getColor(holder.binding.root.context.resources, R.color.colorBackgroundPopup, null)))
                                    .transform(CenterCrop(), RoundedCorners(4.toPx()))
                                    .into(holder.binding.imgBlur)

                            holder.binding.tvCountRemain simpleText "+${(data.values as ArrayList<String>).size - 2}"
                        }
                        holder.binding.image1.setOnClickListener {
                            DetailMediaActivity.start(it.context, data.values as ArrayList<String?>)
                        }
                        holder.binding.image2.setOnClickListener {
                            DetailMediaActivity.start(it.context, data.values as ArrayList<String?>)
                        }
                    } catch (e: Exception) {
                    }

                }
                "multiselect" -> {
                    var text = ""
                    if (data.categoryItem.value is ArrayList<*>) {
                        for (item in data.categoryItem.value as ArrayList<Double>) {
                            val filt = data.categoryItem.options?.firstOrNull {
                                it?.id == item.toInt()
                            }
                            if (filt != null) {
                                text += "${filt.value} \n"
                            }
                        }
                    }
                    holder.binding.tvData simpleText text
                }
                "date" -> {
                    holder.binding.tvData simpleText data.values.toString().getDayTime()
                }
                else -> {
                    holder.binding.tvData simpleText data.values.toString()
                }
            }

        } catch (e: Exception) {

        }
    }

    override fun getItemCount() = if (maxLine == 0) listCategory.size else maxLine

    inner class MyContributionHolder(val binding: ItemMyContributeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvData.maxLines = maxLength
        }
    }
}