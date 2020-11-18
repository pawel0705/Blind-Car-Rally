package salicki.pawel.blindcarrally.gameresources

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import salicki.pawel.blindcarrally.datas.SelectBoxModelData
import salicki.pawel.blindcarrally.information.Settings

class SelectBoxManager {
    private var selectBoxModelData: SelectBoxModelData = SelectBoxModelData()
    private var paint = Paint()
    private var size = 0

    fun initSelectBoxModel(elementsNumber: Int){
        size = elementsNumber
        paint.color = Color.GREEN

        selectBoxModelData.posX1 = 0F;
        selectBoxModelData.posY1 = 0F;

        selectBoxModelData.posX2 = (Settings.SCREEN_WIDTH / size).toFloat();
        selectBoxModelData.posY2 = 0F;

        selectBoxModelData.posX3 = 0F
        selectBoxModelData.posY3 = Settings.SCREEN_HEIGHT.toFloat()

        selectBoxModelData.posX4 =  (Settings.SCREEN_WIDTH / size).toFloat();
        selectBoxModelData.posY4 = Settings.SCREEN_HEIGHT.toFloat()
    }

    fun updateSelectBoxPosition(elementIterator: Int){
        selectBoxModelData.posX1 = 0F + elementIterator* Settings.SCREEN_WIDTH / size;
        selectBoxModelData.posY1 = 0F;

        selectBoxModelData.posX2 = (Settings.SCREEN_WIDTH / size).toFloat() + elementIterator  * Settings.SCREEN_WIDTH / size;
        selectBoxModelData.posY2 = 0F;

        selectBoxModelData.posX3 = 0F + elementIterator * Settings.SCREEN_WIDTH / size;
        selectBoxModelData.posY3 = Settings.SCREEN_HEIGHT.toFloat()

        selectBoxModelData.posX4 =  (Settings.SCREEN_WIDTH / size).toFloat() + elementIterator  * Settings.SCREEN_WIDTH / size;
        selectBoxModelData.posY4 = Settings.SCREEN_HEIGHT.toFloat()
    }

    fun drawSelectBox(canvas: Canvas){

        canvas.drawLine(
            selectBoxModelData.posX1,
            selectBoxModelData.posY1,
            selectBoxModelData.posX2,
            selectBoxModelData.posY2,
            paint
        )
        canvas.drawLine(
            selectBoxModelData.posX1,
            selectBoxModelData.posY1,
            selectBoxModelData.posX3,
            selectBoxModelData.posY3,
            paint
        )
        canvas.drawLine(
            selectBoxModelData.posX2,
            selectBoxModelData.posY2,
            selectBoxModelData.posX4,
            selectBoxModelData.posY4,
            paint
        )
        canvas.drawLine(
            selectBoxModelData.posX3,
            selectBoxModelData.posY3,
            selectBoxModelData.posX4,
            selectBoxModelData.posY4,
            paint
        )
    }
}