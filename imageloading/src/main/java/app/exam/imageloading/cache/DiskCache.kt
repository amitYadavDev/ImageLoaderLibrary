package app.exam.imageloading.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jakewharton.disklrucache.DiskLruCache
import com.jakewharton.disklrucache.DiskLruCache.Editor
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.RuntimeException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

// It is time now to create the DiskCaching to our library to be able to
// save images on Disk even if there is no internet we can retrieve them
// any time easily.

//Save Images on Disk:

class DiskCache private constructor(val context: Context): ImageCache {

    private var cache: DiskLruCache =
        DiskLruCache.open(context.cacheDir, 1, 1, 10 * 1024 * 1024)

    override fun put(url: String, bitmap: Bitmap) {
        val key = md5(url)
        var editor: DiskLruCache.Editor? = null

        try {
            editor = cache.edit(key)
            if (editor == null) {
                return
            }

            if(writeBitmapToFile(bitmap, editor)) {
                cache.flush()
                editor.commit()
            } else {
                editor.abort()
            }
        } catch (e: IOException) {
            try {
                editor?.abort()
            } catch (ignored: IOException) {

            }
        }
    }

    override fun get(url: String): Bitmap? {
        val key = md5(url)
        val snapshot: DiskLruCache.Snapshot? = cache.get(key)

        return if(snapshot != null) {
            val inputStream: InputStream = snapshot.getInputStream(0)
            val buffIn = BufferedInputStream(inputStream, 8 * 1024)
            BitmapFactory.decodeStream(buffIn)
        } else {
            null
        }
    }

    override fun clear() {
        cache.delete()
        cache = DiskLruCache.open(context.cacheDir, 1, 1, 10 * 1024 * 1024)
    }

    private fun writeBitmapToFile(bitmap: Bitmap, editor: Editor): Boolean {
        var out: OutputStream? = null

        try {
            out = BufferedOutputStream(editor.newOutputStream(0), 8 * 1024)
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } finally {
            out?.close()
        }
    }

    fun md5(url: String): String? {
        try {
            // static getInstance method is called with hashing MD5
            val md = MessageDigest.getInstance("MD5")

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte

            val messageDigest = md.digest(url.toByteArray())

            // Convert byte array into signum representation
            val no = BigInteger(1, messageDigest)

            // Convert message digest into hex value
            var hashText = no.toString(16)

            while (hashText.length < 32) {
                hashText = "0$hashText"
            }
            return hashText
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val INSTANCE: DiskCache? = null

        @Synchronized
        fun getInstance(context: Context): DiskCache {
            return INSTANCE?.let { INSTANCE }
                ?: run {
                    DiskCache(context)
                }
        }
    }
}