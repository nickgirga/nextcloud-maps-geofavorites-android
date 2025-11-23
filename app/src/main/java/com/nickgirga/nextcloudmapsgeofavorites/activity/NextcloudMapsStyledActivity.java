package com.nickgirga.nextcloudmapsgeofavorites.activity;

import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.color.MaterialColors;
import com.nickgirga.nextcloudmapsgeofavorites.R;
import com.nickgirga.nextcloudmapsgeofavorites.utils.ThemeUtils;

public class NextcloudMapsStyledActivity extends AppCompatActivity {
    private static final String TAG = "NextcloudMapsStyled";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply Material 3 dynamic theming BEFORE super.onCreate
        ThemeUtils.applyDynamicTheme(this);
        
        super.onCreate(savedInstanceState);

        // For whatever reason, android:windowLightStatusBar is ignored in styles.xml
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyThemeColor();
    }

    /**
     * Apply the Nextcloud theme color to UI elements using Material 3 colors.
     * This method can be overridden by subclasses to customize theming behavior.
     */
    protected void applyThemeColor() {
        int themeColor = ThemeUtils.getThemeColor(this);
        
        Log.d(TAG, "Applying theme color: #" + Integer.toHexString(themeColor));
        
        // Apply to action bar if present - use Material 3 primary color
        if (getSupportActionBar() != null) {
            int primaryColor = MaterialColors.getColor(this, 
                com.google.android.material.R.attr.colorPrimary, themeColor);
            getSupportActionBar().setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(primaryColor)
            );
        }
        
        // Apply to status bar using Material 3 primary container
        int statusBarColor = MaterialColors.getColor(this,
            com.google.android.material.R.attr.colorPrimaryContainer, themeColor);
        getWindow().setStatusBarColor(statusBarColor);
    }

    /**
     * Helper method to apply theme color to a FloatingActionButton using Material 3
     */
    protected void applyThemeToFab(FloatingActionButton fab) {
        if (fab != null) {
            // Material 3 FABs automatically use colorPrimaryContainer
            // Just ensure it's using the Material 3 style
            int primaryColor = MaterialColors.getColor(this,
                com.google.android.material.R.attr.colorPrimaryContainer,
                ThemeUtils.getThemeColor(this));
            fab.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
        }
    }

}
