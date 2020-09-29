package salicki.pawel.blindcarrally

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import salicki.pawel.blindcarrally.data.LeftRightDate
import salicki.pawel.blindcarrally.data.TrackDate
import java.io.InputStream

object XMLParser {
    private lateinit var factory: XmlPullParserFactory
    private lateinit var parser: XmlPullParser
    private lateinit var xmlFile: InputStream

    private var lastLeftX: Int = 0
    private var lastLeftY: Int = 0

    private var lastRightX: Int = 0
    private var lastRightY: Int = 0

    fun read(trackName: String): MutableList<LeftRightDate> {

        lastLeftX = 0
        lastLeftY = 0

        lastRightX = 0
        lastRightY = 0

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
                        var name: String = parser.getAttributeValue(0)

                        trackNames.add(name)
                    }
                }
            }

            event = parser.next()
        }


        var trackDate = mutableListOf<LeftRightDate>()

        for (track in trackNames) {
            trackDate.add(this.readTrack(track))
        }

        return trackDate
    }

    private fun readTrack(roadName: String): LeftRightDate {
        xmlFile = Settings.CONTEXT?.assets?.open(roadName)!!

        factory = XmlPullParserFactory.newInstance()
        parser = factory.newPullParser()

        parser.setInput(xmlFile, null)

        var event: Int = parser.eventType

        var trackDateLeft = mutableListOf<TrackDate>()
        var trackDateRight = mutableListOf<TrackDate>()

        var xLeft: String = "0"
        var yLeft: String = "0"
        var xRight: String = "0"
        var yRight: String = "0"
        var right: Boolean = false
        var firstPoint: Boolean = false

        while (event != XmlPullParser.END_DOCUMENT) {
            var tag_name: String? = parser.name

            when (event) {
                XmlPullParser.END_TAG -> {
                    if (tag_name == "left") {
                        right = false
                        firstPoint = true

                        Log.d("LEFT", "LEFT")
                    }

                    if (tag_name == "right") {
                        right = true
                        firstPoint = true

                        Log.d("RIGHT", "RIGHT")
                    }

                    if (tag_name == "point") {
                        var x: String = parser.getAttributeValue(0)
                        var y: String = parser.getAttributeValue(1)

                        Log.d("X", x.toString())
                        Log.d("Y", y.toString())

                        if (firstPoint) {
                            firstPoint = false
                        } else {
                            if (!right) {
                                trackDateLeft.add(
                                    TrackDate(
                                        x.toFloat() + lastLeftX,
                                        y.toFloat() + lastLeftY,
                                        xLeft.toFloat() + lastLeftX,
                                        yLeft.toFloat() + lastLeftY
                                    )
                                )
                            } else {
                                trackDateRight.add(
                                    TrackDate(
                                        x.toFloat() + lastRightX,
                                        y.toFloat() + lastRightY,
                                        xRight.toFloat() + lastRightX,
                                        yRight.toFloat() + lastRightY
                                    )
                                )
                            }
                        }

                        if (!right) {
                            xLeft = x
                            yLeft = y
                        } else {
                            xRight = x
                            yRight = y
                        }
                    }
                }
            }

            event = parser.next()
        }

        lastLeftX += xLeft.toInt()
        lastLeftY += yLeft.toInt()

        Log.d("X_LEFT", xLeft.toString())
        Log.d("Y_LEFT", yLeft.toString())

        lastRightX += xRight.toInt()
        lastRightY += yRight.toInt()

        Log.d("X_RIGHT", xRight.toString())
        Log.d("Y_RIGHT", yRight.toString())

        return LeftRightDate(trackDateLeft, trackDateRight)
    }
}