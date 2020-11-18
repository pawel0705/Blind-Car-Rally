package salicki.pawel.blindcarrally.gameresources

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import salicki.pawel.blindcarrally.datas.CarPositionSensorsData
import salicki.pawel.blindcarrally.datas.SensorDetectData
import salicki.pawel.blindcarrally.utils.Mathematics

class CarSensors {
    private var carPositionSensorsData: CarPositionSensorsData = CarPositionSensorsData()
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
        carPositionSensorsData.sensorX01 = posX - carHalfWidth
        carPositionSensorsData.sensorY01 = posY - carHalfHeight

        carPositionSensorsData.sensorX02 = posX - 3 * carHalfWidth
        carPositionSensorsData.sensorY02 = posY - obstacleSensorLength * 0.6F


        carPositionSensorsData.sensorX03 = posX - carHalfWidth
        carPositionSensorsData.sensorY03 = posY + carHalfHeight

        carPositionSensorsData.sensorX04 = posX - 3 * carHalfWidth
        carPositionSensorsData.sensorY04 = posY + obstacleSensorLength * 0.6F


        carPositionSensorsData.sensorX05 = posX - carHalfWidth
        carPositionSensorsData.sensorY05 = posY

        carPositionSensorsData.sensorX06 = posX - obstacleSensorLength
        carPositionSensorsData.sensorY06 = posY

        carPositionSensorsData.sensorX07 = posX + carHalfWidth
        carPositionSensorsData.sensorY07 = posY

        carPositionSensorsData.sensorX08 = posX + obstacleSensorLength
        carPositionSensorsData.sensorY08 = posY


        carPositionSensorsData.sensorX09 = posX + carHalfWidth
        carPositionSensorsData.sensorY09 = posY + carHalfHeight

        carPositionSensorsData.sensorX10 = posX + 3 * carHalfWidth
        carPositionSensorsData.sensorY10 = posY + obstacleSensorLength * 0.6F


        carPositionSensorsData.sensorX11 = posX + carHalfWidth
        carPositionSensorsData.sensorY11 = posY - carHalfHeight

        carPositionSensorsData.sensorX12 = posX + 3 * carHalfWidth
        carPositionSensorsData.sensorY12 = posY - obstacleSensorLength * 0.6F
    }

    fun drawSensors(
        canvas: Canvas,
        coordinateDisplayManager: CoordinateDisplayManager
    ) {

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX01),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY01),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX02),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY02),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX03),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY03),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX04),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY04),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX05),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY05),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX06),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY06),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX07),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY07),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX08),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY08),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX09),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY09),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX10),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY10),
            sensorPaint
        )

        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX11),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY11),
            coordinateDisplayManager.convertToEnvironmentX(carPositionSensorsData.sensorX12),
            coordinateDisplayManager.convertToEnvironmentY(carPositionSensorsData.sensorY12),
            sensorPaint
        )
    }

    fun sensorDistance(x1: Float, y1: Float, x2: Float, y2: Float) : SensorDetectData{

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
                carPositionSensorsData.sensorX01, carPositionSensorsData.sensorY01,
                carPositionSensorsData.sensorX02, carPositionSensorsData.sensorY02
            )
        ) {
            sensorLeft1 = true

            sensorDistanceLeft1 = Mathematics.distancePointToLine(
                carPositionSensorsData.sensorX01,
                carPositionSensorsData.sensorY01,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // left 2
        if (Mathematics.collisionLineToLine(
                carPositionSensorsData.sensorX03, carPositionSensorsData.sensorY03,
                carPositionSensorsData.sensorX04, carPositionSensorsData.sensorY04, x1, y1, x2, y2
            )
        ) {
            sensorLeft2 = true

            sensorDistanceLeft2 = Mathematics.distancePointToLine(
                carPositionSensorsData.sensorX03,
                carPositionSensorsData.sensorY03,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // left 3
        if (Mathematics.collisionLineToLine(
                carPositionSensorsData.sensorX05, carPositionSensorsData.sensorY05,
                carPositionSensorsData.sensorX06, carPositionSensorsData.sensorY06, x1, y1, x2, y2
            )
        ) {
            sensorLeft3 = true

            sensorDistanceLeft3 = Mathematics.distancePointToLine(
                carPositionSensorsData.sensorX05,
                carPositionSensorsData.sensorY05,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // right 1
        if (Mathematics.collisionLineToLine(
                carPositionSensorsData.sensorX07, carPositionSensorsData.sensorY07,
                carPositionSensorsData.sensorX08, carPositionSensorsData.sensorY08, x1, y1, x2, y2
            )
        ) {
            sensorRight1 = true

            sensorDistanceRight1 = Mathematics.distancePointToLine(
                carPositionSensorsData.sensorX07,
                carPositionSensorsData.sensorY07,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // right 2
        if (Mathematics.collisionLineToLine(
                carPositionSensorsData.sensorX09, carPositionSensorsData.sensorY09,
                carPositionSensorsData.sensorX10, carPositionSensorsData.sensorY10, x1, y1, x2, y2
            )
        ) {
            sensorRight2 = true

            sensorDistanceRight2 = Mathematics.distancePointToLine(
                carPositionSensorsData.sensorX09,
                carPositionSensorsData.sensorY09,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        // right 3
        if (Mathematics.collisionLineToLine(
                carPositionSensorsData.sensorX11, carPositionSensorsData.sensorY11,
                carPositionSensorsData.sensorX12, carPositionSensorsData.sensorY12, x1, y1, x2, y2
            )
        ) {
            sensorRight3 = true

            sensorDistanceRight3 = Mathematics.distancePointToLine(
                carPositionSensorsData.sensorX11,
                carPositionSensorsData.sensorY11,
                x1,
                y1,
                x2,
                y2
            ).toFloat()
        }

        var sensorDetectData: SensorDetectData = SensorDetectData()

        if(sensorLeft1 || sensorLeft2 || sensorLeft3){
            sensorDetectData.left = true

            var leftArray = arrayOf(sensorDistanceLeft1, sensorDistanceLeft2, sensorDistanceLeft3)
           leftArray.sort()
            sensorDetectData.leftLength = leftArray.first()
        }

        if(sensorRight1 || sensorRight2 || sensorRight3){
            sensorDetectData.right = true

            var rightArray = arrayOf(sensorDistanceRight1, sensorDistanceRight2, sensorDistanceRight3)
           rightArray.sort()
            sensorDetectData.rightLength = rightArray.first()
        }

        return sensorDetectData
    }
}