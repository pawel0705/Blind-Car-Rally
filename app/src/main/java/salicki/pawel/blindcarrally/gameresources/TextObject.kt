package salicki.pawel.blindcarrally.gameresources

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import salicki.pawel.blindcarrally.R
import salicki.pawel.blindcarrally.information.Settings

class TextObject {

    private var textPaint: Paint = Paint()
    private var infoTextPaint = TextPaint()
    private var posX: Float = 0F
    private var posY: Float = 0F
    private lateinit var textLayout: StaticLayout


    fun initMultiLineText(
        fontId: Int,
        fontSizeId: Int,
        positionX: Float,
        positionY: Float,
        text: String
    ) {
        infoTextPaint.color = Color.WHITE
        infoTextPaint.textAlign = Paint.Align.CENTER

        val customTypeface = Settings.CONTEXT?.let { ResourcesCompat.getFont(it, fontId) }

        infoTextPaint.typeface = customTypeface
        infoTextPaint.isAntiAlias = true
        infoTextPaint.isFilterBitmap = true

        posX = positionX
        posY = positionY - (infoTextPaint.descent() + infoTextPaint.ascent()) / 2

        infoTextPaint.textSize =
            Settings.CONTEXT?.resources?.getDimensionPixelSize(fontSizeId)!!.toFloat()

        textLayout = StaticLayout(
            text,
            infoTextPaint,
            (Settings.SCREEN_WIDTH * 0.8F).toInt(),
            Layout.Alignment.ALIGN_NORMAL,
            1.0f,
            0.0f,
            false
        );
    }

    fun initText(fontId: Int, positionX: Float, positionY: Float, fontSizeId: Int = R.dimen.selectFontSize) {

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.isFilterBitmap = true

        val customTypeface = Settings.CONTEXT?.let { ResourcesCompat.getFont(it, fontId) }

        textPaint.typeface = customTypeface


        posX = positionX
        posY = (positionY - (textPaint.descent() + textPaint.ascent()) / 2)

        textPaint.textSize =
            Settings.CONTEXT?.resources?.getDimensionPixelSize(fontSizeId)!!.toFloat()
    }

    fun drawText(canvas: Canvas, text: String) {
        canvas.drawText(
            text,
            posX,
            posY,
            textPaint
        )
    }

    fun drawMultilineText(canvas: Canvas) {
        canvas.save()
        canvas.translate(posX, posY)
        textLayout.draw(canvas);

        canvas.restore()
    }
}