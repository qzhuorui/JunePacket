package com.choryan.junepacket.extensions

import android.content.res.Resources
import android.util.TypedValue

/**
 * @author: ChoRyan Quan
 * @date: 6/15/21
 */
object ExtensionView {

    val Float.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
        )
}