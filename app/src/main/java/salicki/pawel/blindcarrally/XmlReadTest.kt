package salicki.pawel.blindcarrally

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

object XmlReadTest {

    fun read() : LeftRightDate{
        var xml_data : InputStream? = Settings.CONTEXT?.assets?.open("test.xml")

        var factory : XmlPullParserFactory = XmlPullParserFactory.newInstance()
        var parser : XmlPullParser = factory.newPullParser()

        parser.setInput(xml_data, null)

        var event : Int = parser.eventType

        var trackDateLeft  = mutableListOf<TrackDate>()
        var trackDateRight = mutableListOf<TrackDate>()

        var x0 : String = "0"
        var y0: String = "0"
        var right: Boolean = false

        while(event!=XmlPullParser.END_DOCUMENT){
            var tag_name : String? = parser.name

            when(event) {
                XmlPullParser.END_TAG->{
                    if(tag_name == "left"){
                        right = false
                        x0 = "0"
                        y0 = "0"
                    }

                    if(tag_name == "right"){
                        right = true
                        x0 = "0"
                        y0 = "0"
                    }

                    if(tag_name == "point"){
                        var x: String = parser.getAttributeValue(0)
                        var y: String = parser.getAttributeValue(1)

                        if(!right)
                        {
                            trackDateLeft.add(TrackDate(x.toFloat(), y.toFloat(), x0.toFloat(), y0.toFloat()))
                        }
                        else{
                            trackDateRight.add(TrackDate(x.toFloat(), y.toFloat(), x0.toFloat(), y0.toFloat()))
                        }

                        x0 = x
                        y0 = y

                        Log.d("X", x)
                        Log.d("Y", y)
                    }
                }
            }

            event = parser.next()
        }



        return LeftRightDate(trackDateLeft, trackDateRight)
    }

}