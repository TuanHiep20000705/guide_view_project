package com.example.comp_practice_project.ui.guide_view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.Xfermode
import android.os.Build
import android.text.Spannable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import com.example.comp_practice_project.ui.guide_view.config.DismissType
import com.example.comp_practice_project.ui.guide_view.config.Gravity
import com.example.comp_practice_project.ui.guide_view.config.PointerType
import com.example.comp_practice_project.ui.guide_view.listener.GuideListener


@SuppressLint("ViewConstructor")
class GuideView private constructor(context: Context, view: View?) :
    FrameLayout(context) {
    private val selfPaint = Paint()
    private val paintLine = Paint()
    private val paintCircle = Paint()
    private val paintCircleInner = Paint()
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val X_FER_MODE_CLEAR: Xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private val arrowPath = Path()

    private val target: View?
    private var targetRect: RectF? = null
    private val backgroundRect = Rect()

    private val density: Float
    private var stopY = 0f
    private var isTop = false
    var isShowing: Boolean = false
        private set
    private var yMessageView = 0

    private var startYLineAndCircle = 0f
    private var circleIndicatorSize = 0f
    private var circleIndicatorSizeFinal = 0f
    private var circleInnerIndicatorSize = 0f
    private var lineIndicatorWidthSize = 0f
    private var messageViewPadding = 0
    private var marginGuide = 0f
    private var strokeCircleWidth = 0f
    private var indicatorHeight = 0f

    private var isPerformedAnimationSize = false

    private var mGuideListener: GuideListener? = null
    private var mGravity: Gravity? = null
    private var dismissType: DismissType? = null
    private var pointerType: PointerType? = null
    private val mMessageView: GuideMessageView

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        this.target = view
        density = context.resources.displayMetrics.density
        init()

        mMessageView = GuideMessageView(getContext())
        mMessageView.setPadding(
            messageViewPadding,
            messageViewPadding,
            messageViewPadding,
            messageViewPadding
        )
        mMessageView.setColor(Color.WHITE)

//        addView(
//            mMessageView,
//            LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        )

        val layoutListener: OnGlobalLayoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                }

                if (target is Targetable) {
                    targetRect = (target as Targetable).boundingRect()
                } else {
                    val locationTarget = IntArray(2)
                    target!!.getLocationOnScreen(locationTarget)
                    targetRect = RectF(
                        locationTarget[0].toFloat(),
                        locationTarget[1].toFloat(),
                        (locationTarget[0] + target.width).toFloat(),
                        (locationTarget[1] + target.height).toFloat()
                    )
                    if (this@GuideView.isLandscape) {
                        targetRect?.offset(-this@GuideView.statusBarHeight.toFloat(), 0f)
                    }
                }

                backgroundRect[paddingLeft, paddingTop, width - paddingRight] =
                    height - paddingBottom
                if (this@GuideView.isLandscape) {
                    backgroundRect.offset(-this@GuideView.navigationBarSize, 0)
                } else {
                    backgroundRect.offset(0, -this@GuideView.navigationBarSize)
                }

                isTop = !((targetRect!!.top + (indicatorHeight)) > height / 2f)
                marginGuide = (if (isTop) marginGuide else -marginGuide).toInt().toFloat()
                setMessageLocation(resolveMessageViewLocation())
                startYLineAndCircle =
                    (if (isTop) targetRect!!.bottom else targetRect!!.top) + marginGuide
                stopY = yMessageView + indicatorHeight + (if (isTop) -marginGuide else marginGuide)
                startAnimationSize()
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }

    private fun startAnimationSize() {
        if (!isPerformedAnimationSize) {
            val circleSizeAnimator = ValueAnimator.ofFloat(
                0f,
                circleIndicatorSizeFinal
            )
            circleSizeAnimator.addUpdateListener { valueAnimator: ValueAnimator? ->
                circleIndicatorSize = circleSizeAnimator.animatedValue as Float
                circleInnerIndicatorSize =
                    circleSizeAnimator.animatedValue as Float - density
                postInvalidate()
            }

            val linePositionAnimator = ValueAnimator.ofFloat(
                stopY,
                startYLineAndCircle
            )
            linePositionAnimator.addUpdateListener { valueAnimator: ValueAnimator? ->
                startYLineAndCircle = linePositionAnimator.animatedValue as Float
                postInvalidate()
            }

            linePositionAnimator.setDuration(SIZE_ANIMATION_DURATION.toLong())
            linePositionAnimator.start()
            linePositionAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                }

                override fun onAnimationEnd(animator: Animator) {
                    circleSizeAnimator.setDuration(SIZE_ANIMATION_DURATION.toLong())
                    circleSizeAnimator.start()
                    isPerformedAnimationSize = true
                }

                override fun onAnimationCancel(animator: Animator) {
                }

                override fun onAnimationRepeat(animator: Animator) {
                }
            })
        }
    }

    private fun init() {
        lineIndicatorWidthSize = LINE_INDICATOR_WIDTH_SIZE * density
        marginGuide = MARGIN_INDICATOR * density
        indicatorHeight = INDICATOR_HEIGHT * density
        messageViewPadding = (MESSAGE_VIEW_PADDING * density).toInt()
        strokeCircleWidth = STROKE_CIRCLE_INDICATOR_SIZE * density
        circleIndicatorSizeFinal = CIRCLE_INDICATOR_SIZE * density
    }

    val navigationBarSize: Int
        get() {
            val resources = resources
            val id = resources.getIdentifier("navigation_bar_height_landscape", "dimen", "android")
            if (id > 0) {
                return resources.getDimensionPixelSize(id)
            }
            return 0
        }

    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    private val isLandscape: Boolean
        get() {
            val display_mode = resources.configuration.orientation
            return display_mode != Configuration.ORIENTATION_PORTRAIT
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (target != null) {
            selfPaint.color = BACKGROUND_COLOR
            selfPaint.style = Paint.Style.FILL
            selfPaint.isAntiAlias = true
            canvas.drawRect(backgroundRect, selfPaint)

            paintLine.style = Paint.Style.FILL
            paintLine.color = LINE_INDICATOR_COLOR
            paintLine.strokeWidth = lineIndicatorWidthSize
            paintLine.isAntiAlias = true

            paintCircle.style = Paint.Style.STROKE
            paintCircle.color = CIRCLE_INDICATOR_COLOR
            paintCircle.strokeCap = Paint.Cap.ROUND
            paintCircle.strokeWidth = strokeCircleWidth
            paintCircle.isAntiAlias = true

            paintCircleInner.style = Paint.Style.FILL
            paintCircleInner.color = CIRCLE_INNER_INDICATOR_COLOR
            paintCircleInner.isAntiAlias = true

            val x = (targetRect!!.left / 2 + targetRect!!.right / 2)

//            when (pointerType) {
//                PointerType.circle -> {
//                    canvas.drawLine(x, startYLineAndCircle, x, stopY, paintLine)
//                    canvas.drawCircle(x, startYLineAndCircle, circleIndicatorSize, paintCircle)
//                    canvas.drawCircle(
//                        x,
//                        startYLineAndCircle,
//                        circleInnerIndicatorSize,
//                        paintCircleInner
//                    )
//                }
//
//                PointerType.arrow -> {
//                    canvas.drawLine(x, startYLineAndCircle, x, stopY, paintLine)
//                    arrowPath.reset()
//                    if (isTop) {
//                        arrowPath.moveTo(x, startYLineAndCircle - (circleIndicatorSize * 2))
//                    } else {
//                        arrowPath.moveTo(x, startYLineAndCircle + (circleIndicatorSize * 2))
//                    }
//                    arrowPath.lineTo(x + circleIndicatorSize, startYLineAndCircle)
//                    arrowPath.lineTo(x - circleIndicatorSize, startYLineAndCircle)
//                    arrowPath.close()
//                    canvas.drawPath(arrowPath, paintCircle)
//                }
//
//                PointerType.none -> {}
//
//                else -> {}
//            }
            targetPaint.setXfermode(X_FER_MODE_CLEAR)
            targetPaint.isAntiAlias = true

            if (target is Targetable) {
                (target as Targetable).guidePath()?.let { canvas.drawPath(it, targetPaint) }
            } else {
                canvas.drawRoundRect(
                    targetRect!!,
                    RADIUS_SIZE_TARGET_RECT.toFloat(),
                    RADIUS_SIZE_TARGET_RECT.toFloat(),
                    targetPaint
                )
            }
        }
    }

    fun dismiss() {
        ((context as Activity).window.decorView as ViewGroup).removeView(
            this
        )
        isShowing = false
        if (mGuideListener != null) {
            mGuideListener!!.onDismiss(target)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        if (event.action == MotionEvent.ACTION_DOWN) {
            when (dismissType) {
                DismissType.outside -> if (!isViewContains(mMessageView, x, y)) {
                    dismiss()
                }

                DismissType.anywhere -> dismiss()
                DismissType.targetView -> if (targetRect!!.contains(x, y)) {
                    target!!.performClick()
                    dismiss()
                }

                DismissType.selfView -> if (isViewContains(mMessageView, x, y)) {
                    dismiss()
                }

                DismissType.outsideTargetAndMessage -> if (!(targetRect!!.contains(
                        x,
                        y
                    ) || isViewContains(mMessageView, x, y))
                ) {
                    dismiss()
                }

                else -> {}
            }
            return true
        }
        return false
    }

    private fun isViewContains(view: View, rx: Float, ry: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        val w = view.width
        val h = view.height

        return !(rx < x || rx > x + w || ry < y || ry > y + h)
    }

    private fun setMessageLocation(p: Point) {
        mMessageView.setX(p.x.toFloat())
        mMessageView.setY(p.y.toFloat())
        postInvalidate()
    }

    fun updateGuideViewLocation() {
        requestLayout()
    }

    private fun resolveMessageViewLocation(): Point {
        var xMessageView: Int
        xMessageView = if (mGravity == Gravity.center) {
            (targetRect!!.left - mMessageView.getWidth() / 2 + target!!.width / 2).toInt()
        } else {
            targetRect!!.right.toInt() - mMessageView.getWidth()
        }

        if (isLandscape && (xMessageView + mMessageView.getWidth()) > backgroundRect.right) {
            xMessageView -= navigationBarSize
        }

        if (xMessageView + mMessageView.getWidth() > width) {
            xMessageView = width - mMessageView.getWidth()
        }
        if (xMessageView < 0) {
            xMessageView = 0
        }

        //set message view bottom
        if ((targetRect!!.top + (indicatorHeight)) > height / 2f) {
            isTop = false
            yMessageView = (targetRect!!.top - mMessageView.getHeight() - indicatorHeight).toInt()
        } else {
            isTop = true
            yMessageView = (targetRect!!.top + target!!.height + indicatorHeight).toInt()
        }

        if (yMessageView < 0) {
            yMessageView = 0
        }

        return Point(xMessageView, yMessageView)
    }

    fun show() {
        this.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.isClickable = false
        ((context as Activity).window.decorView as ViewGroup).addView(this)
        val startAnimation = AlphaAnimation(0.0f, 1.0f)
        startAnimation.duration = APPEARING_ANIMATION_DURATION.toLong()
        startAnimation.fillAfter = true
        this.startAnimation(startAnimation)
        isShowing = true
    }

    fun setTitle(str: String?) {
        mMessageView.setTitle(str)
    }

    fun setContentText(str: String?) {
        mMessageView.setContentText(str)
    }

    fun setContentSpan(span: Spannable?) {
        mMessageView.setContentSpan(span)
    }

    fun setTitleTypeFace(typeFace: Typeface?) {
        mMessageView.setTitleTypeFace(typeFace)
    }

    fun setContentTypeFace(typeFace: Typeface?) {
        mMessageView.setContentTypeFace(typeFace)
    }

    fun setTitleTextSize(size: Int) {
        mMessageView.setTitleTextSize(size)
    }

    fun setContentTextSize(size: Int) {
        mMessageView.setContentTextSize(size)
    }

    class Builder(private val context: Context) {
        private var targetView: View? = null
        private var title: String? = null
        private var contentText: String? = null
        private var gravity: Gravity? = null
        private var dismissType: DismissType? = null
        private var pointerType: PointerType? = null
        private var contentSpan: Spannable? = null
        private var titleTypeFace: Typeface? = null
        private var contentTypeFace: Typeface? = null
        private var guideListener: GuideListener? = null
        private var titleTextSize = 0
        private var contentTextSize = 0
        private var lineIndicatorHeight = 0f
        private var lineIndicatorWidthSize = 0f
        private var circleIndicatorSize = 0f
        private var circleInnerIndicatorSize = 0f
        private var strokeCircleWidth = 0f

        fun setTargetView(view: View?): Builder {
            this.targetView = view
            return this
        }

        /**
         * gravity GuideView
         *
         * @param gravity it should be one type of Gravity enum.
         */
        fun setGravity(gravity: Gravity?): Builder {
            this.gravity = gravity
            return this
        }

        /**
         * defining a title
         *
         * @param title a title. for example: submit button.
         */
        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        /**
         * defining a description for the target view
         *
         * @param contentText a description. for example: this button can for submit your information..
         */
        fun setContentText(contentText: String?): Builder {
            this.contentText = contentText
            return this
        }

        /**
         * setting spannable type
         *
         * @param span a instance of spannable
         */
        fun setContentSpan(span: Spannable?): Builder {
            this.contentSpan = span
            return this
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         */
        fun setContentTypeFace(typeFace: Typeface?): Builder {
            this.contentTypeFace = typeFace
            return this
        }

        /**
         * adding a listener on show case view
         *
         * @param guideListener a listener for events
         */
        fun setGuideListener(guideListener: GuideListener?): Builder {
            this.guideListener = guideListener
            return this
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         */
        fun setTitleTypeFace(typeFace: Typeface?): Builder {
            this.titleTypeFace = typeFace
            return this
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        fun setContentTextSize(size: Int): Builder {
            this.contentTextSize = size
            return this
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        fun setTitleTextSize(size: Int): Builder {
            this.titleTextSize = size
            return this
        }

        /**
         * this method defining the type of dismissing function
         *
         * @param dismissType should be one type of DismissType enum. for example: outside -> Dismissing with click on outside of MessageView
         */
        fun setDismissType(dismissType: com.example.comp_practice_project.ui.guide_view.config.DismissType?): Builder {
            this.dismissType = dismissType
            return this
        }

        /**
         * changing line height indicator
         *
         * @param height you can change height indicator (Converting to Dp)
         */
        fun setIndicatorHeight(height: Float): Builder {
            this.lineIndicatorHeight = height
            return this
        }

        /**
         * changing line width indicator
         *
         * @param width you can change width indicator
         */
        fun setIndicatorWidthSize(width: Float): Builder {
            this.lineIndicatorWidthSize = width
            return this
        }

        /**
         * changing circle size indicator
         *
         * @param size you can change circle size indicator
         */
        fun setCircleIndicatorSize(size: Float): Builder {
            this.circleIndicatorSize = size
            return this
        }

        /**
         * changing inner circle size indicator
         *
         * @param size you can change inner circle indicator size
         */
        fun setCircleInnerIndicatorSize(size: Float): Builder {
            this.circleInnerIndicatorSize = size
            return this
        }

        /**
         * changing stroke circle size indicator
         *
         * @param size you can change stroke circle indicator size
         */
        fun setCircleStrokeIndicatorSize(size: Float): Builder {
            this.strokeCircleWidth = size
            return this
        }

        /**
         * this method defining the type of pointer
         *
         * @param pointerType should be one type of PointerType enum. for example: arrow -> To show arrow pointing to target view
         */
        fun setPointerType(pointerType: PointerType?): Builder {
            this.pointerType = pointerType
            return this
        }

        fun build(): GuideView {
            val guideView = GuideView(context, targetView)
            guideView.mGravity = if (gravity != null) gravity else Gravity.auto
            guideView.dismissType = if (dismissType != null) dismissType else DismissType.targetView
            guideView.pointerType = if (pointerType != null) pointerType else PointerType.circle
            val density = context.resources.displayMetrics.density

            guideView.setTitle(title)
            if (contentText != null) {
                guideView.setContentText(contentText)
            }
            if (titleTextSize != 0) {
                guideView.setTitleTextSize(titleTextSize)
            }
            if (contentTextSize != 0) {
                guideView.setContentTextSize(contentTextSize)
            }
            if (contentSpan != null) {
                guideView.setContentSpan(contentSpan)
            }
            if (titleTypeFace != null) {
                guideView.setTitleTypeFace(titleTypeFace)
            }
            if (contentTypeFace != null) {
                guideView.setContentTypeFace(contentTypeFace)
            }
            if (guideListener != null) {
                guideView.mGuideListener = guideListener
            }
            if (lineIndicatorHeight != 0f) {
                guideView.indicatorHeight = lineIndicatorHeight * density
            }
            if (lineIndicatorWidthSize != 0f) {
                guideView.lineIndicatorWidthSize = lineIndicatorWidthSize * density
            }
            if (circleIndicatorSize != 0f) {
                guideView.circleIndicatorSize = circleIndicatorSize * density
            }
            if (circleInnerIndicatorSize != 0f) {
                guideView.circleInnerIndicatorSize = circleInnerIndicatorSize * density
            }
            if (strokeCircleWidth != 0f) {
                guideView.strokeCircleWidth = strokeCircleWidth * density
            }

            return guideView
        }
    }

    companion object {
        private const val INDICATOR_HEIGHT = 40
        private const val MESSAGE_VIEW_PADDING = 5
        private const val SIZE_ANIMATION_DURATION = 700
        private const val APPEARING_ANIMATION_DURATION = 400
        private const val CIRCLE_INDICATOR_SIZE = 6
        private const val LINE_INDICATOR_WIDTH_SIZE = 3
        private const val STROKE_CIRCLE_INDICATOR_SIZE = 3
        private const val RADIUS_SIZE_TARGET_RECT = 15
        private const val MARGIN_INDICATOR = 15

        private const val BACKGROUND_COLOR = -0x67000000
        private const val CIRCLE_INNER_INDICATOR_COLOR = -0x333334
        private const val CIRCLE_INDICATOR_COLOR = Color.WHITE
        private const val LINE_INDICATOR_COLOR = Color.WHITE
    }
}