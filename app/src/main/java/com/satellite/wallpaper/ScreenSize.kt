package com.satellite.wallpaper

import android.content.Context
import android.app.ActivityManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Tamaño real de la pantalla en vertical (incluye barras del sistema): el mismo que usa el fondo animado. */
object ScreenSize {
    data class CaptureSize(val width: Int, val height: Int, val densityDpi: Int)

    private const val MAX_CAPTURE_PIXELS = 3_145_728L
    private const val LOW_RAM_MAX_CAPTURE_PIXELS = 1_572_864L
    private const val MAX_CAPTURE_SIDE = 2_560
    private const val LOW_RAM_MAX_CAPTURE_SIDE = 1_920

    fun isLowRamDevice(context: Context): Boolean {
        val activityManager = context.applicationContext
            .getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        return activityManager?.isLowRamDevice == true
    }

    fun real(context: Context): Pair<Int, Int> {
        val wm = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val w: Int
        val h: Int
        if (Build.VERSION.SDK_INT >= 30) {
            val b = wm.maximumWindowMetrics.bounds
            w = b.width(); h = b.height()
        } else {
            val m = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(m)
            w = m.widthPixels; h = m.heightPixels
        }
        return Pair(minOf(w, h), maxOf(w, h))
    }

    /**
     * Limita la resolución de capturas y bitmaps según la capacidad del teléfono.
     * Se reduce también el DPI para mantener el mismo viewport lógico de la web.
     */
    fun capture(context: Context): CaptureSize {
        val (screenWidth, screenHeight) = real(context)
        val lowRam = isLowRamDevice(context)
        val maxPixels = if (lowRam) LOW_RAM_MAX_CAPTURE_PIXELS else MAX_CAPTURE_PIXELS
        val maxSide = if (lowRam) LOW_RAM_MAX_CAPTURE_SIDE else MAX_CAPTURE_SIDE

        val width = screenWidth.coerceAtLeast(1)
        val height = screenHeight.coerceAtLeast(1)
        val areaScale = sqrt(maxPixels.toDouble() / (width.toLong() * height.toLong())).toFloat()
        val sideScale = maxSide.toFloat() / maxOf(width, height)
        val scale = minOf(1f, areaScale, sideScale)
        val densityDpi = context.resources.displayMetrics.densityDpi

        return CaptureSize(
            width = (width * scale).toInt().coerceAtLeast(1),
            height = (height * scale).toInt().coerceAtLeast(1),
            densityDpi = (densityDpi * scale).roundToInt().coerceAtLeast(120)
        )
    }

    /** Relación ancho/alto en vertical. */
    fun aspect(context: Context): Float {
        val (w, h) = real(context)
        return w.toFloat() / h.toFloat()
    }
}
