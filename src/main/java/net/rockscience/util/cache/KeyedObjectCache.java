package net.rockscience.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import net.rockscience.util.cache.KeyedObjectCache.Cacheable;

/**
 * A simple Read-Thru Cache for {@link Cacheable} objects. The caching strategy is based
 * on the cacheSeconds property of the @{link Casheable} and the number of cached items
 * is limited.  Cache Misses are read from the provided {@link CacheableRepo}
 * 
 * @author bdamiano
 * @param <K> the Key Type
 * @param <T> The cached object
 *
 */
public class KeyedObjectCache<K, T extends Cacheable> {

	 private static final Logger LOGGER = LoggerFactory.getLogger(KeyedObjectCache.class);

	private final CacheableRepo<K, T> repository;
	private final int maxSize;

	private Map<Object, CacheEntry<T>> cache;

	/**
	 * Construct with a reference to the repo.
	 * 
	 * @param maxSize - the max number of items to keep in the cache.  Oldest will be evicted when full
	 * @param repo the provided implementation of a {@link CacheableRepo} to read through to
	 */
	public KeyedObjectCache(int maxSize, CacheableRepo<K, T> repo) {
		repository = repo;
		this.maxSize = maxSize;

		// Create the inner cache itself. This is a LinkedHashMap which allows eviction
		// of the oldest inserted element once the max size is hit.
		cache = new LinkedHashMap<>(maxSize, 0.7f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<Object, CacheEntry<T>> eldest) {
				return size() > KeyedObjectCache.this.maxSize;
			}
		};
	}

	/**
	 * Get a {@link Cacheable} object by a key of type K. This attepmts to read from the
	 * cache. If found, it will check the expiration time since the last read. If
	 * expired, or there is a cache miss, it will automatically read through to 
	 * the provided {@link CacheableRepo<K,T>} and update the cache.
	 * 
	 * @param key - The object of type K to use as a lookup.
	 * @return
	 */
	public T getObject(K key) {
		CacheEntry<T> e = null;

		// Obtain the key to use for the cache lookup. Will either be the key object itself or
		// obtain the real key from it if the key implements HasCacheKey.
		Object realCacheKey = key instanceof HasCacheKey ? ((HasCacheKey<Object>) key).getCacheKey() : key;

		synchronized (this) {
			e = cache.get(realCacheKey);
		}

		long now = System.currentTimeMillis();

		if (null != e && e.getObject() != null) {

			// We found the item in the cache. Now check the expiration
			final long msSinceLastUpdate = now - e.getLastUpdateMs();
			final long cacheTTL = e.getObject().getCacheSeconds() * 1000;

			if (e.getObject().getCacheSeconds() < 0 || msSinceLastUpdate < cacheTTL) {
				// In the cache and still fresh enough
				return e.getObject();
			}
			else {
				// Expired, so set the object as null so we read-thru the repo to get it
				LOGGER.debug("Expired Cache Element found for {}", realCacheKey);
				e = null;
			}
		}

		// We are here either because of a cache miss or an expired hit. go to the
		// repo to get a fresh object, add it
		// to the cache and return it.
		if (e == null) {
			// try to get a Fresh Object from the repo. Use the actual key object
			T obj = repository.get(key);
			e = new CacheEntry<T>();

			if (obj != null) {
				e.setLastUpdateMs(now);
				e.setObject(obj);

				synchronized (this) {
					cache.put(realCacheKey, e);
					LOGGER.debug("Cache Put for {}", realCacheKey);
				}
			}
		}
		return e.getObject();
	}

	/**
	 * A class to store an entry in the cache. Consists of a {@link Cacheable<K>}
	 * and the time of its last refresh
	 * 
	 * @author bdamiano
	 *
	 * @param <T> The type we're caching
	 */
	@Data
	private static class CacheEntry<T> {
		private T object;
		private long lastUpdateMs;
	}

	/**
	 * An interface to The Backing repository for {@link Cacheable} objects
	 * 
	 * @author bdamiano
	 *
	 * @param K - the key type
	 * @param T - the object type
	 */
	public static interface CacheableRepo<K, T> {
		T get(K key);
	}

	/**
	 * An interface which complex key objects can implement to return a simple
	 * string key for the actual cache lookups. If the key implements this
	 * interface, the caching code will use the implementor's key in the cache map
	 * rather than the object itself.
	 * 
	 * @author bdamiano
	 *
	 */
	public static interface HasCacheKey<K> {
		K getCacheKey();
	}

	/**
 * Contract interface for something that can be put in our cache with an
 * expiration time
 * 
 * @author bdamiano
 *
 */
public static interface Cacheable {
	int getCacheSeconds();
}


}
