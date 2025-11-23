/*
 * Nextcloud Maps Geofavorites for Android
 *
 * This program is free software: you can redistribute com and/or modify
 * com under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that com will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nickgirga.nextcloudmapsgeofavorites.activity.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.ArrayList;

import com.nickgirga.nextcloudmapsgeofavorites.R;
import com.nickgirga.nextcloudmapsgeofavorites.activity.NextcloudMapsStyledActivity;
import com.nickgirga.nextcloudmapsgeofavorites.activity.about.AboutActivity;
import com.nickgirga.nextcloudmapsgeofavorites.activity.detail.GeofavoriteDetailActivity;
import com.nickgirga.nextcloudmapsgeofavorites.activity.login.LoginActivity;
import com.nickgirga.nextcloudmapsgeofavorites.activity.main.NavigationAdapter.NavigationItem;
import com.nickgirga.nextcloudmapsgeofavorites.activity.mappicker.MapPickerActivity;
import com.nickgirga.nextcloudmapsgeofavorites.api.ApiProvider;
import com.nickgirga.nextcloudmapsgeofavorites.fragments.GeofavoriteListFragment;
import com.nickgirga.nextcloudmapsgeofavorites.fragments.GeofavoriteMapFragment;
import com.nickgirga.nextcloudmapsgeofavorites.repository.GeofavoriteRepository;
import com.nickgirga.nextcloudmapsgeofavorites.utils.SettingsManager;
import com.nickgirga.nextcloudmapsgeofavorites.utils.ThemeUtils;

public class MainActivity extends NextcloudMapsStyledActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 3890;

    private static final String NAVIGATION_KEY_ADD_GEOFAVORITE_FROM_GPS = "add_from_gps";
    private static final String NAVIGATION_KEY_ADD_GEOFAVORITE_FROM_MAP = "add_from_map";
    private static final String NAVIGATION_KEY_MAP_URL_SCHEME = "map_url_scheme";
    private static final String NAVIGATION_KEY_SHOW_ABOUT = "about";
    private static final String NAVIGATION_KEY_SWITCH_ACCOUNT = "switch_account";

    private ArrayList<OnGpsPermissionGrantedListener> onGpsPermissionGrantedListener = new ArrayList<>();
    private DrawerLayout drawerLayout;

    private boolean isFabOpen = false;

    NavigationAdapter navigationCommonAdapter;

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void showMap() {
        replaceFragment(new GeofavoriteMapFragment());
        SettingsManager.setGeofavoriteListShownAsMap(this, true);
    }

    public void showList() {
        replaceFragment(new GeofavoriteListFragment());
        SettingsManager.setGeofavoriteListShownAsMap(this, false);
    }

    public void addOnGpsPermissionGrantedListener(OnGpsPermissionGrantedListener l) {
        onGpsPermissionGrantedListener.add(l);
    }

    public void removeOnGpsPermissionGrantedListener(OnGpsPermissionGrantedListener l) {
        onGpsPermissionGrantedListener.remove(l);
    }

    public void requestGpsPermissions() {
        ActivityCompat.requestPermissions(
            this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (OnGpsPermissionGrantedListener l : onGpsPermissionGrantedListener) {
                    l.onGpsPermissionGranted();
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean showMap = SettingsManager.isGeofavoriteListShownAsMap(this);
        if (showMap)
            showMap();
        else
            showList();

        FloatingActionButton fab = findViewById(R.id.open_fab);
        fab.setOnClickListener(view -> openFab(!this.isFabOpen));

        fab = findViewById(R.id.add_from_gps);
        fab.setOnClickListener(view -> addGeofavoriteFromGps());

        fab = findViewById(R.id.add_from_map);
        fab.setOnClickListener(view -> addGeofavoriteFromMap());

        setupNavigationMenu();

        drawerLayout = findViewById(R.id.drawerLayout);

        // Fetch Nextcloud theme colors
        ThemeUtils.fetchAndSaveTheme(this);
    }

    @Override
    protected void applyThemeColor() {
        super.applyThemeColor();
        
        // Apply theme to FABs using Material 3 colors
        FloatingActionButton openFab = findViewById(R.id.open_fab);
        FloatingActionButton addFromGpsFab = findViewById(R.id.add_from_gps);
        FloatingActionButton addFromMapFab = findViewById(R.id.add_from_map);
        
        applyThemeToFab(openFab);
        applyThemeToFab(addFromGpsFab);
        applyThemeToFab(addFromMapFab);
        
        // Apply theme to drawer header with Material 3 primary color
        View drawerHeader = findViewById(R.id.header_view);
        if (drawerHeader != null) {
            int primaryColor = com.google.android.material.color.MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorPrimary,
                ThemeUtils.getThemeColor(this)
            );
            drawerHeader.setBackgroundColor(primaryColor);
        }
    }

    @Override
    protected void onPause() {
        openFab(false);
        super.onPause();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setupNavigationMenu() {
        ArrayList<NavigationItem> navItems = new ArrayList<>();

        navigationCommonAdapter = new NavigationAdapter(this, item -> {
            switch (item.id) {
                case NAVIGATION_KEY_ADD_GEOFAVORITE_FROM_GPS:
                    addGeofavoriteFromGps();
                    break;
                case NAVIGATION_KEY_ADD_GEOFAVORITE_FROM_MAP:
                    addGeofavoriteFromMap();
                    break;
                case NAVIGATION_KEY_MAP_URL_SCHEME:
                    showMapUrlSchemeDialog();
                    break;
                case NAVIGATION_KEY_SHOW_ABOUT:
                    show_about();
                    break;
                case NAVIGATION_KEY_SWITCH_ACCOUNT:
                    switch_account();
                    break;
            }
        });

        navItems.add(new NavigationItem(NAVIGATION_KEY_ADD_GEOFAVORITE_FROM_GPS, getString(R.string.new_geobookmark_gps), R.drawable.ic_add_gps));
        navItems.add(new NavigationItem(NAVIGATION_KEY_ADD_GEOFAVORITE_FROM_MAP, getString(R.string.new_geobookmark_map), R.drawable.ic_add_map));
        navItems.add(new NavigationItem(NAVIGATION_KEY_MAP_URL_SCHEME, getString(R.string.map_url_scheme_settings), R.drawable.ic_nav));
        navItems.add(new NavigationItem(NAVIGATION_KEY_SHOW_ABOUT, getString(R.string.about), R.drawable.ic_info_grey));
        navItems.add(new NavigationItem(NAVIGATION_KEY_SWITCH_ACCOUNT, getString(R.string.switch_account), R.drawable.ic_logout_grey));
        navigationCommonAdapter.setItems(navItems);

        RecyclerView navigationMenuCommon = findViewById(R.id.navigationCommon);
        navigationMenuCommon.setAdapter(navigationCommonAdapter);
    }

    private void addGeofavoriteFromGps() {
        startActivity(
                new Intent(this, GeofavoriteDetailActivity.class)
        );
    }

    private void addGeofavoriteFromMap() {
        startActivity(
                new Intent(this, MapPickerActivity.class)
        );
    }

    private void showMapUrlSchemeDialog() {
        MapUrlSchemeDialogFragment dialog = MapUrlSchemeDialogFragment.newInstance(
            SettingsManager.getMapUrlScheme(this)
        );
        dialog.setOnUrlSchemeListener(urlScheme -> {
            SettingsManager.setMapUrlScheme(this, urlScheme);
        });
        dialog.show(getSupportFragmentManager(), MapUrlSchemeDialogFragment.MAP_URL_SCHEME_FRAGMENT);
    }

    private void show_about() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void switch_account() {
        ApiProvider.logout();
        GeofavoriteRepository.resetInstance();
        ThemeUtils.clearTheme(this);
        SingleAccountHelper.applyCurrentAccount(this, null);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openFab(boolean open) {
        FloatingActionButton addFromGpsFab = findViewById(R.id.add_from_gps);
        FloatingActionButton addFromMapFab = findViewById(R.id.add_from_map);

        if (open) {
            this.isFabOpen = true;
            
            // Show sub-FABs with fade and scale animation
            addFromGpsFab.setVisibility(View.VISIBLE);
            addFromGpsFab.setAlpha(0f);
            addFromGpsFab.setScaleX(0f);
            addFromGpsFab.setScaleY(0f);
            addFromGpsFab.setTranslationY(0);
            addFromGpsFab.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(-getResources().getDimension(R.dimen.fab_vertical_offset))
                .setDuration(200)
                .start();
            
            addFromMapFab.setVisibility(View.VISIBLE);
            addFromMapFab.setAlpha(0f);
            addFromMapFab.setScaleX(0f);
            addFromMapFab.setScaleY(0f);
            addFromMapFab.setTranslationY(0);
            addFromMapFab.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(-getResources().getDimension(R.dimen.fab_vertical_offset) * 2)
                .setDuration(250)
                .start();
        } else {
            this.isFabOpen = false;
            
            // Hide sub-FABs with fade and scale animation
            addFromGpsFab.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .translationY(0)
                .setDuration(150)
                .withEndAction(() -> addFromGpsFab.setVisibility(View.GONE))
                .start();
            
            addFromMapFab.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .translationY(0)
                .setDuration(150)
                .withEndAction(() -> addFromMapFab.setVisibility(View.GONE))
                .start();
        }
    }

    public interface OnGpsPermissionGrantedListener {
        public void onGpsPermissionGranted();
    }

}
