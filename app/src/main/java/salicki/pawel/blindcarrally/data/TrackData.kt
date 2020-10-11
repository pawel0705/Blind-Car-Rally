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