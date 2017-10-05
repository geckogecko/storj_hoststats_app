package com.steinbacher.storj_hoststats_app.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by georg on 03.09.17.
 */

public class VersionTest {

    @Test
    public void toStringTest() {
        String testVersion = "7.0.0";

        Version version = new Version(testVersion);
        assertEquals(version.toString(), testVersion);
    }
}
