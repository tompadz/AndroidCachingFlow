import kotlinx.coroutines.flow.Flow
import ru.grom.domain.utils.caching.strategy.CacheStrategyGetIfCacheAvailable
import ru.grom.domain.utils.caching.strategy.CacheStrategyGetOnly
import ru.grom.domain.utils.caching.strategy.CacheStrategyType


/**
 * An extension for the [Flow] class that implements
 * caching methods.
 *
 * When the [Flow] method starts, it checks the availability of the cache
 * by its key type [Cache.Key] and implements the specified behavior
 * in [CacheStrategyType]. As a result of execution, an object of type [T] will be returned
 * from the cache.
 *
 * @param key the caching key in the format [Cache.Key], for example [stringCacheKey]
 * @param type caching execution strategy.
 * @param cachedAfterLoad need to save the new values to the cache after receiving the result
 *
 * @see Cache
 * @see Cache.Key
 * @see CacheStrategyType
 *
 * @throws RuntimeException may return an error when using the [CacheStrategyType.ONLY] strategy,
 * if you try to get a cache that was not saved earlier
 */
suspend fun <T> Flow<T>.cache(
    key: Cache.Key<T>,
    type: CacheStrategyType = CacheStrategyType.IF_HAVE,
    cachedAfterLoad: Boolean = true
): Flow<T> {
    return when (type) {
        CacheStrategyType.IF_HAVE -> CacheStrategyGetIfCacheAvailable(key, cachedAfterLoad).execute(this)
        CacheStrategyType.ONLY -> CacheStrategyGetOnly(key, cachedAfterLoad).execute(this)
    }
}
