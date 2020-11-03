package salicki.pawel.blindcarrally.data

import android.graphics.Paint
import android.graphics.RectF
import salicki.pawel.blindcarrally.Texture

data class CarParameters(
    var speed: Float = 0F,
    var angle: Float = 0F,
    var maxSpeed: Float = 2F,
    var turnSpeed: Float = 0.008F,
    var maxGear: Int = 6,
    var minGear: Int = 0,
    var gear: Int = 1,
    var obstacleSensorLength: Float = 0F
) {}

data class CarView(
    var carRectangle: RectF? = null,
    var carTexture: Texture? = null,
    var carPaint: Paint = Paint(),
    var carHalfWidth: Float = 0F,
    var carHalfHeight: Float = 0F
) {}

data class SpeakerSpecial(
    var TEXT_KEY: String = "",
    var speaked: Boolean = false
)