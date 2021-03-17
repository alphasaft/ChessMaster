package assets.utils

@PublishedApi
internal object CachesHandler {
    @PublishedApi
    internal val caches: MutableMap<String, Any?> = mutableMapOf()

    inline fun <R : Any> getPrivateCacheOrElse(className: String, cacheName: String, default: () -> R): R {
        require("::" !in cacheName && "$" !in cacheName)
        @Suppress("UNCHECKED_CAST")
        return caches["$className::$cacheName"] as R? ?: default().also { caches["$className::$cacheName"] = it }
    }

    fun invalidatePrivateCaches(className: String, names: Array<out String>) {
        for (name in names) {
            require("::" !in name && "$" !in name)
            if ("$className::$name" in caches) caches["$className::$name"] = null
        }
    }

    inline fun <R : Any> getPublicCacheOrElse(mainFileName: String, cacheName: String, default: () -> R): R {
        require("::" !in cacheName && "$" !in cacheName)
        @Suppress("UNCHECKED_CAST")
        return caches["$mainFileName$$cacheName"] as R? ?: default().also { caches["$mainFileName$$cacheName"] = it }
    }

    fun invalidateCaches(mainFileName: String, names: Array<out String>) {
        for (name in names) {
            require("::" !in name && "$" !in name)
            if ("$mainFileName$$name" in caches) caches["$mainFileName$$name"] = null
        }
    }
}

inline fun <R : Any> cachedUnder(name: String, provider: () -> R): R {
    return CachesHandler.getPublicCacheOrElse(mainFileName(), name, provider)
}

inline fun <R : Any> privatelyCachedUnder(name: String, provider: () -> R): R {
    return CachesHandler.getPrivateCacheOrElse(callSite().className, name, provider)
}

fun invalidateCaches(vararg names: String) {
    CachesHandler.invalidateCaches(mainFileName(), names)
}

fun invalidatePrivateCaches(vararg names: String) {
    CachesHandler.invalidatePrivateCaches(callSite().className, names)
}

inline fun <K, V> cachedUsing(cacheMap: MutableMap<K, V>, key: K, provider: () -> V): V {
    cacheMap[key]?.let { return it }
    return provider().also { cacheMap[key] = it }
}
