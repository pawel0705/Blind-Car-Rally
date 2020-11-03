package salicki.pawel.blindcarrally

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import salicki.pawel.blindcarrally.data.RoadPointsData
import salicki.pawel.blindcarrally.data.RoadTileData
import salicki.pawel.blindcarrally.data.TileData
import salicki.pawel.blindcarrally.data.TrackData
import java.io.InputStream

class TrackReader {
    private lateinit var factory: XmlPullParserFactory
    private lateinit var parser: XmlPullParser
    private lateinit var xmlFile: InputStream

    fun readRacingTrack(trackName: String) : TrackData{
        var trackNames = mutableListOf<String>()
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
                        Log.d("ROAD", "road");

                        var name: String = parser.getAttributeValue(0)

                        trackNames.add(name)
                    }
                }
            }

            event = parser.next()
        }

        var trackData = TrackData()
        for(track in trackNames){

            Log.d("NAME", track)
            trackData.roadList?.add(this.readTrackTile(track))
        }

        return trackData
    }

    private fun readTrackTile(trackName: String) : RoadTileData{

        Log.d("READ", "rad")

        xmlFile = Settings.CONTEXT?.assets?.open(trackName)!!

        factory = XmlPullParserFactory.newInstance()
        parser = factory.newPullParser()

        parser.setInput(xmlFile, null)

        var left: Boolean = false
        var right: Boolean = false

        var road = RoadTileData()

        var lastX = 0
        var lastY = 0

        var firstRight = false
        var firstLeft = false

        var event: Int = parser.eventType

        Log.d("EVENT", event.toString())

        while (event != XmlPullParser.END_DOCUMENT) {
            var tag_name: String? = parser.name

            Log.d("TAG", tag_name.toString())

            when (event) {
                XmlPullParser.END_TAG -> {
                    when (tag_name) {
                        "tts_key"->{
                            var key: String = parser.getAttributeValue(0)
                            road.speakerKeys.add(key)
                        }
                        "left" -> {
                            right = false
                            left = true
                            lastX = 0
                            lastY = 0

                            Log.d("LEFT", "left");
                        }
                        "right" -> {
                            right = true
                            left = false
                            lastX = 0
                            lastY = 0
                            Log.d("RIGHT", "right");
                        }
                        "point" -> {
                            var x: String = parser.getAttributeValue(0)
                            var y: String = parser.getAttributeValue(1)

                            var x0 = x.toFloat() * Settings.SCREEN_SCALE * 0.02F
                            var y0 = y.toFloat() * Settings.SCREEN_SCALE * 0.02F

                            if(left){
                                if(firstLeft){
                                    road.leftPoints?.add(RoadPointsData(lastX, lastY, x0.toInt(), y0.toInt()))
                                }
                                firstLeft = true
                            } else{
                                if(firstRight){
                                    road.rightPints?.add(RoadPointsData(lastX, lastY, x0.toInt(), y0.toInt()))
                                }
                                firstRight = true
                            }

                            lastX = x0.toInt()
                            lastY = y0.toInt()

                            Log.d("POINT", "point");
                        }
                        "spawn" -> {
                            var x: String = parser.getAttributeValue(0)
                            var y: String = parser.getAttributeValue(1)

                            road.spawnX = (x.toInt() * Settings.SCREEN_SCALE * 0.02F).toInt()
                            road.spawnY = (y.toInt() * Settings.SCREEN_SCALE * 0.02F).toInt()
                            Log.d("SPAWN", "spawn");
                        }
                        "finish" -> {
                            var x: String = parser.getAttributeValue(0)
                            var y: String = parser.getAttributeValue(1)

                            road.finishX = (x.toInt() * Settings.SCREEN_SCALE * 0.02F).toInt()
                            road.finishY = (y.toInt() * Settings.SCREEN_SCALE * 0.02F).toInt()

                            Log.d("FINISH", "finish");
                        }
                    }
                }
            }

            event = parser.next()
        }

        Log.d("ROAD", road.toString())

        return road
    }
}