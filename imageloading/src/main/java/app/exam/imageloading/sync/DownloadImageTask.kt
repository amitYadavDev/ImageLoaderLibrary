package app.exam.imageloading.sync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.widget.ImageView
import app.exam.imageloading.cache.CacheRepository
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Handler

class DownloadImageTask(
    private val url: String,
    private val imageView: ImageView,
    private val cache: CacheRepository
): DownloadTask<Bitmap?>() {

    override fun download(url: String): Bitmap? {
        var bitmap: Bitmap? = null

        try {
            val url = URL(url)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            bitmap = BitmapFactory.decodeStream(conn.inputStream)
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    private val uiHandler = android.os.Handler(Looper.getMainLooper())

    override fun call(): Bitmap? {
        val bitmap = download(url)

        bitmap?.let {
            if(imageView.tag == url) {
                updateImageView(imageView, it)
            }
            cache.put(url, it)
        }

        return bitmap
    }

    fun updateImageView(imageView: ImageView, bitmap: Bitmap) {
        uiHandler.post{
            imageView.setImageBitmap(bitmap)
        }
    }

}