package salicki.pawel.blindcarrally.data

import salicki.pawel.blindcarrally.scenemanager.LevelType

data class OptionSelectionData(
    val levelType: Any,
    val textKey: String,
    val textValue: String,
    var selected: Boolean) {}