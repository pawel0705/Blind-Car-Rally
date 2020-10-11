package salicki.pawel.blindcarrally

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import salicki.pawel.blindcarrally.data.TileData
import salicki.pawel.blindcarrally.data.TrackLineData
import salicki.pawel.blindcarrally.data.TrackRaceData
import salicki.pawel.blindcarrally.data.TrackRoadData
import java.io.InputStream

object XMLParser {
    private lateinit var factory: XmlPullParserFactory
    private lateinit var parser: XmlPullParser
    private lateinit var xmlFile: InputStream

    private var tileOffsetX: Int = 0
    private var tileOffsetY: Int = 0

    private var startPointX: Int = 0
    private var startPointY: Int = 0

    private var lastLeftX = 0
    private var lastLeftY = 0

    private var lastRightX = 0
    private var lastRightY = 0

    fun read(trackName: String): TrackRaceData {
        lastLeftX = 0
        lastLeftY = 0

        lastRightX = 0
        lastRightY = 0

        var trackNames = mutableListOf<TileData>()

        xmlFile = Settings.CONTEXT?.assets?.open(trackName)!!

        factory = XmlPullParserFactory.newInstance()
        parser = factory.newPullParser()

        parser.setInput(xmlFile, null)

        var event: Int = parser.eventType

        while (event != XmlPullParser.END_DOCUMENT) {
            var tag_name: String? = parser.name

            when (event) {
                XmlPullParser.END_TAG -> {
                    if (tag_name == "road") {
                        var name: String = parser.getAttributeValue(0)

                        Log.d("TAK", "ROAD")

                        trackNames.add(TileData(name, tileOffsetX, tileOffsetY))

                        Log.d("OFFSET", tileOffsetX.toString())
                        Log.d("OFFSET", tileOffsetY.toString())
                    } else if(tag_name=="start"){
                        startPointX = (parser.getAttributeValue(0).toFloat() * Settings.SCREEN_SCALE * 0.02F).toInt()
                        startPointY = (parser.getAttributeValue(1).toFloat() * Settings.SCREEN_SCALE * 0.02F).toInt()

                        Log.d("TAK", "START")
                    } else if(tag_name=="tile_up"){
                        tileOffsetX = (0 * Settings.SCREEN_SCALE * 0.02F).toInt()
                        tileOffsetY = (100 * Settings.SCREEN_SCALE * 0.02F).toInt()

                        Log.d("TAK", "UP")
                    } else if(tag_name=="tile_right"){
                        tileOffsetX = (100 * Settings.SCREEN_SCALE * 0.02F).toInt()
                        tileOffsetY = (0 * Settings.SCREEN_SCALE * 0.02F).toInt()

                        Log.d("TAK", "RIGHT")
                    }
                }
            }

            event = parser.next()
        }


        var trackData = mutableListOf<TrackRoadData>()

        for (track in trackNames) {
            trackData.add(this.readTrack(track))
        }

        var trackRaceData : TrackRaceData = TrackRaceData(startPointX, startPointY)
        trackRaceData.trackRoad = trackData

        return trackRaceData
    }

    private fun readTrack(roadName: TileData): TrackRoadData {
        xmlFile = Settings.CONTEXT?.assets?.open(roadName.trackName)!!

        factory = XmlPullParserFactory.newInstance()
        parser = factory.newPullParser()

        parser.setInput(xmlFile, null)

        var event: Int = parser.eventType

        var trackDateLeft = mutableListOf<TrackLineData>()
        var trackDateRight = mutableListOf<TrackLineData>()

        var xLeft: Float = 0F
        var yLeft: Float = 0F
        var xRight: Float = 0F
        var yRight: Float = 0F
        var right: Boolean = false
        var firstPoint: Boolean = false

        lastLeftX += roadName.offsetX
        lastLeftY += roadName.offsetY

        Log.d("X_LEFT", lastLeftX.toString())
        Log.d("Y_LEFT", lastLeftY.toString())

        lastRightX += roadName.offsetX
        lastRightY += roadName.offsetY

        Log.d("X_RIGHT", lastRightX.toString())
        Log.d("Y_RIGHT", lastRightY.toString())

        while (event != XmlPullParser.END_DOCUMENT) {
            var tag_name: String? = parser.name

            when (event) {
                XmlPullParser.END_TAG -> {
                    if (tag_name == "left") {
                        right = false
                        firstPoint = true

                    //    Log.d("LEFT", "LEFT")
                    }

                    if (tag_name == "right") {
                        right = true
                        firstPoint = true

                    //    Log.d("RIGHT", "RIGHT")
                    }

                    if (tag_name == "point") {
                        var x: String = parser.getAttributeValue(0)
                        var y: String = parser.getAttributeValue(1)

                        var x0 = x.toFloat() * Settings.SCREEN_SCALE * 0.02F
                        var y0 = y.toFloat() * Settings.SCREEN_SCALE * 0.02F

                 //       Log.d("X", x.toString())
                 //       Log.d("Y", y.toString())

                        if (firstPoint) {
                            firstPoint = false
/*
                            if(right){
                                trackDateRight.add(
                                    TrackLineData(
                                        x0 + lastRightX.toFloat(),
                                        y0 + lastRightY,
                                        xRight.toFloat() + lastRightX,
                                        yRight.toFloat() + lastRightY
                                    )
                                )
                            }
*/
                        } else {
                            if (!right) {
                                trackDateLeft.add(
                                    TrackLineData(
                                        x0+ lastLeftX,
                                        y0+ lastLeftY,
                                        xLeft.toFloat() + lastLeftX,
                                        yLeft.toFloat() + lastLeftY
                                    )
                                )
                            } else {
                                trackDateRight.add(
                                    TrackLineData(
                                        x0 + lastRightX,
                                        y0+ lastRightY,
                                        xRight.toFloat() + lastRightX,
                                        yRight.toFloat() + lastRightY
                                    )
                                )
                            }
                        }

                        if (!right) {
                            xLeft = x0
                            yLeft = y0
                        } else {
                            xRight = x0
                            yRight = y0
                        }
                    }
                }
            }

            event = parser.next()
        }



        return TrackRoadData(trackDateLeft, trackDateRight)
    }
}