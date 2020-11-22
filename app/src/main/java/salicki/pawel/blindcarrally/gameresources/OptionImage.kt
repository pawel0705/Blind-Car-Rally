package salicki.pawel.blindcarrally.gameresources

import android.graphics.*
import salicki.pawel.blindcarrally.information.Settings

class OptionImage {

    private var rectangle: Rect = Rect()
    private lateinit var  image: Bitmap
    private var paint: Paint = Paint()

    fun setImage(imageId: Int, positionX: Int, positionY: Int, sizeId: Int) {
        val point = Point(positionX, positionY)

        paint.isAntiAlias = true
        paint.isFilterBitmap = true

        image = BitmapFactory.decodeResource(Settings.CONTEXT?.resources, imageId)

        Settings.CONTEXT?.resources?.getDimensionPixelSize(sizeId)?.let {
            rectangle.set(0,0,
                Settings.CONTEXT?.resources?.getDimensionPixelSize(sizeId)!!,
                it
            )
        }

        rectangle.set(
            point.x - rectangle.width() / 2,
            point.y - rectangle.height() / 2,
            point.x + rectangle.width() / 2,
            point.y + rectangle.height() / 2
        )

        val whRatio: Float =
            image.width.toFloat() / image.height
        if (rectangle.width() > rectangle.height()) {
            rectangle.left =
                rectangle.right - ((rectangle.height() * whRatio)).toInt()
        } else {
            rectangle.top =
                rectangle.bottom - ((rectangle.width() * (1 / whRatio))).toInt()
        }
    }

    fun setFullScreenImage(imageId: Int){
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        image = BitmapFactory.decodeResource(Settings.CONTEXT?.resources, imageId)
        rectangle.set(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT )
    }

    fun drawImage(canvas: Canvas){
        canvas.drawBitmap(image, null, rectangle, paint)
    }

    fun freeMemory(){
        image.recycle()
    }
}