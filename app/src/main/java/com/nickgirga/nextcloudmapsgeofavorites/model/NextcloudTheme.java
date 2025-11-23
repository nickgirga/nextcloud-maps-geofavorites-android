/*
 * Nextcloud Maps Geofavorites for Android
 *
 * @copyright Copyright (c) 2020 Nick Girga <nickgirga@gmail.com>
 * @author Nick Girga <nickgirga@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nickgirga.nextcloudmapsgeofavorites.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NextcloudTheme {
    @Expose
    @SerializedName("color")
    private String color;

    @Expose
    @SerializedName("color-primary")
    private String colorPrimary;

    @Expose
    @SerializedName("color-text")
    private String colorText;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("slogan")
    private String slogan;

    public String getColor() {
        return color;
    }

    public String getColorPrimary() {
        return colorPrimary != null ? colorPrimary : color;
    }

    public String getColorText() {
        return colorText;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getSlogan() {
        return slogan;
    }
}
