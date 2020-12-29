package salicki.pawel.blindcarrally.information

import salicki.pawel.blindcarrally.enums.CarEnum
import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.enums.RacingModeEnum
import salicki.pawel.blindcarrally.enums.StageEnum

object GameOptions {
    lateinit var car: CarEnum

    var carTopSpeed: Float = 0.0F
    var carAcceleration: Float = 0.0F
    var carManeuverability: Float = 0.0F

    var gamemode: RacingModeEnum = RacingModeEnum.SINGLE_RACE
    var nation: NationEnum = NationEnum.ARGENTINA
    var stage: StageEnum = StageEnum.STAGE_1

    var stageNumber: Int = 0
}