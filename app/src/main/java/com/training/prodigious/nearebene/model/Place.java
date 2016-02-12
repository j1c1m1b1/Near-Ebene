package com.training.prodigious.nearebene.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Julio Mendoza on 2/11/16.
 */
public class Place {

    @SerializedName("place_id")
    private String id;

    private String name;

    private String icon;

    private Geometry geometry;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getLat() {
        return geometry.getLocation().getLat();
    }

    public double getLng() {
        return geometry.getLocation().getLng();
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    private static class Geometry {

        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        private static class Location {
            private double lat;

            private double lng;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }
}
