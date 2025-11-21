package com.nickgirga.nextcloudmapsgeofavorites.utils;

import android.net.Uri;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nickgirga.nextcloudmapsgeofavorites.model.Geofavorite;

public class GeoUriParser {
    // URL scheme constants
    public static final int URL_SCHEME_GOOGLE_MAPS = 0;
    public static final int URL_SCHEME_OPENSTREETMAP = 1;
    public static final int URL_SCHEME_APPLE_MAPS = 2;

    private static final Pattern PATTERN_GEO = Pattern.compile("geo:(-?[\\d.]+),(-?[\\d.]+)");
    // Try to match not only geoUri but also Google Maps Uri
    private static final Pattern PATTERN_BROAD = Pattern.compile(
        "(?:@|&query=|&ce\nter=|geo:|#map=\\d{1,2}\\/)(-?\\d{1,2}\\.\\d+)(?:,|%2C|\\/)(-?\\d{1,3}\\.\\d{1,10})"
    );

    /**
     * Parses an URI into latitude and longitude
     * @param uri to parse
     * @param strict if true, the uri must be a valid geo: uri, otherwise a broader check is applied to include other uris, like Google Maps ones
     * @return the parsed coordinates in an array [lat,lon]
     * @throws IllegalArgumentException if the url could not be parsed
     */
    public static double[] parseUri(Uri uri, boolean strict) throws IllegalArgumentException {
        if (uri == null)
            throw new IllegalArgumentException("no uri");

        // Try to extract coordinates in uri string with regexp
        Pattern pattern = strict ? PATTERN_GEO : PATTERN_BROAD;
        Matcher m = pattern.matcher(uri.toString());
        if (!m.find() || m.groupCount() != 2)
            throw new IllegalArgumentException("unable to parse uri: unable to find coordinates in uri");

        // Obtain coordinates from regexp result
        String sLat = m.group(1);
        String sLon = m.group(2);
        double[] coords = null;
        try {
            // Check coordinates are numeric
            coords = new double[]{Double.parseDouble(sLat), Double.parseDouble(sLon)};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("unable to parse uri: coordinates are not double");
        }

        // Check coordinates validity
        String error = checkCoordsValidity(coords[0], coords[1]);
        if (error != null)
            throw new IllegalArgumentException(error);

        return coords;
    }

    public static Uri createGeoUri(double lat, double lon, String name) {
        String error = checkCoordsValidity(lat, lon);
        if (error != null)
            throw new IllegalArgumentException(error);

        String uriStr = "geo:" + lat + "," + lon;
        if (name != null)
            uriStr += "(" + name + ")";
        return Uri.parse(uriStr);
    }

    public static Uri createGmapsUri(double lat, double lon) {
        String error = checkCoordsValidity(lat, lon);
        if (error != null)
            throw new IllegalArgumentException(error);

        String uriStr = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
        return Uri.parse(uriStr);
    }

    public static Uri createMapUri(double lat, double lon, int urlScheme) {
        String error = checkCoordsValidity(lat, lon);
        if (error != null)
            throw new IllegalArgumentException(error);

        String uriStr;
        switch (urlScheme) {
            case URL_SCHEME_OPENSTREETMAP:
                // OpenStreetMap URL format
                uriStr = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=17/" + lat + "/" + lon;
                break;
            case URL_SCHEME_APPLE_MAPS:
                // Apple Maps URL format
                uriStr = "http://maps.apple.com/?ll=" + lat + "," + lon + "&q=" + lat + "," + lon;
                break;
            case URL_SCHEME_GOOGLE_MAPS:
            default:
                // Google Maps URL format
                uriStr = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
                break;
        }
        return Uri.parse(uriStr);
    }

    /**
     * Checks a latitude/longitude is valid
     * @param lat latitude
     * @param lon longitude
     * @return null if valid, a string containing an error otherwise
     */
    private static String checkCoordsValidity(double lat, double lon) {
        // Check coords validity
        if (lon <= -180 || lon >= 180 )
            return "Invalid longitude: " + lon;
        if (lat <= -90 || lat >= 90)
            return "Invalid latitude: " + lat;
        return null;
    }
}
