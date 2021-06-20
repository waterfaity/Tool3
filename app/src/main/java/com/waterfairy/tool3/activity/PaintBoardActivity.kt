package com.waterfairy.tool3.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.waterfairy.tool3.R
import com.waterfairy.widget.paintboard.EraserGraph
import com.waterfairy.widget.paintboard.LineGraph
import kotlinx.android.synthetic.main.activity_paint_board.*

class PaintBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint_board)

    }

    fun eraser(view: View) {
        paint_board_view.setGraph(EraserGraph(3))
    }

    fun paint(view: View) {
        paint_board_view.setGraph(LineGraph(Color.RED, 3))
    }

    fun back(view: View) {
        paint_board_view.back()
    }

    fun forward(view: View) {
        paint_board_view.forward()
    }
}