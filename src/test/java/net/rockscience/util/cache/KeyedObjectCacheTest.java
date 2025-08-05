package net.rockscience.util.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.rockscience.util.cache.KeyedObjectCache.Cacheable;
import net.rockscience.util.cache.KeyedObjectCache.CacheableRepo;
import net.rockscience.util.cache.KeyedObjectCache.HasCacheKey;

@ExtendWith(MockitoExtension.class)
public class KeyedObjectCacheTest {

	private KeyedObjectCache<String, Thing> underTest;

	@Mock
	CacheableRepo<String, Thing> repo;
	@Mock
	CacheableRepo<Integer, Thing> intRepo;
	@Mock
	CacheableRepo<ComplexKey, Thing> complexRepo;

	@BeforeEach
	public void setUp() {
		underTest = new KeyedObjectCache<String, Thing>(2, repo);
	}

	@Test
	public void testDontCache() {

		Mockito.when(repo.get("a")).thenReturn(new Thing("a", 0));
		// this object has 0 cache time
		underTest.getObject("a");
		underTest.getObject("a");

		// expect two misses
		Mockito.verify(repo, Mockito.times(2)).get("a");

	}

	@Test
	public void testCache() {
		Mockito.when(repo.get("b")).thenReturn(new Thing("b", 100000));
		// this object has a very large cache time
		underTest.getObject("b");
		underTest.getObject("b");

		// only one miss
		Mockito.verify(repo).get("b");
	}

	@Test
	public void testNegativeCache() {
		Mockito.when(repo.get("c")).thenReturn(new Thing("c", -1));
		// this object has a negative cache time, so it should never get evicted
		underTest.getObject("c");
		underTest.getObject("c");

		// only one miss
		Mockito.verify(repo).get("c");
	}

	@Test
	public void evictForSize() {
		Mockito.when(repo.get("a")).thenReturn(new Thing("a", 0));
		Mockito.when(repo.get("b")).thenReturn(new Thing("b", 100000));
		Mockito.when(repo.get("c")).thenReturn(new Thing("c", -1));
		Mockito.when(repo.get("d")).thenReturn(null);

		underTest.getObject("a");
		underTest.getObject("b");
		underTest.getObject("c");
		underTest.getObject("d");

		// cache size is 2. Ask for the eldest two again, and we should need
		// to go to the repo
		underTest.getObject("a");
		underTest.getObject("b");

		Mockito.verify(repo, Mockito.times(2)).get("a");
		Mockito.verify(repo, Mockito.times(2)).get("b");
		Mockito.verify(repo, Mockito.times(1)).get("c");
		Mockito.verify(repo, Mockito.times(1)).get("d");
	}

	@Test
	public void testNull() {
		// this one returns null
		underTest.getObject("d");
		underTest.getObject("d");
		// don't cache something if it didn't get returned from the repo
		Mockito.verify(repo, Mockito.times(2)).get("d");

	}

	@Test
	public void testIntKey() {
		KeyedObjectCache<Integer, Thing> cache = new KeyedObjectCache<Integer, Thing>(10, intRepo);

		Mockito.when(intRepo.get(Integer.valueOf(50))).thenReturn(new Thing("a", 10));
		Mockito.when(intRepo.get(Integer.valueOf(60))).thenReturn(new Thing("b", 10));

		Thing got = cache.getObject(50);
		assertNotNull(got);
		got = cache.getObject(60);
		assertNotNull(got);
		got = cache.getObject(50);
		assertNotNull(got);

		// should only pass through to the repo twice
		Mockito.verify(intRepo, Mockito.times(2)).get(Mockito.anyInt());
	}

	@Test
	public void testComplexKey() {
		KeyedObjectCache<ComplexKey, Thing> cache = new KeyedObjectCache<ComplexKey, Thing>(10, complexRepo);

		ComplexKey k1 = new ComplexKey("a", "b");
		ComplexKey k2 = new ComplexKey("y", "z");

		Thing t1 = new Thing("AlphaBeta", 20);
		Thing t2 = new Thing("YelloZebra", 20);

		Mockito.when(complexRepo.get(k1)).thenReturn(t1);
		Mockito.when(complexRepo.get(k2)).thenReturn(t2);

		Thing got = cache.getObject(k1);
		assertNotNull(got);
		got = cache.getObject(k2);
		assertNotNull(got);
		got = cache.getObject(k1);
		assertNotNull(got);
		got = cache.getObject(k2);
		assertNotNull(got);
		got = cache.getObject(k2);
		assertNotNull(got);
		got = cache.getObject(k1);
		assertNotNull(got);

		// should only pass through to the repo twice
		Mockito.verify(complexRepo, Mockito.times(2)).get(Mockito.any(ComplexKey.class));
	}



		private static class Thing implements Cacheable {
		public Thing(String v, int s) {
			val = v;
			cs = s;
		}

		private String val;
		private int cs;

		@Override
		public int getCacheSeconds() {
			return cs;
		}
	}

	private static class ComplexKey implements HasCacheKey<String> {
		private String part1;
		private String part2;

		public ComplexKey(String s1, String s2) {
			part1 = s1;
			part2 = s2;
		}

		@Override
		public String getCacheKey() {
			return String.format("%s-%s", part1, part2);
		}
	}
}
