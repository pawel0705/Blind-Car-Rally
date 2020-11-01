package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat

class TextObject {

    private var textPaint: Paint = Paint()
    private var posX: Float = 0F
    private var posY: Float = 0F

    fun initText(fontId: Int, positionX: Float, positionY: Float) {

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.isFilterBitmap = true

        val customTypeface = Settings.CONTEXT?.let { ResourcesCompat.getFont(it, fontId) }

        textPaint.typeface = customTypeface


        posX = positionX
        posY = (positionY - (textPaint.descent() + textPaint.ascent()) / 2)

        textPaint.textSize = Settings.CONTEXT?.resources?.getDimensionPixelSize(R.dimen.selectFontSize)!!.toFloat()
    }

    fun drawText(canvas: Canvas, text: String){
        canvas.drawText(
            text,
            posX,
            posY,
            textPaint
        )
    }
}