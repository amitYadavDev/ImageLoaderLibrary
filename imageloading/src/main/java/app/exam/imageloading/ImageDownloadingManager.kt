package app.exam.imageloading

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import app.exam.imageloading.cache.CacheRepository
import app.exam.imageloading.cache.Config
import app.exam.imageloading.sync.DownloadImageTask
import app.exam.imageloading.sync.DownloadTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ImageDownloadingManager private constructor(context: Context, cacheSize: Int){

    private val cache = CacheRepository(context, cacheSize)
    private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val mRunningDownloadList: HashMap<String, Future<Bitmap?>> = hashMapOf()


    fun displayImage(url: String, imageView: ImageView, placeholder: Int) {
        var bitmap = cache.get(url)

        bitmap?.let {
            imageView.setImageBitmap(it)
            return
        } ?: run {
            imageView.tag = url
            placeholder?.let {
                imageView.setImageResource(placeholder)
            }
            addDownloadImageTask(url, DownloadImageTask(url, imageView, cache))
        }
    }

    fun addDownloadImageTask(url: String, downloadTask: DownloadTask<Bitmap?>) {
        mRunningDownloadList.put(url, executorService.submit(downloadTask))
    }

    fun clearCache() {
        cache.clear()
    }

    fun cancelTask(url: String) {
        synchronized(this) {
            mRunningDownloadList.forEach {
                if(it.key == url && !it.value.isDone)
                    it.value.cancel(true)
            }
        }
    }

    fun cancelAll() {
        synchronized(this) {
            mRunningDownloadList.forEach {
                if(!it.value.isDone) it.value.cancel(true)
            }
            mRunningDownloadList.clear()
        }
    }

    companion object {
        private var INSTANCE: ImageDownloadingManager? = null

        fun getInstance(context: Context, cacheSize: Int = Config.defaultCacheSize): ImageDownloadingManager {
            if(INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = ImageDownloadingManager(context, cacheSize)
                }
            }
            return INSTANCE!!
        }
    }
}