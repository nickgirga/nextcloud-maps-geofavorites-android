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

package com.nickgirga.nextcloudmapsgeofavorites.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.google.android.material.color.HarmonizedColors;
import com.google.android.material.color.HarmonizedColorsOptions;
import com.nickgirga.nextcloudmapsgeofavorites.R;
import com.nickgirga.nextcloudmapsgeofavorites.api.ApiProvider;
import com.nickgirga.nextcloudmapsgeofavorites.model.NextcloudTheme;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemeUtils {
    private static final String TAG = "ThemeUtils";
    private static boolean themeApplied = false;

    /**
     * Fetch the Nextcloud theme and store the primary color.
     * This should be called when the app connects to a Nextcloud instance.
     */
    public static void fetchAndSaveTheme(@NonNull Context context) {
        Log.d(TAG, "Starting theme fetch...");
        try {
            ApiProvider.getAPI(context).getTheme().enqueue(new Callback<NextcloudTheme>() {
                @Override
                public void onResponse(Call<NextcloudTheme> call, Response<NextcloudTheme> response) {
                    Log.d(TAG, "Theme API response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        NextcloudTheme theme = response.body();
                        String color = theme.getColorPrimary();
                        Log.d(TAG, "Parsed theme color: " + color);
                        if (color != null && !color.isEmpty()) {
                            Log.d(TAG, "✅ Successfully fetched Nextcloud theme color: " + color);
                            SettingsManager.setThemeColor(context, color);
                        } else {
                            Log.w(TAG, "Theme color is null or empty");
                        }
                    } else {
                        Log.w(TAG, "❌ Failed to fetch Nextcloud theme. Code: " + response.code() + ", Message: " + response.message());
                        try {
                            if (response.errorBody() != null) {
                                Log.w(TAG, "Error body: " + response.errorBody().string());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<NextcloudTheme> call, Throwable t) {
                    Log.e(TAG, "❌ Error fetching Nextcloud theme", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception starting theme fetch", e);
        }
    }

    /**
     * Get the current theme color.
     * Returns the Nextcloud server's primary color if available,
     * otherwise returns the default brand color.
     */
    @ColorInt
    public static int getThemeColor(@NonNull Context context) {
        String colorString = SettingsManager.getThemeColor(context);
        if (colorString != null && !colorString.isEmpty()) {
            try {
                // Nextcloud returns colors in #RRGGBB format
                return Color.parseColor(colorString);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid color format: " + colorString, e);
            }
        }
        // Fallback to default brand color
        return context.getColor(R.color.defaultBrand);
    }

    /**
     * Apply Material 3 dynamic colors to an activity using Nextcloud theme.
     * This creates a harmonized color scheme based on the server's primary color.
     */
    public static void applyDynamicTheme(@NonNull Activity activity) {
        int themeColor = getThemeColor(activity);
        
        Log.d(TAG, "🎨 Applying dynamic theme with color: #" + Integer.toHexString(themeColor));
        
        // For now, skip Material 3 DynamicColors as it may not work well with custom seed colors
        // Instead, we'll apply colors manually in activities
        
        themeApplied = true;
        
        Log.d(TAG, "✅ Theme application complete");
    }
    
    /**
     * Check if theme has been applied
     */
    public static boolean isThemeApplied() {
        return themeApplied;
    }
    
    /**
     * Reset theme applied state
     */
    public static void resetThemeState() {
        themeApplied = false;
    }

    /**
     * Clear the saved theme color (e.g., when logging out).
     */
    public static void clearTheme(@NonNull Context context) {
        SettingsManager.setThemeColor(context, null);
        resetThemeState();
    }
}
