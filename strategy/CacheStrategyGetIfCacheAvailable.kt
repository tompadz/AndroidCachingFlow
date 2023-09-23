import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.grom.domain.utils.caching.Cache

/**
 * A strategy for obtaining caching if the cache was previously saved.
 * Returns new data from flow and stores new values in the cache if [cachedAfterLoad] == true.
 */
class CacheStrategyGetIfCacheAvailable <T> (
    key: Cache.Key<T>,
    cachedAfterLoad: Boolean
): CacheStrategy<T>(key, cachedAfterLoad) {

    override suspend fun execute(currentFlow: Flow<T>): Flow<T> = flow {
        val cacheData: T? = Cache.getFromCache(key)
        cacheData?.let { emit(it) }
        currentFlow.collect { value ->
            emit(value)
            if (cachedAfterLoad) {
                Cache.saveToCache(key, value)
            }
        }
    }
}
