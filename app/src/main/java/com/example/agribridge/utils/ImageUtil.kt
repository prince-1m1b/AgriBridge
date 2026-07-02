package com.example.agribridge.utils

import com.example.agribridge.R
import kotlin.random.Random

object ImageUtil {

    private val imageList = listOf(
        R.drawable.imag1,
        R.drawable.imag2,
        R.drawable.imag3,
        R.drawable.imag4,
        R.drawable.imag5,
        R.drawable.imag6,
        R.drawable.imag7,
        R.drawable.imag8,
        R.drawable.imag9,
        R.drawable.imag10,
        R.drawable.imag11,
        R.drawable.imag12,
        R.drawable.imag13,
        R.drawable.imag14,
        R.drawable.imag15,
        R.drawable.imag16,
        R.drawable.imag17
    )

    fun getRandomImage(): Int {
        return imageList[Random.nextInt(imageList.size)]
    }
}
