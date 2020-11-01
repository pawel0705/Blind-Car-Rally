package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import salicki.pawel.blindcarrally.data.CarPositionSensors
import salicki.pawel.blindcarrally.data.SensorDetect
import java.util.Collections.swap

class CarSensors {
    private var carPositionSensors: CarPositionSensors = CarPositionSensors()
    private var sensorPaint = Paint()
    private var obstacleSensorsLength = 0F

    fun initCarDistanceSensors(sensorsLength: Float) {
        obstacleSensorsLength = sensorsLength
        sensorPaint.color = Color.YELLOW
    }

    fun updatePositionSensors(
        posX: Float,
        posY: Float,
        carHalfWidth: Float,
        carHalfHeight: Float,
        obstacleSensorLength: Float
    ) {
        carPositionSensors.sensorX01 = posX - carHalfWidth
        carPositionSensors.sensorY01 = posY - carHalfHeight

        carPositionSensors.sensorX02 = posX - 4 * carHalfWidth
        carPositionSensors.sensorY02 = posY - obstacleSensorLength


        carPositionSensors.sensorX03 = posX - carHalfWidth
        carPositionSensors.sensorY03 = posY + carHalfHeight

        carPositionSensors.sensorX04 = posX - 4 * carHalfWidth
        carPositionSensors.sensorY04 = posY + obstacleSensorLength


        carPositionSensors.sensorX05 = posX - carHalfWidth
        carPositionSensors.sensorY05 = posY

        carPositionSensors.sensorX06 = posX - obstacleSensorLength
        carPositionSensors.sensorY06 = posY

        carPositionSensors.sensorX07 = posX + carHalfWidth
        carPositionSensors.sensorY07 = posY

        carPositionSensors.sensorX08 = posX + obstacleSensorLength
        carPositionSensors.sensorY08 = posY


        carPositionSensors.sensorX09 = posX + carHalfWidth
        carPositionSensors.sensorY09 = posY + carHalfHeight

        carPositionSensors.sensorX10 = posX + 4 * carHalfWidth
        carPositionSensors.sensorY10 = posY + obstacleSensorLength


        carPositionSensors.sensorX11 = posX + carHalfWidth
        carPositionSensors.sensorY11 = posY - carHalfHeight

        carPositionSensors.sensorX12 = posX + 4 * carHalfWidth
        carPositionSensors.sensorY12 = posY - obstacleSensorLength
    }

    fun drawSensors(
        canvas: Canvas,
        coordinateDisplayManager: CoordinateDisplayManager
    ) {

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX01),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY01),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX02),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY02),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX03),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY03),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX04),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY04),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX05),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY05),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX06),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY06),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX07),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY07),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX08),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY08),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX09),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY09),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX10),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY10),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX11),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY11),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensors.sensorX12),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensors.sensorY12),
            sensorPaint
        )
    }

    fun sensorDistance(x1: Float, y1: Float, x2: Float, y2: Float) : SensorDetect{

        var sensorLeft1: Boolean = false
        var sensorLeft2: Boolean = false
        var sensorLeft3: Boolean = false

        var sensorRight1: Boolean = false
        var sensorRight2: Boolean = false
        var sensorRight3: Boolean = false

        var sensorDistanceLeft1: Float = Float.MAX_VALUE
        var sensorDistanceLeft2: Float = Float.MAX_VALUE
        var sensorDistanceLeft3: Float = Float.MAX_VALUE

        var sensorDistanceRight1: Float = Float.MAX_VALUE
        var sensorDistanceRight2: Float = Float.MAX_VALUE
        var sensorDistanceRight3: Float = Float.MAX_VALUE

        // left 1
        if (Mathematics.collisionLineToLine(
                x1, y1, x2, y2,
                carPositionSensors.sensorX01, carPositionSensors.sensorY01,
                carPositionSensors.sensorX02, carPositionSensors.sensorY02
            )
        ) {
            sensorLeft1 = true

            sensorDistanceLeft1 = Mathematics.distancePointToLine(
                carPositionSensors.sensorX01,
                carPositionSensors.sensorY01,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // left 2
        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX03, carPositionSensors.sensorY03,
                carPositionSensors.sensorX04, carPositionSensors.sensorY04, x1, y1, x2, y2
            )
        ) {
            sensorLeft2 = true

            sensorDistanceLeft2 = Mathematics.distancePointToLine(
                carPositionSensors.sensorX03,
                carPositionSensors.sensorY03,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // left 3
        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX05, carPositionSensors.sensorY05,
                carPositionSensors.sensorX06, carPositionSensors.sensorY06, x1, y1, x2, y2
            )
        ) {
            sensorLeft3 = true

            sensorDistanceLeft3 = Mathematics.distancePointToLine(
                carPositionSensors.sensorX05,
                carPositionSensors.sensorY05,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // right 1
        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX07, carPositionSensors.sensorY07,
                carPositionSensors.sensorX08, carPositionSensors.sensorY08, x1, y1, x2, y2
            )
        ) {
            sensorRight1 = true

            sensorDistanceRight1 = Mathematics.distancePointToLine(
                carPositionSensors.sensorX07,
                carPositionSensors.sensorY07,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // right 2
        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX09, carPositionSensors.sensorY09,
                carPositionSensors.sensorX10, carPositionSensors.sensorY10, x1, y1, x2, y2
            )
        ) {
            sensorRight2 = true

            sensorDistanceRight2 = Mathematics.distancePointToLine(
                carPositionSensors.sensorX09,
                carPositionSensors.sensorY09,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // right 3
        if (Mathematics.collisionLineToLine(
                carPositionSensors.sensorX11, carPositionSensors.sensorY11,
                carPositionSensors.sensorX12, carPositionSensors.sensorY12, x1, y1, x2, y2
            )
        ) {
            sensorRight3 = true

            sensorDistanceRight3 = Mathematics.distancePointToLine(
                carPositionSensors.sensorX11,
                carPositionSensors.sensorY11,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        var sensorDetect: SensorDetect = SensorDetect()

        if(sensorLeft1 || sensorLeft2 || sensorLeft3){
            sensorDetect.left = true

            var leftArray = arrayOf(sensorDistanceLeft1, sensorDistanceLeft2, sensorDistanceLeft3)
           leftArray.sort()
            sensorDetect.leftLength = leftArray.first()
        }

        if(sensorRight1 || sensorRight2 || sensorRight3){
            sensorDetect.right = true

            var rightArray = arrayOf(sensorDistanceRight1, sensorDistanceRight2, sensorDistanceRight3)
           rightArray.sort()
            sensorDetect.rightLength = rightArray.first()
        }

        return sensorDetect
    }
}