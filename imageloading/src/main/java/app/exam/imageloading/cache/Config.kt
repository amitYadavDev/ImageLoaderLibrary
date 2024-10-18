package app.exam.imageloading.cache

class Config {
    companion object {
        val maxMemory = Runtime.getRuntime().maxMemory() /1024
        val cacheSize = (maxMemory/8).toInt()
        val defaultCacheSize = (maxMemory/4).toInt()
    }
}