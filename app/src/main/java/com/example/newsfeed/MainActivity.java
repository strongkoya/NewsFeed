package com.example.newsfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementAdapter elementAdapter;
    private ArrayList<Element> fullElemntAdapter;
    private String city="breaking news";
    public LocationListener listener;
    private EditText searchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        elementAdapter = new ElementAdapter(new ArrayList<>());





        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }*/
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Permission already granted, start location updates
        }*/

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), true);
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                }

                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            if (provider != null) {
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 300000, 0, listener);

                if (location == null) {
                    locationManager.requestSingleUpdate(provider, listener, null);

                    return;
                }

                //located(location) affecte à city le nom de la cité via getAdminArea()
                located(location);


                //  Toast.makeText(getApplicationContext(), city, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "erreur de localisation", Toast.LENGTH_LONG).show();

            }


            recyclerView.setAdapter(elementAdapter);

           // Toast.makeText(getApplicationContext(), city + " d", Toast.LENGTH_LONG).show();
            searchNewsByCity(city);
            searchEt = findViewById(R.id.searchEt);


            // Ajouter le TextWatcher
            searchEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Mettre à jour les éléments de RecyclerView en fonction de la recherche
                    //recyclerViewAdapter.getFilter().filter(s.toString());
                    if (elementAdapter != null && fullElemntAdapter != null) {
                        elementAdapter.filter(s.toString(), fullElemntAdapter);
                    }
                    //  Toast.makeText(getContext(),s.toString(),Toast.LENGTH_SHORT).show();
                    //  Log.d("EditText :     ",s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });




    }

    private void located(Location location) {
      Double  lat = location.getLatitude();
       Double lon = location.getLongitude();

        if (!Geocoder.isPresent()) {
            Toast.makeText(getApplicationContext(), "Les coordonnées sont indisponibles", Toast.LENGTH_LONG).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lon, 10);
            //  String cityName = addressList.get(0).getAddressLine(0);
            //   String stateName = addressList.get(0).getAddressLine(1);
           //  String countryName = addressList.get(0).getAddressLine(2);
            // Toast.makeText(getApplicationContext(), String.valueOf(addressList.get(0).getAdminArea()),Toast.LENGTH_LONG).show();
            //  Toast.makeText(getApplicationContext(), addressList.get(0).getAdminArea(),Toast.LENGTH_LONG).show();

            if (addressList == null) {
                Toast.makeText(getApplicationContext(), "Les coordonnées sont indisponibles", Toast.LENGTH_LONG).show();
                return;
            }


            city = addressList.get(0).getCountryName();
           // Toast.makeText(getApplicationContext(), city,Toast.LENGTH_LONG).show();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    private void searchNewsByCity(String cityName) {
        NewsApiClient newsApiClient = new NewsApiClient("9d6490beee234a3381ed6de9bc962ee8");
        newsApiClient.getEverything(
                new EverythingRequest.Builder()
                        .q(cityName)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        List<Element> elementList = new ArrayList<>();
                        for (int i = 0; i < response.getArticles().size(); i++) {
                          // Log.d("titre "+i+" :",response.getArticles().get(i).getTitle());
                            Element element = new Element(response.getArticles().get(i).getAuthor(),response.getArticles().get(i).getTitle(),response.getArticles().get(i).getDescription(),response.getArticles().get(i).getUrl(),response.getArticles().get(i).getUrlToImage(),response.getArticles().get(i).getPublishedAt());
                            elementList.add(element);
                        }
                        elementAdapter.setElementList(elementList);

                        fullElemntAdapter = new ArrayList<>(elementAdapter.getElementList());

                        elementAdapter.notifyDataSetChanged();
                        elementAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(Element element) {
                              //   Toast.makeText(getApplicationContext(), "youe have clicked !!!!!" + element.getTitle(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), ElementDetail.class);
                                intent.putExtra("url", element.getUrl());
                                intent.putExtra("title", element.getTitle());
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (!isNetworkAvailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "Il n'y a pas de connexion, veuillez l'activer!!!", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
        );

    }



    // Vérifie si le terminal est connecté à Internet
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startActivity(new Intent(this,MainActivity.class));
                finish();
            } else {
                // Permission denied, handle the error
                Toast.makeText(getApplicationContext(),"Permission denied, So breaking news are shown",Toast.LENGTH_LONG).show();
            }
        }
    }

}