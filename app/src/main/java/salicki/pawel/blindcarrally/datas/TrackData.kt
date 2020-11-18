package salicki.pawel.blindcarrally.datas

data class TrackData(
    var roadList: ArrayList<RoadTileData> = ArrayList(),
) {}

data class RoadTileData(
    var speakerKeys: ArrayList<String> = ArrayList(),
    var leftPoints: MutableList<RoadPointsData> = mutableListOf(),
    var rightPints: MutableList<RoadPointsData> = mutableListOf(),
    var spawnX: Int = 0,
    var spawnY: Int = 0,
    var finishX: Int = 0,
    var finishY: Int = 0,
) {}

data class RoadPointsData(
    var startX: Int = 0,
    var startY: Int = 0,
    var endX: Int = 0,
    var endY: Int = 0
) {}