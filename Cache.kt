import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import androidx.core.content.edit
import com.google.gson.Gson
import kotlin.reflect.KClass

private const val PREFS_NAME = "Cache"
private const val NULL_INT_VALUE = -33805

/**
 * Singleton class for working with query caching using [cache] flow.
 *
 * The cache works by key/value type and stores the result in [SharedPreferences].
 * Each key has its own type to determine the caching strategy,
 * all keys must be inherited from [Cache.Key], for example, how it is done
 * with [stringCacheKey].
 *
 * The class contains 2 public methods - [initialize] and [clearAllKeys].
 *
 * - [initialize] is needed to initialize the class and takes [Context] in arguments
 * to create an instance of [SharedPreferences];
 * - [clearAllKeys] is needed to clear the cache inside all keys.
 *
 * It is important to initialize it before using caching
 *
 * ```
 * class App: Application() {
 *   override fun onCreate() {
 *      super.onCreate()
 *      Cache.initialize(this)
 *   }
 * }
 * ```
 *
 * @see cache
 * @see stringCacheKey
 * @see Cache.Key
 */
object Cache {

    private lateinit var sharedPreferences : SharedPreferences

    /**
     * Initializes the class and creates an instance of [SharedPreferences]
     * @param context Application context
     */
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Causes clearing of all keys, all stored key data
     */
    fun clearAllKeys() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Returns [T] from the cache, calls the [Key.getFromPrefs] method
     * to get the result, it can return null.
     *
     * @param key the caching key in the format [Cache.Key], for example [stringCacheKey]
     * @param T is the type that should be returned from caching
     * @return object of type [T] from cache
     */
    internal fun <T: Any?> getFromCache(key : Key<T>): T? {
        return key.getFromPrefs()
    }

    /**
     * Saves [item] to the cache.
     *
     * @param T type [item]
     * @param key the caching key in the format [Cache.Key], for example [stringCacheKey]
     * @param item an object of type [T] to be saved to the cache
     */
    internal fun <T> saveToCache(key : Key<T>, item: T) {
        key.saveToPrefs(item)
    }

    /**
     * An abstract key class that contains basic methods for
     * working with cache keys.
     *
     * Used to create the key behavior in certain situations, such as saving or receiving
     * data from the cache.
     *
     * All keys must be inherited from this class, for example [stringCacheKey].
     * The class contains several abstract functions.
     * - [Key.isTypeOf]
     * - [Key.getFromPrefs]
     * - [Key.saveToPrefs]
     *
     * All these functions must be implemented in the successor class
     *
     * @see stringCacheKey
     */
    abstract class Key<T>(val name: String) {

        /**
         * A method that compares the types of the object being sent from the cache.
         *
         * ```
         * fun isTypeOf(valueClass: KClass<*>): Boolean = valueClass == String::class
         * ```
         *
         * @param valueClass a class of type [KClass] to be compared with the type from the key
         */
        abstract fun isTypeOf(valueClass: KClass<*>): Boolean

        /**
         * A method that implements saving [item] of type [T] to the cache.
         *
         * ```
         * fun saveToPrefs(item : String) = sharedPreferences.edit {putString(name, item)}
         * ```
         *
         * @param item is an object of type [T] that needs to be saved to the cache.
         */
        abstract fun saveToPrefs(item: T)

        /**
         * Method for getting item[T] from cache
         *
         * ```
         * fun getFromPrefs() : String? = sharedPreferences.getString(name, null)
         * ```
         *
         * @return object of type [T] from cache
         */
        abstract fun getFromPrefs(): T?
    }

    /**
     * A class for saving an item of type [String] to the cache.
     *
     * @param name key name
     * @see Cache.Key
     * @see stringCacheKey
     */
    class StringKey(name: String) : Key<String>(name) {
        override fun isTypeOf(valueClass: KClass<*>): Boolean = valueClass == String::class
        override fun saveToPrefs(item : String) = sharedPreferences.edit { putString(name, item) }
        override fun getFromPrefs() : String? = sharedPreferences.getString(name, null)
    }

    /**
     * A class for saving an item of type [Int] to the cache.
     *
     * @param name key name
     * @see Cache.Key
     * @see integerCacheKey
     */
    class IntegerKey(name: String): Key<Int>(name) {
        override fun isTypeOf(valueClass: KClass<*>): Boolean = valueClass == Int::class
        override fun getFromPrefs(): Int? = sharedPreferences.getInt(name, NULL_INT_VALUE).takeIf { it != NULL_INT_VALUE }
        override fun saveToPrefs(item: Int) = sharedPreferences.edit { putInt(name, item) }
    }

    /**
     * A class for saving an item of type [Parcelable] to the cache.
     *
     * @param name key name
     * @param clazz class inherited from [Parcelable]
     * @see Cache.Key
     * @see parcelizeCacheKey
     */
    class ParcelableKey<T : Parcelable>(name: String, private val clazz : Class<T>) : Key<T>(name) {
        override fun isTypeOf(valueClass: KClass<*>): Boolean = valueClass == Parcelable::class
        override fun saveToPrefs(item : T) = sharedPreferences.edit { putString(name, Gson().toJson(item)) }
        override fun getFromPrefs() : T? {
            val json = sharedPreferences.getString(name, null) ?: return null
            return Gson().fromJson(json, clazz)
        }
    }
}
