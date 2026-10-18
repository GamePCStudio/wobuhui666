package com.github.catvod.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class DohTest {
    @Test
    public void defaultSelectionPrefersTheShippedTencentEntry() {
        Doh tencent = new Doh().name("腾讯").url(Doh.DEFAULT_URL);
        Doh alibaba = new Doh().name("阿里").url("https://dns.alidns.com/dns-query");
        assertSame(tencent, Doh.pickDefault(Arrays.asList(alibaba, tencent)));
    }

    @Test
    public void defaultSelectionIsConstructedWhenTheListLacksTencent() {
        Doh picked = Doh.pickDefault(Collections.singletonList(new Doh().name("系统").url("")));
        assertEquals(Doh.DEFAULT_URL, picked.getUrl());
        assertEquals("Tencent", picked.getName());
    }

    @Test
    public void defaultItemWorksWithoutAnAndroidContext() {
        assertEquals(Doh.DEFAULT_URL, Doh.defaultItem(null).getUrl());
    }
}
