import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.grom.domain.utils.caching.Cache

private const val TAG = "CacheStrategyGetOnly"

/**
 * Will return only the cache data,
 * if the cache has not been written, it will return
 * [RuntimeException]
 */
class CacheStrategyGetOnly <T> (
    key: Cache.Key<T>,
    cachedAfterLoad: Boolean
): CacheStrategy<T>(key, cachedAfterLoad) {

    override suspend fun execute(currentFlow: Flow<T>): Flow<T> = flow {
        val cacheData: T? = Cache.getFromCache(key)
        cacheData?.let { emit(it) } ?: Log.w(TAG, "Cache by key \"$key\" not found")
        currentFlow.collect { value ->
            if (cachedAfterLoad) {
                Cache.saveToCache(key, value)
            }
        }
    }
}
