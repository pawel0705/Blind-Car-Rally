package salicki.pawel.blindcarrally

import android.graphics.*

class Texture(bitmap: Bitmap) {
    private var bitmap: Bitmap = bitmap

    fun drawTexture(canvas: Canvas, rect: RectF) {
        scaleTexture(rect)
        canvas.drawBitmap(bitmap, null, rect, Paint())
    }

    private fun scaleTexture(rect: RectF) {
        val whRatio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()


        if (rect.width() > rect.height()) rect.left =
            rect.right - (rect.height() * whRatio).toInt() else rect.top =
            rect.bottom - (rect.width() * (1 / whRatio)).toInt()
    }
}