package vn.icheck.android.helper

import android.content.Context
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.address.AddressInteractor
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICDistrict
import vn.icheck.android.room.entity.ICProvince
import vn.icheck.android.room.entity.ICWard


class AddressHelper(context: Context) {
    private val addressInteraction = AddressInteractor()
    private val appDatabase = AppDatabase.getDatabase(context)

    fun getListProvince(offset: Int, limit: Int, listener: ICApiListener<MutableList<ICProvince>>) {
        val provinceDao = appDatabase.provinceDao()

        val listProvince = provinceDao.getAllProvince()

        if (listProvince.isEmpty()) {
            addressInteraction.getListProvince(0, 30, object : ICApiListener<ICListResponse<vn.icheck.android.network.models.ICProvince>> {
                override fun onSuccess(obj: ICListResponse<vn.icheck.android.network.models.ICProvince>) {
                    for (it in obj.rows) {
                        listProvince.add(ICProvince(it.id.toInt(), it.name, TextHelper.unicodeToKoDauLowerCase(it.name), it.country_id))
                    }

                    provinceDao.deleteAll()
                    provinceDao.insertListProvince(listProvince)

                    listener.onSuccess(listProvince)
                }

                override fun onError(error: ICBaseResponse?) {
                    listener.onError(error)
                }
            })
        } else {
            listener.onSuccess(listProvince)
        }
    }

    fun getListDistrict(provinceID: Int, listener: ICApiListener<MutableList<ICDistrict>>) {
        val districtDao = appDatabase.districtDao()

        val listDistrict = districtDao.getListDistrict(provinceID)

        if (listDistrict.isEmpty()) {
            addressInteraction.getListDistrict(provinceID, 0, 30, object : ICApiListener<ICListResponse<vn.icheck.android.network.models.ICDistrict>> {
                override fun onSuccess(obj: ICListResponse<vn.icheck.android.network.models.ICDistrict>) {
                    for (it in obj.rows) {
                        listDistrict.add(ICDistrict(it.id, it.name, TextHelper.unicodeToKoDauLowerCase(it.name), it.city_id))
                    }

                    districtDao.deleteDistrictByID(provinceID)
                    districtDao.insertListDistrict(listDistrict)

                    listener.onSuccess(listDistrict)
                }

                override fun onError(error: ICBaseResponse?) {
                    listener.onError(error)
                }
            })
        } else {
            listener.onSuccess(listDistrict)
        }
    }

    fun getListWard(districtID: Int, listener: ICApiListener<MutableList<ICWard>>) {
        val wardDao = appDatabase.wardDao()

        val listDistrict = wardDao.getListDistrict(districtID)

        if (listDistrict.isEmpty()) {
            addressInteraction.getListWard(districtID, 0, 30, object : ICApiListener<ICListResponse<vn.icheck.android.network.models.ICWard>> {
                override fun onSuccess(obj: ICListResponse<vn.icheck.android.network.models.ICWard>) {
                    for (it in obj.rows) {
                        listDistrict.add(ICWard(it.id, it.name, TextHelper.unicodeToKoDauLowerCase(it.name), it.district_id))
                    }

                    wardDao.deleteDistrictByID(districtID)
                    wardDao.insertListDistrict(listDistrict)

                    listener.onSuccess(listDistrict)
                }

                override fun onError(error: ICBaseResponse?) {
                    listener.onError(error)
                }
            })
        } else {
            listener.onSuccess(listDistrict)
        }
    }

    fun clearAllData() {
        val provinceDao = appDatabase.provinceDao()
        val districtDao = appDatabase.districtDao()
        val wardDao = appDatabase.wardDao()

        provinceDao.deleteAll()
        districtDao.deleteAll()
        wardDao.deleteAll()
    }
}