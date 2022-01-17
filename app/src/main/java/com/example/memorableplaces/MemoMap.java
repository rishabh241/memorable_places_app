package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.memorableplaces.databinding.ActivityMemoMapBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoMap extends FragmentActivity implements OnMapReadyCallback {
    LocationManager lm;
    LocationListener ll;

    private GoogleMap mMap;
    private ActivityMemoMapBinding binding;
    public void ceterOnlocation(Location location,String title){
        mMap.clear();
        LatLng location1 = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location1).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location1));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
                Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                ceterOnlocation(lastLocation, "your Last Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMemoMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        if(intent.getIntExtra("Place",0)==0){
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            ll = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                        ceterOnlocation(location,"your Location");
                }
            };
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ll);
//                Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                ceterOnlocation(lastLocation,"your Last Location");
            }
        }else{
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.location.get(intent.getIntExtra("Place",0)).latitude);
            placeLocation.setLongitude(MainActivity.location.get(intent.getIntExtra("Place",0)).longitude);
            ceterOnlocation(placeLocation,MainActivity.arrayList.get(intent.getIntExtra("Place",0)));
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String location ="";
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if(addresses.size()>0 && addresses!=null){
                        if(addresses.get(0).getAdminArea()!= null){
                            location+=addresses.get(0).getAdminArea()+" ";
                        }if(addresses.get(0).getLocality()!=null){
                            location+=addresses.get(0).getLocality()+" ";
                        }if(addresses.get(0).getSubLocality()!=null){
                            location+=addresses.get(0).getSubLocality()+" ";
                        }
                    }
                    if(location==""){
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-mm-yyyy");
                        location+=sdf.format(new Date());
                    }
                    LatLng latLng1 = new LatLng(latLng.latitude,latLng.longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng1).title(location));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    MainActivity.arrayList.add(location);
                    MainActivity.location.add(latLng1);
                    MainActivity.adapter.notifyDataSetChanged();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }
}