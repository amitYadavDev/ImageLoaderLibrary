package app.exam.imageloading.sync

import java.util.concurrent.Callable


/*
* Download Task is an abstract class that extends from Callable
* which is like Runnable but it returns Future Object because we
* need to be able to cancel certain Loading Task later so that we
* used this approach. Also, This Download Task is generic so we can
* use it to download another file not just photo if we need but
* with some modification.
* */
abstract class DownloadTask<T>: Callable<T> {
    abstract fun download(url: String): T
}