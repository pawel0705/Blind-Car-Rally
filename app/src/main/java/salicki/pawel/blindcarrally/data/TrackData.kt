package salicki.pawel.blindcarrally.data

data class TrackDate(val xStart: Float, val yStart: Float, val xEnd: Float, val yEnd: Float) {}

data class LeftRightDate(val left: List<TrackDate>, val right: List<TrackDate>) {}