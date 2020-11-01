package salicki.pawel.blindcarrally

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import salicki.pawel.blindcarrally.data.SelectBoxModel

class SelectBoxManager {
    private var selectBoxModel: SelectBoxModel = SelectBoxModel()
    private var paint = Paint()
    private var size = 0

    fun initSelectBoxModel(elementsNumber: Int){
        size = elementsNumber
        paint.color = Color.GREEN

        selectBoxModel.posX1 = 0F;
        selectBoxModel.posY1 = 0F;

        selectBoxModel.posX2 = (Settings.SCREEN_WIDTH / size).toFloat();
        selectBoxModel.posY2 = 0F;

        selectBoxModel.posX3 = 0F
        selectBoxModel.posY3 = Settings.SCREEN_HEIGHT.toFloat()

        selectBoxModel.posX4 =  (Settings.SCREEN_WIDTH / size).toFloat();
        selectBoxModel.posY4 = Settings.SCREEN_HEIGHT.toFloat()
    }

    fun updateSelectBoxPosition(elementIterator: Int){
        selectBoxModel.posX1 = 0F + elementIterator* Settings.SCREEN_WIDTH / size;
        selectBoxModel.posY1 = 0F;

        selectBoxModel.posX2 = (Settings.SCREEN_WIDTH / size).toFloat() + elementIterator  * Settings.SCREEN_WIDTH / size;
        selectBoxModel.posY2 = 0F;

        selectBoxModel.posX3 = 0F + elementIterator * Settings.SCREEN_WIDTH / size;
        selectBoxModel.posY3 = Settings.SCREEN_HEIGHT.toFloat()

        selectBoxModel.posX4 =  (Settings.SCREEN_WIDTH / size).toFloat() + elementIterator  * Settings.SCREEN_WIDTH / size;
        selectBoxModel.posY4 = Settings.SCREEN_HEIGHT.toFloat()
    }

    fun drawSelectBox(canvas: Canvas){

        canvas.drawLine(
            selectBoxModel.posX1,
            selectBoxModel.posY1,
            selectBoxModel.posX2,
            selectBoxModel.posY2,
            paint
        )
        canvas.drawLine(
            selectBoxModel.posX1,
            selectBoxModel.posY1,
            selectBoxModel.posX3,
            selectBoxModel.posY3,
            paint
        )
        canvas.drawLine(
            selectBoxModel.posX2,
            selectBoxModel.posY2,
            selectBoxModel.posX4,
            selectBoxModel.posY4,
            paint
        )
        canvas.drawLine(
            selectBoxModel.posX3,
            selectBoxModel.posY3,
            selectBoxModel.posX4,
            selectBoxModel.posY4,
            paint
        )
    }
}