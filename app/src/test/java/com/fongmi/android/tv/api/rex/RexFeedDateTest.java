package com.fongmi.android.tv.api.rex;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class RexFeedDateTest {

    private static long epoch(int year, int month, int day, int hourUtc, int minuteUtc) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(year, month - 1, day, hourUtc, minuteUtc, 0);
        return calendar.getTimeInMillis();
    }

    @Test
    public void compactUsesAsiaShanghai() {
        // 北京时间 2026-10-18 01:00
        assertEquals("20261018", RexFeedDate.compact(epoch(2026, 10, 17, 17, 0)));
    }

    @Test
    public void compactKeepsSameDayBeforeMidnight() {
        // 北京时间 2026-10-17 23:59
        assertEquals("20261017", RexFeedDate.compact(epoch(2026, 10, 17, 15, 59)));
    }

    @Test
    public void recentFromOrdersDescendingDays() {
        List<String> dates = RexFeedDate.recentFrom(epoch(2026, 10, 18, 4, 0), 3);
        assertEquals(3, dates.size());
        assertEquals("20261018", dates.get(0));
        assertEquals("20261017", dates.get(1));
        assertEquals("20261016", dates.get(2));
    }

    @Test
    public void recentIsCappedAtMaxBacktrackDays() {
        assertEquals(7, RexFeedDate.recentFrom(epoch(2026, 10, 18, 4, 0), 30).size());
    }
}
