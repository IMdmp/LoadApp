package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val backgroundColor: Int = ResourcesCompat.getColor(resources, R.color.purple, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.darkBlue, null)
    private val rectangleProgressColor = ResourcesCompat.getColor(resources,R.color.colorAccent,null)
    var currentSweepAngle =0f
    private var widthSize = 0
    private var heightSize = 0
    private var rectF :RectF
    var rectangleProgress = RectF()

    private val LOADING ="We are loading"
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    init{
        isClickable = true
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

    private val rectanglePaint = Paint().apply{
        color = rectangleProgressColor


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
        rectangleProgress = RectF(0f,0f,1f,heightSize.toFloat())
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawBitmap(extraBitmap,0f,0f,null)
        canvas.drawColor(backgroundColor)
        var currText = ""
        var showRect = false
        var showCircle = false
        when(buttonState){
            ButtonState.Clicked->{
                showRect  = true
                showCircle = true
            }
            ButtonState.Loading -> {
                currText = LOADING
                showRect  = true
                showCircle = true
            }
            ButtonState.Completed->{
                showRect  = false
                showCircle = false
                currText = "Download"
            }
        }

        val yPos =
            (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()


        val bounds = getTextBounds(currText, textPaint)
        if(showRect){
            canvas.drawRect(rectangleProgress,rectanglePaint)
        }

        canvas.save()
        canvas.translate(widthSize/2f+(bounds.right.toFloat()/2) + circleRadius,heightSize/2f -circleRadius)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        if(showCircle){
            canvas.drawArc(rectF,0f,currentSweepAngle,true,circlePaint)
        }
        canvas.restore()
        canvas.drawText(currText, widthSize/2f, yPos.toFloat(), textPaint)

    }

    fun startAnimation(){
        val cicleMultiplier = 3.6f
        val rectMultiplier = widthSize.toFloat()/100
        buttonState = ButtonState.Clicked

        ValueAnimator.ofFloat(0f,100f).apply {
            duration = 2000
            interpolator = LinearOutSlowInInterpolator()
            addUpdateListener {valueAnimator->
                rectangleProgress.right= valueAnimator.animatedValue as Float * rectMultiplier
                currentSweepAngle = valueAnimator.animatedValue as Float *cicleMultiplier
                buttonState = ButtonState.Loading
                invalidate()
            }
            addListener({
                buttonState = ButtonState.Completed
                invalidate()
            })
        }?.start()

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

    fun startRectangleAnimation(){
        ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            duration = 650
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                rectangleProgress.right= valueAnimator.animatedValue as Float
                invalidate()
            }
        }?.start()
    }
    override fun performClick(): Boolean {
//        if(super.performClick()) return true


        Log.d("TAG","perform click")
        buttonState = ButtonState.Clicked
        startAnimation()

        invalidate()
        super.performClick()
        return true
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