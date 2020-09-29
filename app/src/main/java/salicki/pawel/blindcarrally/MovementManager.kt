package salicki.pawel.blindcarrally

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MovementManager(context: Context) : SensorEventListener {
    private var manager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private var accelOutput: FloatArray? = null
    private var magOutput: FloatArray? = null

    private var startOrientation: FloatArray? = null

    private val orientation = FloatArray(3)

    init {
        manager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = manager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = manager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun getOrientation(): FloatArray? {
        return orientation
    }

    fun getStartOrientation(): FloatArray? {
        return startOrientation
    }

    fun newGame() {
        startOrientation = null
    }

    fun register() {
        manager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        manager!!.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun pause() {
        manager!!.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelOutput = event.values
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magOutput = event.values
        }

        if (accelOutput != null && magOutput != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput)
            if (success) {
                SensorManager.getOrientation(R, orientation)
                if (startOrientation == null) {
                    startOrientation = FloatArray(orientation.size)
                    System.arraycopy(orientation, 0, startOrientation, 0, orientation.size)
                }
            }
        }
    }
}