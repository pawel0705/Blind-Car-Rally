package salicki.pawel.blindcarrally.data

import android.graphics.Paint
import android.graphics.RectF
import salicki.pawel.blindcarrally.GameOptions
import salicki.pawel.blindcarrally.Texture

data class CarParameters(
    var speed: Float = 0F,
    var angle: Float = 0F,
    var maxGear: Int = 6,
    var minGear: Int = 1,
    var gear: Int = 1,
    var obstacleSensorLength: Float = 0F,
    var health: Int = 100,

    var maxSpeed: Float = 1F * GameOptions.carTopSpeed,
    var turnSpeed: Float = 0.008F * GameOptions.carManeuverability,
    var acceleration: Float = 1F * GameOptions.carAcceleration,
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