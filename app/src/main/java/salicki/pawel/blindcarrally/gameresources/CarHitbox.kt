package salicki.pawel.blindcarrally.gameresources

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import salicki.pawel.blindcarrally.datas.CarCoordinatesData
import salicki.pawel.blindcarrally.utils.Mathematics

class CarHitbox {
    private var carCoordinatesData: CarCoordinatesData = CarCoordinatesData()
    private var hitboxPaint: Paint = Paint()

    fun initHitbox(posX: Float, posY: Float, carHalfWidth: Float, carHalfHeight: Float){
        hitboxPaint.color = Color.GREEN
    }

    fun updateHitboxPosition(posX: Float, posY: Float, carHalfWidth: Float, carHalfHeight: Float,  carWidth: Float){
        carCoordinatesData.posX01 = posX - carHalfWidth
        carCoordinatesData.posY01 = posY - carHalfHeight

        carCoordinatesData.posX02 = posX + carWidth - carHalfWidth
        carCoordinatesData.posY02 = posY - carHalfHeight

        carCoordinatesData.posX03 = posX - carHalfWidth
        carCoordinatesData.posY03 = posY + carHalfHeight

        carCoordinatesData.posX04 = posX + carWidth - carHalfWidth
        carCoordinatesData.posY04 = posY + carHalfHeight

    }

    fun drawHitbox(canvas: Canvas,
                   coordinateDisplayManager: CoordinateDisplayManager
    )
    {
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX01),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY01),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX02),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY02),
            hitboxPaint
        )
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX01),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY01),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX03),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY03),
            hitboxPaint
        )
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX02),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY02),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX04),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY04),
            hitboxPaint
        )
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX03),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY03),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinatesData.posX04),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinatesData.posY04),
            hitboxPaint
        )
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {

        val left: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinatesData.posX01,
            carCoordinatesData.posY01,
            carCoordinatesData.posX03,
            carCoordinatesData.posY03
        )
        val right: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinatesData.posX02,
            carCoordinatesData.posY02,
            carCoordinatesData.posX04,
            carCoordinatesData.posY04
        )
        val top: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinatesData.posX01,
            carCoordinatesData.posY01,
            carCoordinatesData.posX02,
            carCoordinatesData.posY02
        )
        var bottom: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinatesData.posX03,
            carCoordinatesData.posY03,
            carCoordinatesData.posX04,
            carCoordinatesData.posY04
        )

        if (left || right || top || bottom) {
            return true
        }

        return false
    }
}