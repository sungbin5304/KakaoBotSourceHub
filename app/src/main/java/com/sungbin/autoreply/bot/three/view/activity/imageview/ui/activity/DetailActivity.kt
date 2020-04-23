package com.sungbin.autoreply.bot.three.view.activity.imageview.ui.activity

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionSet
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.ui.Glide
import com.sungbin.autoreply.bot.three.utils.ui.ImageUtils
import com.sungbin.autoreply.bot.three.view.activity.imageview.ui.transition.ChangeRoundedImageTransform
import com.sungbin.autoreply.bot.three.view.activity.imageview.ui.transition.SimpleAnimationListener
import com.sungbin.autoreply.bot.three.view.activity.imageview.ui.transition.SimpleTransitionListener
import com.sungbin.autoreply.bot.three.view.activity.imageview.ui.view.CircleSwipeLayout
import com.sungbin.autoreply.bot.three.view.activity.imageview.ui.view.RoundedImageView
import kotlinx.android.synthetic.main.activity_imageview_detail.*
import pl.droidsonroids.gif.GifDrawable
import java.io.File


class DetailActivity : AppCompatActivity() {
    private var sharedElementEnterLeft: Int = 0
    private var sharedElementEnterTop: Int = 0
    private var sharedElementEnterRadius: Int = 0
    private var isInformationUiHidden = false
    private var isFlingSwiped = false
    private var progressAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()

        pauseProgress()
    }

    override fun onStart() {
        super.onStart()

        initializeTransitionCallback()
    }

    override fun onResume() {
        super.onResume()

        resumeProgress()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        initializeSystemUi()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun initializeViews(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_imageview_detail)

        val gifPath = ImageUtils.getDownloadFilePath(intent.getStringExtra("image")!!)
        if(gifPath.contains(".gif")) {
            val gifFile = File(gifPath)
            val gifFromFile = GifDrawable(gifFile)
            image.setImageDrawable(gifFromFile)
        }
        else ImageUtils.set(gifPath, image, applicationContext)
        Glide.set(applicationContext, intent.getStringExtra("avatar")!!, imageViewUser)
        name.text = intent.getStringExtra("name")

        buttonBack.setOnClickListener {
            supportFinishAfterTransition()
        }

        swipeLayout.listener = object : CircleSwipeLayout.Listener {
            override fun onSwipeStarted() {
                pauseProgress()
            }

            override fun onSwipeCancelled() {
            }

            override fun onSwiped() {
                pauseProgress()

                swipeLayout.autoReset = false
                startExitAnimation()
            }

            override fun onSwipedFling() {
                isFlingSwiped = true
            }

            override fun onSwipeReset() {
                if (isFlingSwiped)
                    supportFinishAfterTransition()
                else
                    resumeProgress()
            }
        }
        swipeLayout.setOnClickListener {
            if (!swipeLayout.isTransitionPresent()) {
                if (!isInformationUiHidden) {
                    supportFinishAfterTransition()
                } else {
                    swipeLayout.swipeEnabled = true
                    isInformationUiHidden = false

                    showInformationUi().setListener(object : SimpleAnimationListener() {
                        override fun onAnimationEnd(animator: Animator) {
                            super.onAnimationEnd(animator)
                            resumeProgress()
                        }
                    })
                }
            }
        }
        swipeLayout.setOnLongClickListener {
            if (!swipeLayout.isTransitionPresent()) {
                swipeLayout.swipeEnabled = false
                isInformationUiHidden = true

                pauseProgress()
                hideInformationUi()
            }

            false
        }
    }

    private fun initializeTransitionCallback() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementStart(
                sharedElementNames: MutableList<String>,
                sharedElements: MutableList<View>,
                sharedElementSnapshots: MutableList<View>
            ) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots)

                for (view in sharedElements) {
                    if (view is RoundedImageView) {
                        sharedElementEnterLeft = view.left
                        sharedElementEnterTop = view.top
                        break
                    }
                }
            }
        })

        val sharedElementTransition = window.sharedElementEnterTransition
        sharedElementTransition!!.addListener(object : SimpleTransitionListener() {
            override fun onTransitionStart(p0: Transition?) {
                super.onTransitionStart(p0)

                val roundedImageTransform: ChangeRoundedImageTransform? =
                    if (p0 is TransitionSet) {
                        var roundedImageTransform: ChangeRoundedImageTransform? = null
                        for (i in 0..p0.transitionCount) {
                            val transition = p0.getTransitionAt(i)
                            if (transition is ChangeRoundedImageTransform) {
                                roundedImageTransform = transition
                                break
                            }
                        }
                        roundedImageTransform
                    } else if (p0 is ChangeRoundedImageTransform) {
                        p0
                    } else {
                        null
                    }
                sharedElementEnterRadius = roundedImageTransform?.getFromRadius() ?: sharedElementEnterRadius
            }

            override fun onTransitionEnd(p0: Transition?) {
            }
        })
    }

    private fun initializeSystemUi() {
        window!!.decorView.systemUiVisibility += (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    private fun pauseProgress() {
        progressAnimator?.pause()
    }

    private fun resumeProgress() {
        progressAnimator?.resume()
    }

    private fun showInformationUi(): ViewPropertyAnimator {
        return layoutInformation.animate().alpha(1f)

    }

    private fun hideInformationUi(): ViewPropertyAnimator {
        return layoutInformation.animate().alpha(0f)
    }

    private fun startExitAnimation() {
        swipeLayout.moveCircleToState(
            sharedElementEnterLeft,
            sharedElementEnterTop,
            sharedElementEnterRadius,
            0
        )?.addListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)

                if (!isFinishing)
                    finish()
            }
        })
    }
}