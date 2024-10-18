package app.exam.imageloading.cache

import android.content.Context
import android.graphics.Bitmap

class CacheRepository(val context: Context, val cacheSize: Int): ImageCache {
    override fun put(url: String, bitmap: Bitmap) {
        TODO("Not yet implemented")
    }

    override fun get(url: String): Bitmap? {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}