package salicki.pawel.blindcarrally

import salicki.pawel.blindcarrally.scenemanager.LevelType

object GameOptions {
    lateinit var gamemode: RacingModeEnum
    lateinit var nation: NationEnum
    lateinit var stage: StageEnum
    lateinit var car: CarEnum

    var carTopSpeed: Float = 0.0F
    var carAcceleration: Float = 0.0F
    var carManeuverability: Float = 0.0F
}