package org.sherman.geo.server.storage;

import org.sherman.geo.server.configuration.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Denis Gabaydulin
 * @since 11.09.16
 */
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = {
                ServerConfiguration.class,
                GeoStorageImpl.class
        })
@TestPropertySource(locations = "classpath:application.properties")
public class GeoStorageTest extends AbstractTestNGSpringContextTests {
    private static final Logger log = LoggerFactory.getLogger(GeoStorageTest.class);

    @Autowired
    private GeoStorage geoStorage;

    @Test
    public void getSizeByGeoHash() throws IOException, InterruptedException {
        assertEquals(geoStorage.getSizeByGeoHash("ucftpk"), 1778);
    }

    @Test
    public void getDistanceError() {
        assertEquals(geoStorage.getDistanceError("ucftpk").get(), Integer.valueOf(327));
    }
}
