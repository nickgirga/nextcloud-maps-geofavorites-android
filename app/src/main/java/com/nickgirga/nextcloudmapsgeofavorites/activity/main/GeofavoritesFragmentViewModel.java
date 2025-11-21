package com.nickgirga.nextcloudmapsgeofavorites.activity.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.List;

import com.nickgirga.nextcloudmapsgeofavorites.model.Geofavorite;
import com.nickgirga.nextcloudmapsgeofavorites.repository.GeofavoriteRepository;

public class GeofavoritesFragmentViewModel extends ViewModel {
    private GeofavoriteRepository mRepo;

    public void init(Context applicationContext) {
        mRepo = GeofavoriteRepository.getInstance(applicationContext);
    }

    public LiveData<List<Geofavorite>> getGeofavorites(){
        mRepo.updateGeofavorites();
        return mRepo.getGeofavorites();
    }

    public void updateGeofavorites() {
        mRepo.updateGeofavorites();
    }

    public LiveData<HashSet<String>> getCategories(){
        return mRepo.getCategories();
    }

    public void deleteGeofavorite(Geofavorite geofav) {
        mRepo.deleteGeofavorite(geofav);
    }

    public LiveData<Boolean> getIsUpdating(){
        return mRepo.isUpdating();
    }

    public LiveData<Boolean> getOnFinished(){
        return mRepo.onFinished();
    }

}
