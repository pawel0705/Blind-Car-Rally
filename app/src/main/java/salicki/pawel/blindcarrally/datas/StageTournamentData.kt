package salicki.pawel.blindcarrally.datas

import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.enums.StageEnum
import salicki.pawel.blindcarrally.enums.WeatherEnum

data class StageTournamentData(
    var stageFileName: String = "",
    var nationKey: String = "",
    var stageKey: String = "",
    var nation: NationEnum = NationEnum.AUSTRALIA,
    var stageEnum: StageEnum = StageEnum.STAGE_1,
    var weather: WeatherEnum = WeatherEnum.SUN,
) {}