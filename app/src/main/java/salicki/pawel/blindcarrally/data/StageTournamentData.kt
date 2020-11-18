package salicki.pawel.blindcarrally.data

import salicki.pawel.blindcarrally.NationEnum
import salicki.pawel.blindcarrally.StageEnum
import salicki.pawel.blindcarrally.WeatherEnum

data class StageTournamentData(
    var stageFileName: String = "",
    var nationKey: String = "",
    var stageKey: String = "",
    var nation: NationEnum = NationEnum.AUSTRALIA,
    var stageEnum: StageEnum = StageEnum.STAGE_1,
    var weather: WeatherEnum = WeatherEnum.SUN
)
{}