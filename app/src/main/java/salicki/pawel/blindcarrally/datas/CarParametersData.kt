package salicki.pawel.blindcarrally.datas

import salicki.pawel.blindcarrally.information.GameOptions

data class CarParametersData(
    var speed: Float = 0.0F,
    var angle: Float = 0.0F,
    var obstacleSensorLength: Float = 0.0F,
    var maxGear: Int = 6,
    var minGear: Int = 1,
    var gear: Int = 1,
    var health: Int = 100,

    var maxSpeed: Float = 1F * GameOptions.carTopSpeed,
    var turnSpeed: Float = 0.008F * GameOptions.carManeuverability,
    var acceleration: Float = 1F * GameOptions.carAcceleration,
) {}