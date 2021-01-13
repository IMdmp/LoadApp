package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val backgroundColor: Int = ResourcesCompat.getColor(resources, R.color.purple, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.darkBlue, null)
    var currentSweepAngle =0f
    private var widthSize = 0
    private var heightSize = 0
    private var rectF :RectF
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
        style = Paint.Style.FILL // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        textSize = fontSize
    }
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)


    init {
        val defFontSize = context.resources.getDimension(R.dimen.default_text_size);

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            fontSize = getDimension(R.styleable.LoadingButton_textSize, defFontSize)
            circleRadius = getDimension(R.styleable.LoadingButton_circleRadius, defFontSize)
            Log.d("TEST", "fontsize : $fontSize")
        }
        rectF = RectF(0f,0f,circleRadius*2f,circleRadius*2f)
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
//        canvas.drawCircle(width/2f + bounds.width(),height/2f, circleRadius, circlePaint)
        canvas.save()
        canvas.translate(widthSize/2f+(bounds.right.toFloat()/2) + circleRadius,heightSize/2f -circleRadius)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        canvas.drawArc(rectF,0f,currentSweepAngle,true,circlePaint)
        canvas.restore()
    }

    fun startCircleAnimation(){
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 650
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Float
                invalidate()
            }
        }?.start()
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