package com.waterfairy.tool3.activity

import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.waterfairy.fileselector.FileSelectActivity
import com.waterfairy.fileselector.FileSelectOptions
import com.waterfairy.fileselector.FileSelector
import com.waterfairy.fileselector.imageloader.ImageLoader
import com.waterfairy.imageselect.ImageSelector
import com.waterfairy.imageselect.options.SelectImgOptions
import com.waterfairy.tool3.R
import kotlinx.android.synthetic.main.activity_matrix.*

/**
 *
 * MSCALE_X = 0
 * MSKEW_X  = 1
 * MTRANS_X = 2
 * MSKEW_Y  = 3
 * MSCALE_Y = 4
 * MTRANS_Y = 5
 * MPERSP_0 = 6
 * MPERSP_1 = 7
 * MPERSP_2 = 8
 *
 * 缩放 移动 倾斜
 * {
 *      MSCALE_X     MSKEW_X     MTRANS_X
 *      MSKEW_Y      MSCALE_Y    MTRANS_Y
 *      MPERSP_0     MPERSP_1    MPERSP_2
 * }
 *
 */
class MatrixActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix)
        iv_img.canZoom = true

    }

    var TAG = "MatrixActivity"


    fun reset(view: View?) {
        getMatrixTemp().setScale(1F, 1F)
        getMatrixTemp().setSkew(0F, 0F)
        getMatrixTemp().setTranslate(0F, 0F)

        printLog("reset:", getMatrixTemp());

    }

    fun set(view: View?) {

        //缩放
        var scaleX = et_scale_x.text.toString().toFloat()
        var scaleY = et_scale_y.text.toString().toFloat()
        if (cb_scale_x.isChecked && cb_scale_y.isChecked) {
            getMatrixTemp().setScale(scaleX, scaleY)
        }

        //倾斜
        var skewX = et_skew_x.text.toString().toFloat()
        var skewY = et_skew_y.text.toString().toFloat()
        if (cb_skew_x.isChecked && cb_skew_y.isChecked) {
            getMatrixTemp().setSkew(skewX, skewY)
        }

        //移动
        var transX = et_trans_x.text.toString().toFloat()
        var transY = et_trans_y.text.toString().toFloat()
        if (cb_trans_x.isChecked && cb_trans_y.isChecked) {
            getMatrixTemp().setTranslate(transX, transY)
        }

        printLog("set", getMatrixTemp());

    }

    private fun getMatrixTemp(): Matrix {
        return iv_img.imageMatrix
    }


    fun pre(view: View) {


        //缩放
        var scaleX = et_scale_x.text.toString().toFloat()
        var scaleY = et_scale_y.text.toString().toFloat()
        if (cb_scale_x.isChecked && cb_scale_y.isChecked) {
            getMatrixTemp().preScale(scaleX, scaleY)
        }

        //倾斜
        var skewX = et_skew_x.text.toString().toFloat()
        var skewY = et_skew_y.text.toString().toFloat()
        if (cb_skew_x.isChecked && cb_skew_y.isChecked) {
            getMatrixTemp().postSkew(skewX, skewY)
        }

        //移动
        var transX = et_trans_x.text.toString().toFloat()
        var transY = et_trans_y.text.toString().toFloat()
        if (cb_trans_x.isChecked && cb_trans_y.isChecked) {
            getMatrixTemp().preTranslate(transX, transY)
        }

        printLog("pre", getMatrixTemp());

    }

    fun post(view: View) {

        //缩放
        var scaleX = et_scale_x.text.toString().toFloat()
        var scaleY = et_scale_y.text.toString().toFloat()
        if (cb_scale_x.isChecked && cb_scale_y.isChecked) {
            getMatrixTemp().postScale(scaleX, scaleY)
        }


        //倾斜
        var skewX = et_skew_x.text.toString().toFloat()
        var skewY = et_skew_y.text.toString().toFloat()
        if (cb_skew_x.isChecked && cb_skew_y.isChecked) {
            getMatrixTemp().postSkew(skewX, skewY)
        }

        //移动
        var transX = et_trans_x.text.toString().toFloat()
        var transY = et_trans_y.text.toString().toFloat()
        if (cb_trans_x.isChecked && cb_trans_y.isChecked) {
            getMatrixTemp().postTranslate(transX, transY)
        }

        printLog("post", getMatrixTemp());
    }

    fun printLog(tag: String, matrix: Matrix) {
        tv_info.text = tv_info.text.toString() + "\n" + tag + ":" + getInfo(matrix)
        scrollView.smoothScrollTo(0, tv_info.height)
        iv_img.invalidate()
    }

    fun getInfo(matrix: Matrix): String? {
        var values = FloatArray(9)
        matrix.getValues(values)
        return ("sX: " + values[Matrix.MSCALE_X] + " ; "
                + "sY: " + values[Matrix.MSCALE_Y] + " ; "
                + "rX: " + values[Matrix.MSKEW_X] + " ; "
                + "rY: " + values[Matrix.MSKEW_Y] + " ; "
                + "tX: " + values[Matrix.MTRANS_X] + " ; "
                + "tY: " + values[Matrix.MTRANS_Y] + " ; ")
    }

    fun selectImg(view: View?) {

        ImageSelector.with(this).options(SelectImgOptions()).execute()
        FileSelector.with(this).option(FileSelectOptions().setLimitNum(1).setShowThumb(true).setSelectType(",jpg,").setSearchStyle(FileSelectOptions.STYLE_ONLY_FILE)).execute(1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            val stringArrayExtra = data.getStringArrayListExtra(FileSelectActivity.RESULT_DATA);
            if (stringArrayExtra != null) {
                ImageLoader.with(this).load(stringArrayExtra[0]).into(iv_img)
//                Glide.with(this).load(R.mipmap.ic_launcher).into((findViewById<View>(R.id.zoom_img) as ImageView))

            }
        }
    }

}