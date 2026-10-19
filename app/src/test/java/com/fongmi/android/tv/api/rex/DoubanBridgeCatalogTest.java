package com.fongmi.android.tv.api.rex;

import com.fongmi.android.tv.bean.Vod;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DoubanBridgeCatalogTest {

    @Test
    public void parsesTmdbEntriesAndRebuildsImageUrls() {
        String json = "{\"items\":[{\"doubanId\":\"35010610\",\"tmdbId\":1122573,\"title\":\"挽救计划\",\"mediaType\":\"movie\","
                + "\"year\":2026,\"rating\":8.6,\"genres\":[\"剧情\",\"科幻\",\"惊悚\"],"
                + "\"images\":{\"poster\":\"https://image.tmdb.org/t/p/original/abc.jpg\",\"background\":\"https://image.tmdb.org/t/p/w500/bg.jpg\"}}]}";
        List<Vod> items = DoubanBridgeClient.parseCatalog(json);
        assertEquals(1, items.size());
        Vod item = items.get(0);
        assertEquals("tmdb:movie:1122573", item.getId());
        assertEquals("挽救计划", item.getName());
        assertEquals("2026", item.getYear());
        assertEquals("8.6分", item.getRemarks());
        assertEquals("剧情、科幻", item.getGenreText());
        assertTrue(item.getPic().endsWith("/t/p/w342/abc.jpg"));
        assertTrue(item.getBackdrop().endsWith("/t/p/w780/bg.jpg"));
    }

    @Test
    public void fallsBackToDoubanIdWithoutTmdb() {
        String json = "{\"items\":[{\"doubanId\":\"123\",\"title\":\"某剧\",\"mediaType\":\"tv\","
                + "\"images\":{\"poster\":\"https://img9.doubanio.com/view/photo/x.jpg\"}}]}";
        List<Vod> items = DoubanBridgeClient.parseCatalog(json);
        assertEquals(1, items.size());
        assertEquals("douban:123", items.get(0).getId());
        assertEquals("https://img9.doubanio.com/view/photo/x.jpg", items.get(0).getPic());
    }

    @Test
    public void skipsEntriesWithoutIdOrTitle() {
        assertTrue(DoubanBridgeClient.parseCatalog("{\"items\":[{\"title\":\"无ID\"},{\"doubanId\":\"9\"}]}").isEmpty());
        assertTrue(DoubanBridgeClient.parseCatalog("not json").isEmpty());
        assertTrue(DoubanBridgeClient.parseCatalog(null).isEmpty());
    }

    @Test
    public void buildsHeadersOnlyWhenPresent() {
        Map<String, String> headers = DoubanBridgeClient.headers("sk_abc", "uid1");
        assertEquals("Bearer sk_abc", headers.get("Authorization"));
        assertEquals("uid1", headers.get("X-User-Id"));
        assertTrue(DoubanBridgeClient.headers("", "").isEmpty());
        assertTrue(DoubanBridgeClient.headers(null, null).isEmpty());
    }

    @Test
    public void normalizesBase() {
        assertEquals(DoubanBridgeClient.DEFAULT_BASE, DoubanBridgeClient.normalizeBase(null));
        assertEquals(DoubanBridgeClient.DEFAULT_BASE, DoubanBridgeClient.normalizeBase("https://douban-bridge.baran.wang/"));
        assertEquals("https://my.example.com", DoubanBridgeClient.normalizeBase("my.example.com"));
    }

    @Test
    public void buildsCatalogUrlAndItemsBody() {
        assertEquals("https://x/v1/catalog/movie_hot_gaia?skip=20",
                DoubanBridgeClient.catalogUrl("https://x", "movie_hot_gaia", 20));
        assertEquals("{\"ids\":[\"1\",\"2\"]}", DoubanBridgeClient.itemsBody(List.of("1", " 2 ")).toString());
    }

    @Test
    public void stripsOnlyTmdbImagePrefix() {
        assertEquals("/abc.jpg", DoubanBridgeClient.tmdbPath("https://image.tmdb.org/t/p/original/abc.jpg"));
        assertEquals("/abc.jpg", DoubanBridgeClient.tmdbPath("https://image.tmdb.org/t/p/w500/abc.jpg"));
        assertEquals("", DoubanBridgeClient.tmdbPath("https://img9.doubanio.com/view/photo/x.jpg"));
        assertEquals("", DoubanBridgeClient.tmdbPath(null));
    }
}
