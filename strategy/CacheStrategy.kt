import kotlinx.coroutines.flow.Flow
import ru.grom.domain.utils.caching.Cache
import ru.grom.domain.utils.caching.stringCacheKey

/**
 * A basic abstract class for implementing a caching strategy
 *
 * @param key the caching key in the format [Cache.Key], for example [stringCacheKey]
 * @param cachedAfterLoad need to save the new values to the cache after receiving the result
 *
 * @see Cache.Key
 */
abstract class CacheStrategy <T> (
    protected val key: Cache.Key<T>,
    protected val cachedAfterLoad : Boolean
) {
    abstract suspend fun execute(currentFlow: Flow<T>): Flow<T>
}
