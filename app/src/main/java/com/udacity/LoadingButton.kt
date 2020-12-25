package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val backgroundColor: Int = ResourcesCompat.getColor(resources, R.color.purple, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.darkBlue, null)

    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var fontSize = 0f
    private var circleRadius = 0f
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    // Set up the paint with which to draw.
    private val textPaint = Paint().apply {
        color = drawColor
        textSize = fontSize
        textAlign =Paint.Align.CENTER
    }

    private val circlePaint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        textSize = fontSize
    }


    init {
        val defFontSize = context.resources.getDimension(R.dimen.default_text_size);

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            fontSize = getDimension(R.styleable.LoadingButton_textSize, defFontSize)
            circleRadius = getDimension(R.styleable.LoadingButton_circleRadius, defFontSize)
            Log.d("TEST", "fontsize : $fontSize")

        }

        textPaint.textSize = fontSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)


    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawBitmap(extraBitmap,0f,0f,null)
        canvas.drawColor(backgroundColor)

        val yPos =
            (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()

        canvas.drawText("sample text", widthSize/2f, yPos.toFloat(), textPaint)

        val bounds = getTextBounds("sample text", textPaint)
        Log.d("TEST", "bounds: width: ${bounds}")
        canvas.drawCircle(width/2f + bounds.width(),height/2f, circleRadius, circlePaint)
    }

    fun getTextBounds(text: String, paint: Paint): Rect {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}