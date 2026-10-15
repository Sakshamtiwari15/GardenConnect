package com.gardenconnect.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic pagination container and calculation utility.
 * Demonstrates Java Generics (class-level type parameter T) and Collections (List, ArrayList).
 *
 * @param <T> The domain entity type contained in this page.
 */
public class PaginationUtil<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<T> items;
    private final int currentPage;
    private final int pageSize;
    private final long totalItems;
    private final int totalPages;

    public PaginationUtil(List<T> items, int currentPage, int pageSize, long totalItems) {
        this.items = (items != null) ? new ArrayList<>(items) : Collections.emptyList();
        this.currentPage = Math.max(1, currentPage);
        this.pageSize = Math.max(1, pageSize);
        this.totalItems = Math.max(0, totalItems);
        this.totalPages = (int) Math.ceil((double) this.totalItems / this.pageSize);
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public boolean hasNext() {
        return currentPage < totalPages;
    }

    public boolean hasPreviousPage() {
        return hasPrevious();
    }

    public boolean hasNextPage() {
        return hasNext();
    }

    public int getPreviousPage() {
        return Math.max(1, currentPage - 1);
    }

    public int getNextPage() {
        return Math.min(totalPages, currentPage + 1);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Calculates SQL OFFSET parameter safely.
     */
    public static int calculateOffset(int page, int pageSize) {
        int validPage = Math.max(1, page);
        int validSize = Math.max(1, pageSize);
        return (validPage - 1) * validSize;
    }

    /**
     * Returns an array of page numbers to render in pagination navigation bar.
     */
    public List<Integer> getNavigationPages() {
        List<Integer> pages = new ArrayList<>();
        if (totalPages <= 1) {
            return pages;
        }

        int start = Math.max(1, currentPage - 2);
        int end = Math.min(totalPages, currentPage + 2);

        for (int i = start; i <= end; i++) {
            pages.add(i);
        }
        return pages;
    }
}
