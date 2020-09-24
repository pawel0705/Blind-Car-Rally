package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Paint

abstract class Circle(color: Int?, posX: Float, posY: Float, radius: Float) : EnvironmentObject(
    posX,
    posY
) {
    protected var radius : Float = radius
    protected var paint: Paint = Paint()

    init {
        if (color != null) {
            paint.color = color
        }
    }

    open fun isColliding(obj: Circle): Boolean {
        val distance: Float = calculateDistance(obj)
        val distanceToCollision: Float = this.radius + obj.radius
        return distance < distanceToCollision
    }

    override fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?) {
        canvas?.drawCircle(
            coordinateDisplayManager?.convertToEnvironmentX(posX) as Float,
            coordinateDisplayManager?.convertToEnvironmentY(posY) as Float,
            radius,
            paint
        )
    }


}