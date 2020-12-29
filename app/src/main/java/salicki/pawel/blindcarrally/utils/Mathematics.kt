package salicki.pawel.blindcarrally.utils

import java.util.*
import kotlin.math.sqrt

object Mathematics {
    private val random = Random()

    fun randFloat(from: Float, to: Float): Float {
        return random.nextFloat() * (to - from) + from;
    }

    fun randInt(from: Int, to: Int): Int {
        return (from..to).random()
    }

    fun distancePointToLine(
        pointX: Float,
        pointY: Float,
        lineX1: Float,
        lineY1: Float,
        lineX2: Float,
        lineY2: Float
    ): Double {

        var A: Float = pointX - lineX1
        var B: Float = pointY - lineY1
        var C: Float = lineX2 - lineX1
        var D: Float = lineY2 - lineY1

        var dot: Float = A * C + B * D
        var sqLength = C * C + D * D
        var param = -1F
        if (sqLength > 0.001F || sqLength < 0.001F) {
            param = dot / sqLength;
        }

        var xx: Float = 0F
        var yy: Float = 0F

        when {
            param < 0 -> {
                xx = lineX1
                yy = lineY1
            }
            param > 1 -> {
                xx = lineX2
                yy = lineY2
            }
            else -> {
                xx = lineX1 + param * C
                yy = lineY1 + param * D
            }
        }

        var dx = pointX - xx
        var dy = pointY - yy

        return sqrt((dx * dx + dy * dy).toDouble())
    }

    fun collisionLineToLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        x4: Float,
        y4: Float
    ): Boolean {
        val uA =
            (((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)))
        val uB =
            (((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)))

        if (uA in 0.0..1.0 && uB in 0.0..1.0) {
            return true
        }

        return false
    }
}