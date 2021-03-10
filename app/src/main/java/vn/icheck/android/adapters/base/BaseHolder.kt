package vn.icheck.android.adapters.base

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder(val view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun getView(@LayoutRes id: Int, parent: ViewGroup): View {
            return LayoutInflater.from(parent.context)
                    .inflate(id, parent, false)
        }
    }
    val longClickDuration = 3000L
    var isLongPress = false

    fun getTv(@IdRes id: Int): TextView {
        return view.findViewById(id)
    }

    fun hideView(@IdRes id: Int) {
        view.findViewById<View>(id).visibility = View.GONE
    }

    fun hideView(@IdRes id: Int, @IdRes id1: Int, @IdRes id2: Int?, @IdRes id3: Int?) {
        view.findViewById<View>(id).visibility = View.GONE
        view.findViewById<View>(id1).visibility = View.GONE

        if (id2 != null) {
            view.findViewById<View>(id2).visibility = View.GONE
        }

        if (id3 != null){
            view.findViewById<View>(id3).visibility = View.GONE
        }
    }

    fun hideViewInvisible(@IdRes id: Int, @IdRes id1: Int, @IdRes id2: Int?, @IdRes id3: Int?) {
        view.findViewById<View>(id).visibility = View.INVISIBLE
        view.findViewById<View>(id1).visibility = View.INVISIBLE

        if (id2 != null) {
            view.findViewById<View>(id2).visibility = View.INVISIBLE
        }

        if (id3 != null){
            view.findViewById<View>(id3).visibility = View.INVISIBLE
        }
    }

    fun inviView(@IdRes id: Int) {
        view.findViewById<View>(id).visibility = View.INVISIBLE
    }

    fun setOnClick(@IdRes id: Int, onClickListener: View.OnClickListener) {
        view.findViewById<View>(id).setOnClickListener(onClickListener)
    }

    fun setOnClick(child: View, onClickListener: View.OnClickListener) {
        child.setOnClickListener(onClickListener)
    }

    fun setOnLongLick(@IdRes id: Int, onLongPress:() -> Unit) {
        view.findViewById<View>(id).setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                isLongPress = true
                Handler().postDelayed(Runnable {
                    if (isLongPress) {
                        onLongPress()
                    }
                }, longClickDuration)
            }else if (event.action == MotionEvent.ACTION_UP) {
                isLongPress = false
            }else if (event.action == MotionEvent.ACTION_MOVE) {
                isLongPress = false
            }
            return@setOnTouchListener true
        }
    }

    fun setOnLongLick(@IdRes id: Int, onLongClickListener: View.OnLongClickListener) {
        view.findViewById<View>(id).setOnLongClickListener(onLongClickListener)
    }
    fun showView(@IdRes id: Int) {
        view.findViewById<View>(id).visibility = View.VISIBLE
    }

    fun showView(@IdRes id: Int, @IdRes id1: Int) {
        view.findViewById<View>(id).visibility = View.VISIBLE
        view.findViewById<View>(id1).visibility = View.VISIBLE
    }

    fun getVg(@IdRes id: Int): ViewGroup {
        return view.findViewById(id)
    }

    fun getEdt(@IdRes id: Int): EditText {
        return view.findViewById(id)
    }

    fun getRcv(@IdRes id: Int): RecyclerView {
        return view.findViewById(id)
    }

    fun getImg(@IdRes id: Int): ImageView {
        return view.findViewById(id)
    }

    fun getProgressBar(@IdRes id: Int): ProgressBar {
        return view.findViewById(id)
    }
}