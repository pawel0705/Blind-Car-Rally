package salicki.pawel.blindcarrally.datas

data class SensorDetectData(
    var left: Boolean = false,
    var right: Boolean = false,

    var leftLength: Float = Float.MAX_VALUE,
    var rightLength: Float = Float.MAX_VALUE,
) {}