package com.example.bangkitcapstone.view.canvas

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.io.OutputStream


class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var paint: Paint? = null
    private var path: Path? = null

    init {
        paint = Paint()
        path = Path()
        paint!!.color = Color.BLACK
        paint!!.strokeWidth = 25f
        paint!!.style = Paint.Style.STROKE
        paint!!.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas){
        canvas!!.drawPath(path!!, paint!!)
    }

    override fun onTouchEvent(event: MotionEvent?) : Boolean {
        val xPos : Float = event!!.x
        val yPos : Float = event!!.y
        when(event!!.action){
            MotionEvent.ACTION_DOWN -> {
                path!!.moveTo(xPos, yPos)
            }
            MotionEvent.ACTION_MOVE-> {
                path!!.lineTo(xPos, yPos)
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        invalidate()
        return true
    }

    fun clearDrawing() {
        path!!.reset()
        invalidate()
    }

    fun saveDrawingToGallery(context: Context) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)

        val canvas = Canvas(bitmap)
        draw(canvas)

        val fileName = "Drawing_${System.currentTimeMillis()}.jpeg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val resolver = context.contentResolver
        val uri = resolver.insert(contentUri, contentValues)

        uri?.let {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.let { it1 -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it1) }
                outputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

