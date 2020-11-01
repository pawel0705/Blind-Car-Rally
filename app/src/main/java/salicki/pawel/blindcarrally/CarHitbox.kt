package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import salicki.pawel.blindcarrally.data.CarCoordinates

class CarHitbox {
    private var carCoordinates: CarCoordinates = CarCoordinates()
    private var hitboxPaint: Paint = Paint()

    fun initHitbox(posX: Float, posY: Float, carHalfWidth: Float, carHalfHeight: Float){
        hitboxPaint.color = Color.GREEN
    }

    fun updateHitboxPosition(posX: Float, posY: Float, carHalfWidth: Float, carHalfHeight: Float,  carWidth: Float){
        carCoordinates.posX01 = posX - carHalfWidth
        carCoordinates.posY01 = posY - carHalfHeight

        carCoordinates.posX02 = posX + carWidth - carHalfWidth
        carCoordinates.posY02 = posY - carHalfHeight

        carCoordinates.posX03 = posX - carHalfWidth
        carCoordinates.posY03 = posY + carHalfHeight

        carCoordinates.posX04 = posX + carWidth - carHalfWidth
        carCoordinates.posY04 = posY + carHalfHeight

    }

    fun drawHitbox(canvas: Canvas,
                   coordinateDisplayManager: CoordinateDisplayManager)
    {
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX01),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY01),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX02),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY02),
            hitboxPaint
        )
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX01),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY01),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX03),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY03),
            hitboxPaint
        )
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX02),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY02),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX04),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY04),
            hitboxPaint
        )
        canvas.drawLine(
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX03),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY03),
            coordinateDisplayManager.convertToEnvironmentX(carCoordinates.posX04),
            coordinateDisplayManager.convertToEnvironmentY(carCoordinates.posY04),
            hitboxPaint
        )
    }

    fun collisionCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {

        val left: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX01,
            carCoordinates.posY01,
            carCoordinates.posX03,
            carCoordinates.posY03
        )
        val right: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX02,
            carCoordinates.posY02,
            carCoordinates.posX04,
            carCoordinates.posY04
        )
        val top: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX01,
            carCoordinates.posY01,
            carCoordinates.posX02,
            carCoordinates.posY02
        )
        var bottom: Boolean = Mathematics.collisionLineToLine(
            x1,
            y1,
            x2,
            y2,
            carCoordinates.posX03,
            carCoordinates.posY03,
            carCoordinates.posX04,
            carCoordinates.posY04
        )

        if (left || right || top || bottom) {
            return true
        }

        return false
    }
}