package salicki.pawel.blindcarrally.data

data class TrackLineData(
    val xStart: Float,
    val yStart: Float,
    val xEnd: Float,
    val yEnd: Float) {}

data class TrackRoadData(
    val left: List<TrackLineData>,
    val right: List<TrackLineData>
) {}

data class TrackRaceData(
    val startPointX: Int,
    val startPointY: Int
) {
    var trackRoad : MutableList<TrackRoadData>? = null
}

data class TileData(
    val trackName: String,
    val offsetX: Int,
    val offsetY: Int
)


data class TrackData(
    var roadList: ArrayList<RoadTileData> = ArrayList()
)

data class RoadTileData(
    var leftPoints: MutableList<RoadPointsData> = mutableListOf(),
    var rightPints: MutableList<RoadPointsData> = mutableListOf(),
    var spawnX: Int = 0,
    var spawnY: Int = 0,
    var finishX: Int = 0,
    var finishY: Int = 0
){}

data class RoadPointsData(
    var startX: Int = 0,
    var startY: Int = 0,
    var endX: Int = 0,
    var endY: Int = 0
)