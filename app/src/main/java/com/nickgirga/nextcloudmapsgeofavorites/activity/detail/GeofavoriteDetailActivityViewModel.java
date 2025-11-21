package com.nickgirga.nextcloudmapsgeofavorites.activity.detail;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.nickgirga.nextcloudmapsgeofavorites.model.Geofavorite;
import com.nickgirga.nextcloudmapsgeofavorites.repository.GeofavoriteRepository;

public class GeofavoriteDetailActivityViewModel extends ViewModel {
    private GeofavoriteRepository mRepo;

    public void init(Context applicationContext) {
        mRepo = GeofavoriteRepository.getInstance(applicationContext);
    }

    public Geofavorite getGeofavorite(int id) {
        return mRepo.getGeofavorite(id);
    }

    public void saveGeofavorite(Geofavorite geofav) {
        mRepo.saveGeofavorite(geofav);
    }

    public LiveData<HashSet<String>> getCategories(){
        return mRepo.getCategories();
    }

    public LiveData<Boolean> getIsUpdating(){
        return mRepo.isUpdating();
    }

    public LiveData<Boolean> getOnFinished(){
        return mRepo.onFinished();
    }

}
