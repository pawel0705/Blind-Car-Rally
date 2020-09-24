package salicki.pawel.blindcarrally

import android.graphics.Canvas
import kotlin.math.pow
import kotlin.math.sqrt

abstract class EnvironmentObject(posX : Float, posY: Float) {

    var posX : Float = posX
    var posY : Float = posY
    var dirX : Float = 0.0F
    var dirY : Float = 0.0F
    var velX : Float = 0.0F
    var velY : Float = 0.0F

    abstract fun draw(canvas: Canvas?, coordinateDisplayManager: CoordinateDisplayManager?)
    abstract fun update()

    open fun calculateDistance(obj: EnvironmentObject): Float {
        return sqrt(
            (obj.posX - this.posX).pow(2.0F) +
                    (obj.posY - this.posY).pow(2.0F)
        )
    }
}