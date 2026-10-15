package com.gardenconnect.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaginationUtil Unit Tests")
class PaginationUtilTest {

    @Test
    @DisplayName("Calculate total pages correctly for various item totals and page sizes")
    void testTotalPagesCalculation() {
        PaginationUtil<String> page1 = new PaginationUtil<>(Arrays.asList("A", "B"), 1, 10, 25);
        assertEquals(3, page1.getTotalPages(), "25 items with page size 10 should yield 3 pages");

        PaginationUtil<String> page2 = new PaginationUtil<>(Collections.emptyList(), 1, 10, 30);
        assertEquals(3, page2.getTotalPages(), "30 items with page size 10 should yield exactly 3 pages");

        PaginationUtil<String> page3 = new PaginationUtil<>(Collections.emptyList(), 1, 10, 0);
        assertEquals(0, page3.getTotalPages(), "0 items should yield 0 pages");
    }

    @Test
    @DisplayName("Calculate previous and next page bounds")
    void testPreviousAndNextPageBounds() {
        PaginationUtil<Integer> pageMid = new PaginationUtil<>(Arrays.asList(1, 2, 3), 2, 5, 15);
        assertTrue(pageMid.hasPrevious());
        assertTrue(pageMid.hasPreviousPage());
        assertTrue(pageMid.hasNext());
        assertTrue(pageMid.hasNextPage());
        assertEquals(1, pageMid.getPreviousPage());
        assertEquals(3, pageMid.getNextPage());

        PaginationUtil<Integer> pageFirst = new PaginationUtil<>(Arrays.asList(1, 2, 3), 1, 5, 15);
        assertFalse(pageFirst.hasPrevious());
        assertFalse(pageFirst.hasPreviousPage());
        assertTrue(pageFirst.hasNext());
        assertEquals(1, pageFirst.getPreviousPage());

        PaginationUtil<Integer> pageLast = new PaginationUtil<>(Arrays.asList(1, 2, 3), 3, 5, 15);
        assertTrue(pageLast.hasPrevious());
        assertFalse(pageLast.hasNext());
        assertFalse(pageLast.hasNextPage());
        assertEquals(3, pageLast.getNextPage());
    }

    @Test
    @DisplayName("Calculate SQL OFFSET safely")
    void testCalculateOffset() {
        assertEquals(0, PaginationUtil.calculateOffset(1, 10));
        assertEquals(10, PaginationUtil.calculateOffset(2, 10));
        assertEquals(20, PaginationUtil.calculateOffset(3, 10));
        assertEquals(0, PaginationUtil.calculateOffset(0, 10)); // Non-positive page defaults safely to page 1
        assertEquals(0, PaginationUtil.calculateOffset(-5, 10));
    }

    @Test
    @DisplayName("Generate navigation page window")
    void testNavigationPages() {
        PaginationUtil<String> page = new PaginationUtil<>(Collections.emptyList(), 5, 10, 100);
        List<Integer> nav = page.getNavigationPages();
        assertNotNull(nav);
        assertTrue(nav.contains(5));
        assertTrue(nav.contains(4));
        assertTrue(nav.contains(6));
    }
}
