package vn.icheck.android.screen.user.my_gift_warehouse.shake_gift.shake.sdk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import vn.icheck.android.network.base.SettingManager
import kotlin.math.abs


class ShakeDetector : SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var vibrator: Vibrator

    private val isVibrate = SettingManager.getVibrateSetting

    private val SHAKE_THRESHOLD_GRAVITY = 5.7f
    private val SHAKE_SLOP_TIME_MS = 500
    private val SHAKE_COUNT_RESET_TIME_MS = 1000

    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0

    private var shakeTime: Long = 0
    private var shakeOneTime = 2000

    fun setOnShakeListener(listener: OnShakeListener) {
        this.mListener = listener
    }

    fun initSensor(activity: AppCompatActivity) {
        mSensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun registerSensor() {
        try {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unRegisterSensor() {
        try {
            mSensorManager.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun randomOneTime() {
        shakeOneTime = 2000
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun resetTime() {
        mShakeCount = 0
        shakeTime = 0
        randomOneTime()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (mListener != null && event != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val gForce = abs(gX * gX + gY * gY + gZ * gZ)

            val now = System.currentTimeMillis()

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                // Đang lắc
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    if (shakeTime == 0L) {
                        shakeTime = now
                    }

                    val progress = ((now - shakeTime).toDouble() / 1000) * (100 / 2)
//                    Log.d("ShakeDetector", "thoi gian - $progress")
                    mListener!!.onShaking(100, progress.toInt())

                    // Lắc xong
                    if (progress >= 100) {
                        if (isVibrate) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                vibrator.vibrate(500)
                            }
                        }

//                        Log.d("ShakeDetector", "lac xong " + shakeOneTime)
                        mListener?.onShake(mShakeCount)
                        resetTime()
                    }
                    return
                }

                mShakeTimestamp = now
                mShakeCount++
            } else {
                // Sau khi lắc 2s
                if (shakeTime != 0L && mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
//                    Log.d("ShakeDetector", "dừng lắc")
                    mListener?.onCancel()
                    resetTime()
                }
            }
        }
    }

    interface OnShakeListener {
        fun onShaking(total: Int, progress: Int)
        fun onShake(count: Int)
        fun onCancel()
    }
}