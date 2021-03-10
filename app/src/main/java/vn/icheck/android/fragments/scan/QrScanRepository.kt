package vn.icheck.android.fragments.scan

import androidx.lifecycle.LiveData
import vn.icheck.android.room.dao.QrScanDao
import vn.icheck.android.room.entity.ICQrScan

class QrScanRepository(private val qrScanDao: QrScanDao) {

    suspend fun insert(qrScan: ICQrScan) {
        qrScanDao.insertQrScan(qrScan)
    }

    fun search(id: String): LiveData<ICQrScan?> {
        return qrScanDao.search(id)
    }

    suspend fun update(qrScan: ICQrScan) {
        qrScanDao.update(qrScan.noidungQr, qrScan.loaima, qrScan.maQr, qrScan.idProduct, qrScan.imgProduct, qrScan.nameProduct, qrScan.price, qrScan.specialPrice, qrScan.seller_id, qrScan.barcode)
    }
}