package org.sherman.geo.server.service;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import com.github.davidmoten.grumpy.core.Position;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoAnnotatedContextLoader;
import org.mockito.Mockito;
import org.sherman.geo.common.domain.IndexedUserLabel;
import org.sherman.geo.common.domain.UserLabel;
import org.sherman.geo.server.storage.GeoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Denis Gabaydulin
 * @since 11.09.16
 */
@ContextConfiguration(
        loader = SpringockitoAnnotatedContextLoader.class,
        classes = {
                GeoServiceImpl.class
        })
public class GeoServiceTest extends AbstractTestNGSpringContextTests {
    private static final Logger log = LoggerFactory.getLogger(GeoServiceTest.class);

    @Autowired
    private GeoService geoService;

    @Autowired
    @ReplaceWithMock
    private GeoStorage geoStorage;

    @Test
    public void noUser() {
        when(geoStorage.getByUser(eq(42L))).thenReturn(empty());

        assertFalse(geoService.isUserNearLabel(42L, new LatLong(42d, 42d)));
    }

    @Test
    public void nearby() {
        when(geoStorage.getByUser(eq(42L))).thenReturn(Optional.of(create(42L, new LatLong(42d, 42d))));
        when(geoStorage.getDistanceError(eq("szmygt"))).thenReturn(Optional.of(100));

        assertTrue(geoService.isUserNearLabel(42L, new LatLong(42d, 42d)));
    }

    @Test
    public void farAway() {
        when(geoStorage.getByUser(eq(42L))).thenReturn(Optional.of(create(42L, new LatLong(42d, 42d))));
        when(geoStorage.getDistanceError(eq("szmygt"))).thenReturn(Optional.of(82500));

        assertFalse(geoService.isUserNearLabel(42L, new LatLong(42d, 41d)));
    }

    @Test
    public void checkDistanceError() {
        when(geoStorage.getByUser(eq(356865068291330092L))).thenReturn(Optional.of(create(356865068291330092L, new LatLong(55.804, 37.637))));
        when(geoStorage.getDistanceError(eq("ucfv2q"))).thenReturn(Optional.of(247));

        assertTrue(geoService.isUserNearLabel(356865068291330092L, new LatLong(55.804, 37.637)));
        assertFalse(geoService.isUserNearLabel(356865068291330092L, new LatLong(55.806, 37.639))); // ~ 255 meters
    }

    @BeforeMethod
    private void reset() {
        Mockito.reset(geoStorage);
    }

    private static IndexedUserLabel create(long userId, LatLong coords) {
        return new IndexedUserLabel(
                new UserLabel(userId, coords),
                GeoStorage.MAX_LENGTH
        );
    }
}
