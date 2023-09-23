/**
 * Available caching execution strategies
 *
 * @property IF_HAVE
 * @property ONLY
 *
 * @see CacheStrategy
 */
enum class CacheStrategyType {

    /**
     * will return the cache only if it has been saved,
     * then it will return the received data
     * @see CacheStrategyGetIfCacheAvailable
     */
    IF_HAVE,

    /**
     * Will return only the cache data,
     * if the cache has not been written, it will return
     * [RuntimeException]
     * @see CacheStrategyGetOnly
     */
    ONLY
}
