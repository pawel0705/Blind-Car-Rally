package salicki.pawel.blindcarrally.datas

import android.graphics.Paint
import android.graphics.RectF
import salicki.pawel.blindcarrally.gameresources.Texture

data class CarViewData(
    var carHalfWidth: Float = 0.0F,
    var carHalfHeight: Float = 0.0F,
    var carRectangle: RectF? = null,
    var carTexture: Texture? = null,
    var carPaint: Paint = Paint(),
) {}