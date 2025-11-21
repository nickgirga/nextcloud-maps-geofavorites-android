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

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import com.nickgirga.nextcloudmapsgeofavorites.R;
import com.nickgirga.nextcloudmapsgeofavorites.utils.GeoUriParser;

/**
 * Dialog to show and choose the map URL scheme for opening locations.
 */
public class MapUrlSchemeDialogFragment extends DialogFragment {

    private final static String TAG = MapUrlSchemeDialogFragment.class.getSimpleName();

    public static final String MAP_URL_SCHEME_FRAGMENT = "MAP_URL_SCHEME_FRAGMENT";
    private static final String KEY_URL_SCHEME = "URL_SCHEME";

    private OnUrlSchemeListener onUrlSchemeListener;
    private View mView;
    private View[] mTaggedViews;
    private Button mCancel;

    private int mCurrentUrlScheme;

    public static MapUrlSchemeDialogFragment newInstance(int urlScheme) {
        MapUrlSchemeDialogFragment dialogFragment = new MapUrlSchemeDialogFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_URL_SCHEME, urlScheme);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep the state of the fragment on configuration changes
        setRetainInstance(true);

        mView = null;

        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments may not be null");
        }
        mCurrentUrlScheme = arguments.getInt(KEY_URL_SCHEME, GeoUriParser.URL_SCHEME_GOOGLE_MAPS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_url_scheme_fragment, container, false);

        setupDialogElements(mView);
        setupListeners();

        return mView;
    }

    public void setOnUrlSchemeListener(OnUrlSchemeListener listener) {
        this.onUrlSchemeListener = listener;
    }

    /**
     * find all relevant UI elements and set their values.
     *
     * @param view the parent view
     */
    private void setupDialogElements(View view) {
        mCancel = view.findViewById(R.id.cancel);

        mTaggedViews = new View[6];
        mTaggedViews[0] = view.findViewById(R.id.urlSchemeGoogleMaps);
        mTaggedViews[0].setTag(GeoUriParser.URL_SCHEME_GOOGLE_MAPS);
        mTaggedViews[1] = view.findViewById(R.id.urlSchemeGoogleMapsText);
        mTaggedViews[1].setTag(GeoUriParser.URL_SCHEME_GOOGLE_MAPS);
        mTaggedViews[2] = view.findViewById(R.id.urlSchemeOpenStreetMap);
        mTaggedViews[2].setTag(GeoUriParser.URL_SCHEME_OPENSTREETMAP);
        mTaggedViews[3] = view.findViewById(R.id.urlSchemeOpenStreetMapText);
        mTaggedViews[3].setTag(GeoUriParser.URL_SCHEME_OPENSTREETMAP);
        mTaggedViews[4] = view.findViewById(R.id.urlSchemeAppleMaps);
        mTaggedViews[4].setTag(GeoUriParser.URL_SCHEME_APPLE_MAPS);
        mTaggedViews[5] = view.findViewById(R.id.urlSchemeAppleMapsText);
        mTaggedViews[5].setTag(GeoUriParser.URL_SCHEME_APPLE_MAPS);

        setupActiveSchemeSelection();
    }

    /**
     * tints the icon reflecting the actual URL scheme choice in the apps primary color.
     */
    private void setupActiveSchemeSelection() {
        for (View view: mTaggedViews) {
            if (mCurrentUrlScheme != (int) view.getTag()) {
                continue;
            }
            if (view instanceof ImageButton) {
                Drawable normalDrawable = ((ImageButton) view).getDrawable();
                Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
                DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(), R.color.selector_item_selected));
            }
            if (view instanceof TextView) {
                ((TextView)view).setTextColor(ContextCompat.getColor(getContext(), R.color.selector_item_selected));
                ((TextView)view).setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
    }

    /**
     * setup all listeners.
     */
    private void setupListeners() {
        mCancel.setOnClickListener(view -> dismiss());

        OnUrlSchemeClickListener urlSchemeClickListener = new OnUrlSchemeClickListener();

        for (View view : mTaggedViews) {
            view.setOnClickListener(urlSchemeClickListener);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private class OnUrlSchemeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismissAllowingStateLoss();
            if (onUrlSchemeListener != null)
                onUrlSchemeListener.onUrlSchemeChosen((int) v.getTag());
        }
    }

    public interface OnUrlSchemeListener {
        void onUrlSchemeChosen(int urlScheme);
    }
}
