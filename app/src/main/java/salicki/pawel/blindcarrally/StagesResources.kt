package salicki.pawel.blindcarrally

import salicki.pawel.blindcarrally.data.StageTournamentData

object StagesResources {
    // tracks
    val argentina_1: String = "argentina_1.xml"
    val argentina_2: String = "argentina_2.xml"
    val argentina_3: String = "argentina_3.xml"

    val australia_1: String = "australia_1.xml"
    val australia_2: String = "australia_2.xml"
    val australia_3: String = "australia_3.xml"

    val poland_1: String = "poland_1.xml"
    val poland_2: String = "poland_2.xml"
    val poland_3: String = "poland_3.xml"

    val spain_1: String = "spain_1.xml"
    val spain_2: String = "spain_2.xml"
    val spain_3: String = "spain_3.xml"

    val zealand_1: String = "zealand_1.xml"
    val zealand_2: String = "zealand_1.xml"
    val zealand_3: String = "zealand_1.xml"

    val stageList: List<StageTournamentData> = listOf(
        StageTournamentData(argentina_1, "ARGENTINA", "ARGENTINA_1", NationEnum.ARGENTINA, StageEnum.STAGE_1, WeatherEnum.SUN),
        StageTournamentData(argentina_2, "ARGENTINA", "ARGENTINA_1", NationEnum.ARGENTINA, StageEnum.STAGE_2, WeatherEnum.RAIN),
        StageTournamentData(argentina_3, "ARGENTINA", "ARGENTINA_1", NationEnum.ARGENTINA, StageEnum.STAGE_3, WeatherEnum.WIND),
        StageTournamentData(australia_1, "AUSTRALIA", "AUSTRALIA_1", NationEnum.AUSTRALIA, StageEnum.STAGE_1, WeatherEnum.SUN),
        StageTournamentData(australia_2, "AUSTRALIA", "AUSTRALIA_1", NationEnum.AUSTRALIA, StageEnum.STAGE_2, WeatherEnum.RAIN),
        StageTournamentData(australia_3, "AUSTRALIA", "AUSTRALIA_1", NationEnum.ARGENTINA, StageEnum.STAGE_3, WeatherEnum.WIND),
        StageTournamentData(poland_1, "POLAND", "POLAND_1", NationEnum.POLAND, StageEnum.STAGE_1, WeatherEnum.SUN),
        StageTournamentData(poland_2, "POLAND", "POLAND_1", NationEnum.POLAND, StageEnum.STAGE_2, WeatherEnum.RAIN),
        StageTournamentData(poland_3, "POLAND", "POLAND_1", NationEnum.POLAND, StageEnum.STAGE_3, WeatherEnum.WIND),
        StageTournamentData(spain_1, "SPAIN", "SPAIN_1", NationEnum.SPAIN, StageEnum.STAGE_1, WeatherEnum.SUN),
        StageTournamentData(spain_2, "SPAIN", "SPAIN_1", NationEnum.SPAIN, StageEnum.STAGE_2, WeatherEnum.RAIN),
        StageTournamentData(spain_3, "SPAIN", "SPAIN_1", NationEnum.SPAIN, StageEnum.STAGE_3, WeatherEnum.WIND),
        StageTournamentData(zealand_1, "NEW_ZEALAND_1", "NEW_ZEALAND_1", NationEnum.NEW_ZEALAND, StageEnum.STAGE_1, WeatherEnum.SUN),
        StageTournamentData(zealand_2, "NEW_ZEALAND_1", "NEW_ZEALAND_1", NationEnum.NEW_ZEALAND, StageEnum.STAGE_2, WeatherEnum.RAIN),
        StageTournamentData(zealand_3, "NEW_ZEALAND_1", "NEW_ZEALAND_1", NationEnum.NEW_ZEALAND, StageEnum.STAGE_3, WeatherEnum.WIND)
    )
}