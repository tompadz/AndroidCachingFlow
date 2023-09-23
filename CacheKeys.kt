import android.os.Parcelable

/**
 * The caching key for the [String] type.
 *
 * @param name key name
 * @see Cache.StringKey
 */
fun stringCacheKey(name: String): Cache.StringKey = Cache.StringKey(name)

/**
 * The caching key for the [Int] type.
 *
 * @param name key name
 * @see Cache.IntegerKey
 */
fun integerCacheKey(name: String): Cache.IntegerKey = Cache.IntegerKey(name)

/**
 * The caching key for the [Parcelable] type.
 *
 * @param name key name
 * @param clazz class inherited from Parcelable
 * @see Cache.ParcelableKey
 */
fun <T : Parcelable> parcelizeCacheKey(name: String, clazz : Class<T>): Cache.ParcelableKey<T> = Cache.ParcelableKey(name, clazz)
