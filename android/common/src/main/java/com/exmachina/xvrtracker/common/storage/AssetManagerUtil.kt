package com.exmachina.xvrtracker.common.storage

import android.content.res.AssetManager

fun AssetManager.getBytes(fileName: String): ByteArray =
    open(fileName).use { inputStream ->
        inputStream.readBytes()
    }
