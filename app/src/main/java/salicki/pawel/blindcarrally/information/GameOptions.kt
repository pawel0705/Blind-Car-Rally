package salicki.pawel.blindcarrally.information

import salicki.pawel.blindcarrally.enums.CarEnum
import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.enums.RacingModeEnum
import salicki.pawel.blindcarrally.enums.StageEnum

object GameOptions {
    lateinit var gamemode: RacingModeEnum
    lateinit var car: CarEnum

    var carTopSpeed: Float = 0.0F
    var carAcceleration: Float = 0.0F
    var carManeuverability: Float = 0.0F

    lateinit var nation: NationEnum
    lateinit var stage: StageEnum

    var stageNumber: Int = 0
}