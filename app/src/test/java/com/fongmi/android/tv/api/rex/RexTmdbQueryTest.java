package com.fongmi.android.tv.api.rex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RexTmdbQueryTest {

    @Test
    public void tvSciFiMergesTo10765() {
        RexTmdbQuery.Query query = RexTmdbQuery.discover("878", RexTmdbQuery.TV, null);
        assertEquals("discover/tv", query.path);
        assertEquals("10765", query.params.get("with_genres"));
    }

    @Test
    public void movieSciFiStays878() {
        assertEquals("878", RexTmdbQuery.discover("878", RexTmdbQuery.MOVIE, null).params.get("with_genres"));
    }

    @Test
    public void movieOnlyGenreForcesMovieType() {
        RexTmdbQuery.Query query = RexTmdbQuery.discover("28", RexTmdbQuery.TV, null);
        assertEquals("discover/movie", query.path);
        assertEquals("movie", query.fallbackMediaType);
    }

    @Test
    public void tvOnlyGenreForcesTvType() {
        assertEquals("discover/tv", RexTmdbQuery.discover("10764", RexTmdbQuery.MOVIE, null).path);
    }

    @Test
    public void sortByMapsReleaseDateToMediaDateField() {
        assertEquals("primary_release_date.desc",
                RexTmdbQuery.discover(null, RexTmdbQuery.MOVIE, "release_date.desc").params.get("sort_by"));
        assertEquals("first_air_date.asc",
                RexTmdbQuery.discover(null, RexTmdbQuery.TV, "release_date.asc").params.get("sort_by"));
    }

    @Test
    public void defaultSortByIsDropped() {
        assertFalse(RexTmdbQuery.discover(null, RexTmdbQuery.MOVIE, "default").params.containsKey("sort_by"));
        assertFalse(RexTmdbQuery.discover(null, RexTmdbQuery.TV, "").params.containsKey("sort_by"));
    }

    @Test
    public void networkMovieUsesWatchProviders() {
        RexTmdbQuery.Query query = RexTmdbQuery.networks("4330", RexTmdbQuery.MOVIE, null);
        assertEquals("discover/movie", query.path);
        assertEquals("2303|2616", query.params.get("with_watch_providers"));
        assertEquals("US", query.params.get("watch_region"));
        assertFalse(query.params.containsKey("with_networks"));
    }

    @Test
    public void networkTvKeepsNetworks() {
        RexTmdbQuery.Query query = RexTmdbQuery.networks("213", RexTmdbQuery.TV, null);
        assertEquals("discover/tv", query.path);
        assertEquals("213", query.params.get("with_networks"));
        assertFalse(query.params.containsKey("watch_region"));
    }

    @Test
    public void networkMovieWithoutMappingFallsBackToTv() {
        RexTmdbQuery.Query query = RexTmdbQuery.networks("999999", RexTmdbQuery.MOVIE, null);
        assertEquals("discover/tv", query.path);
        assertEquals("999999", query.params.get("with_networks"));
    }

    @Test
    public void companiesStayMovieDiscover() {
        RexTmdbQuery.Query query = RexTmdbQuery.companies("41077", null);
        assertEquals("discover/movie", query.path);
        assertEquals("41077", query.params.get("with_companies"));
    }

    @Test
    public void fixedListPaths() {
        assertEquals("trending/all/week", RexTmdbQuery.trending("week").path);
        assertEquals("trending/all/day", RexTmdbQuery.trending("day").path);
        assertEquals("movie/now_playing", RexTmdbQuery.nowPlaying().path);
        assertEquals("tv/on_the_air", RexTmdbQuery.onTheAir().path);
        assertEquals("movie/top_rated", RexTmdbQuery.topRated(RexTmdbQuery.MOVIE).path);
        assertEquals("list/8241", RexTmdbQuery.list("8241").path);
    }
}
