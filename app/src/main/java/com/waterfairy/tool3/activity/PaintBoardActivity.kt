package com.waterfairy.tool3.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.waterfairy.tool3.R
import com.waterfairy.utils.ImageUtils
import com.waterfairy.utils.ToastUtils
import com.waterfairy.widget.paintboard.EraserGraph
import com.waterfairy.widget.paintboard.LineGraph
import kotlinx.android.synthetic.main.activity_paint_board.*
import java.io.File

class PaintBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint_board)

        if (File(savePath).exists()) {
            paint_board_view.setBackgroundDrawable(BitmapDrawable(BitmapFactory.decodeFile(savePath)))
        }

        paint_board_view.setGraph(LineGraph(Color.RED, 10))
    }

    val savePath = "/sdcard/test/image/jjj.png"

    fun eraser(view: View) {
        paint_board_view.setGraph(EraserGraph(10))
    }

    fun paint(view: View) {
        paint_board_view.setGraph(LineGraph(Color.RED, 10))
    }

    fun back(view: View) {
        if (!paint_board_view.back()) {
            ToastUtils.show("不能向后")
        }
    }

    fun forward(view: View) {
        if (!paint_board_view.forward()) {
            ToastUtils.show("不能向前")
        }
    }

    fun save(view: View) {


        val bitmap1 = ImageUtils.getBitmapFromView2(paint_board_view)
        iv_paint.setImageBitmap(bitmap1)

        ImageUtils.saveBitmap(savePath, bitmap1, Bitmap.CompressFormat.PNG, 100)

        val bitmap2 = ImageUtils.getBitmapFromView2(iv_src)
        iv_merger.setImageBitmap(merger(bitmap1, bitmap2))


    }

    fun merger(bitmap1: Bitmap, bitmap2: Bitmap): Bitmap {
        val createBitmap = Bitmap.createBitmap(bitmap1)
        val canvas = Canvas(createBitmap)
        canvas.drawBitmap(bitmap2, 0f, 0f, null)
        canvas.drawBitmap(bitmap1, 0f, 0f, null)
        return createBitmap
    }
}