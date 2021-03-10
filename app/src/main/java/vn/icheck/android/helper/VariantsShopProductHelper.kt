package vn.icheck.android.helper

import vn.icheck.android.network.models.ICAttributes
import vn.icheck.android.network.models.ICVariants

object VariantsShopProductHelper {


    /**
     * Lấy list tất cả MÀU không bị trùng dữ liệu hiển thị
     */

    fun getListAllColor(listData: MutableList<ICVariants>): MutableList<ICAttributes> {
        val listAllColor = mutableListOf<ICAttributes>()

        for (item in listData) {
            if (!listAllColor.contains(item.attributes?.get(1))) {
                listAllColor.add(item.attributes!![1])
            }
        }
        return listAllColor
    }


    /**
     * Lấy list tất cả SIZE không bị trùng dữ liệu hiển thị
     */

    fun getListAllSize(listData: MutableList<ICVariants>): MutableList<ICAttributes> {
        val listAllSize = mutableListOf<ICAttributes>()
        for (item in listData) {
            if (!listAllSize.contains(item.attributes?.get(0))) {
                listAllSize.add(item.attributes!![0])
            }

        }
        return listAllSize
    }

    /**
     * lấy list size từ màu được chọn
     */
    fun getListSizeByColor(listData: MutableList<ICVariants>, color: String): MutableList<ICAttributes> {
        val listSizeByColor = mutableListOf<ICAttributes>()
        for (item in listData) {
            if (color == item.attributes?.get(1)!!.value) {
                if (!listSizeByColor.contains(item.attributes!![0])) {
                    listSizeByColor.add(item.attributes!![0])
                }
            }
        }
        return listSizeByColor
    }

    /**
     * lấy vị trí size đầu tiên  trong listSize của màu được chọn
     */
    fun getSizePositionFist(listAllSize: MutableList<ICAttributes>, listSizeByColor: MutableList<ICAttributes>): Int {
        var sizePosition = 0
        outerloop@ for (i in 0 until listAllSize.size) {
            for (j in 0 until listSizeByColor.size) {
                if (listSizeByColor[j].value == listAllSize[i].value) {
                    sizePosition = i
                    break@outerloop
                }
            }

        }
        return sizePosition
    }

    /**
     * lấy size đầu tiên trong listSize của màu được chọn
     */
    fun getSizeFirst(listAllSize: MutableList<ICAttributes>, sizePosition: Int): String {
        return listAllSize[sizePosition].value.toString()

    }

}