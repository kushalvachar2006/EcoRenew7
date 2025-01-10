package com.example.waste_management;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Trader_Details extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int MAPS_REQUEST_CODE = 1002;

    private EditText addressEditText, phoneEditText;
    private Button selectAddressButton, saveDetailsButton;

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude, currentLongitude;

    private FirebaseAuth mAuth;
    private DatabaseReference tradersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_details);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        tradersRef = FirebaseDatabase.getInstance().getReference("traders");

        // Initialize Views
        addressEditText = findViewById(R.id.address_input);
        phoneEditText = findViewById(R.id.phone_input);
        selectAddressButton = findViewById(R.id.select_address_button);
        saveDetailsButton = findViewById(R.id.save_details_button);

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request Location Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Fetch Current Location and Launch Google Maps
        selectAddressButton.setOnClickListener(v -> {
            fetchCurrentLocationAndOpenMaps();
        });

        // Save Details to Firebase
        saveDetailsButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (TextUtils.isEmpty(address)) {
                addressEditText.setError("Address is required");
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                phoneEditText.setError("Phone number is required");
                return;
            }

            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();

                Map<String, Object> traderDetails = new HashMap<>();
                traderDetails.put("address", address);
                traderDetails.put("phone", phone);

                tradersRef.child(userId).updateChildren(traderDetails)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Trader_Details.this, "Details saved successfully!", Toast.LENGTH_SHORT).show();

                            // Redirect to another activity if needed
                            Intent intent = new Intent(Trader_Details.this, MainActivity.class); // Replace MainActivity with your target activity
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Trader_Details.this, "Failed to save details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCurrentLocationAndOpenMaps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    // Launch Google Maps with Current Location
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=", currentLatitude, currentLongitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivityForResult(intent, MAPS_REQUEST_CODE);
                } else {
                    Toast.makeText(Trader_Details.this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAPS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String locationData = uri.toString();

                // Extract latitude and longitude
                String[] coordinates = locationData.replace("geo:", "").split(",");
                if (coordinates.length >= 2) {
                    double latitude = Double.parseDouble(coordinates[0]);
                    double longitude = Double.parseDouble(coordinates[1]);

                    // Reverse geocoding to get the address
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (!addresses.isEmpty()) {
                            String address = addresses.get(0).getAddressLine(0);
                            addressEditText.setText(address);
                        } else {
                            Toast.makeText(this, "Could not fetch address", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error fetching address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocationAndOpenMaps();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
