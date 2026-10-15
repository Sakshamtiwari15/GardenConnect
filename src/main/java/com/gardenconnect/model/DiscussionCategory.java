package com.gardenconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain model representing a category for community discussions.
 */
public class DiscussionCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private int displayOrder;

    public DiscussionCategory() {}

    public DiscussionCategory(Integer id, String name, String slug, String description, String icon, int displayOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.icon = icon;
        this.displayOrder = displayOrder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscussionCategory that = (DiscussionCategory) o;
        return Objects.equals(id, that.id) || Objects.equals(slug, that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slug);
    }

    @Override
    public String toString() {
        return "DiscussionCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
