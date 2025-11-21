package com.nickgirga.nextcloudmapsgeofavorites.utils;

import java.util.ArrayList;
import java.util.List;

import com.nickgirga.nextcloudmapsgeofavorites.model.Geofavorite;

public class GeofavoritesFilter {
    List<Geofavorite> items;
    public GeofavoritesFilter(List<Geofavorite> items) {
        this.items = items;
    }

    public List<Geofavorite> byText(String text) {
        List<Geofavorite> filteredGeofavorites = new ArrayList<>();

        if (text.isEmpty()) {
            return items;
        } else {
            for (Geofavorite geofavorite : items) {
                String query = text.toLowerCase();
                if (geofavorite.getName() != null && geofavorite.getName().toLowerCase().contains(query)) {
                    filteredGeofavorites.add(geofavorite);
                } else if (geofavorite.getComment() != null && geofavorite.getComment().toLowerCase().contains(query)) {
                    filteredGeofavorites.add(geofavorite);
                }
            }
        }
        return filteredGeofavorites;
    }

    public List<Geofavorite> byCategory(String category) {
        List<Geofavorite> filteredGeofavorites = new ArrayList<>();

        if (category == null) {
            return items;
        } else {
            for (Geofavorite geofavorite : items) {
                if (geofavorite.getCategory().equals(category)) {
                    filteredGeofavorites.add(geofavorite);
                }
            }
        }
        return filteredGeofavorites;

    }
}
