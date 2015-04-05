package com.example.daniel.myapplication;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.daniel.myapplication.dummy.PopupAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MapsActivity extends FragmentActivity {

    private final int MAX_PLACES = 20;
    private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon, pawIcon;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locMan;
    private Marker[] placeMarkers;
    private MarkerOptions[] places;
    private Marker userMarker;
    private double lat;
    private double lng;
    private String animal;

    private class GetPlaces extends AsyncTask<String, Void, String> {
        //fetch and parse place data
        @Override
        protected String doInBackground(String... placesURL) {
            //fetch places
            StringBuilder placesBuilder = new StringBuilder();
            for (String placeSearchURL : placesURL) {
                //execute search
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //try to fetch the data
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();
                    if (placeSearchStatus.getStatusCode() == 200) {
                        //we have an OK response
                        HttpEntity placesEntity = placesResponse.getEntity();
                        InputStream placesContent = placesEntity.getContent();
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();
        }

        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
                        placeMarkers[pm].remove();
                }
            }
            try {
                //parse JSON
                JSONObject resultObject = new JSONObject(result);
                JSONArray placesArray = resultObject.getJSONArray("results");
                places = new MarkerOptions[placesArray.length()];
                //loop through places
                for (int p=0; p<placesArray.length(); p++) {
                    //parse each place
                    boolean missingValue=false;

                    LatLng placeLL=null;
                    String placeName="";
                    String vicinity="";
                    String phoneNum="";
                    String rating="";
                    int currIcon = pawIcon;

                    try{
                        //attempt to retrieve place data values
                        missingValue=false;
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                        placeLL = new LatLng(
                            Double.valueOf(loc.getString("lat")),
                            Double.valueOf(loc.getString("lng")));
                        JSONArray types = placeObject.getJSONArray("types");
                        for(int t=0; t<types.length(); t++){
                            //what type is it
                            /*
                            String thisType=types.get(t).toString();
                            if(thisType.contains("food")){
                                currIcon = foodIcon;
                                break;
                            }
                            else if(thisType.contains("bar")){
                                currIcon = drinkIcon;
                                break;
                            }
                            else if(thisType.contains("store")){
                                currIcon = shopIcon;
                                break;
                            }
                            */
                            vicinity = placeObject.getString("vicinity");
                            placeName = placeObject.getString("name");
                            //phoneNum = placeObject.getString("formatted_phone_number");
                            rating = placeObject.getString("rating");
                        }
                    }
                    catch(JSONException jse){
                        missingValue=true;
                        jse.printStackTrace();
                    }
                    if(missingValue) places[p]=null;
                    else
                        places[p]=new MarkerOptions()
                            .position(placeLL)
                            .title(placeName)
                            .icon(BitmapDescriptorFactory.fromResource(currIcon))
                            .snippet("Address: "+vicinity + ";Rating: " + rating+" out of 5");
                            //.snippet(phoneNum)
                            //.snippet(rating);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if(places!=null && placeMarkers!=null){
                for(int p=0; p<places.length && p<placeMarkers.length; p++){
                    //will be null if a value was missing
                    if(places[p]!=null)
                        placeMarkers[p]=mMap.addMarker(places[p]);
                }
            }

            mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
            //mMap.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) MapsActivity.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        placeMarkers = new Marker[MAX_PLACES];

        Intent activityThatCalled = getIntent();
        animal = activityThatCalled.getExtras().getString("animal");

        foodIcon = R.drawable.red_point;
        drinkIcon = R.drawable.blue_point;
        shopIcon = R.drawable.green_point;
        otherIcon = R.drawable.purple_point;
        pawIcon = R.drawable.paw_marker;


        updatePlaces();
        setUpMapIfNeeded();

        /*
        double latitude=0;
        double longitude=0;

        List<Address> geocodeMatches = null;

        try {
            geocodeMatches =
                    new Geocoder(this).getFromLocationName(
                            "Veterinarian", 3);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int i=1;
        while (!geocodeMatches.isEmpty())
        {
            latitude = geocodeMatches.get(0).getLatitude();
            longitude = geocodeMatches.get(0).getLongitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker "+i));
            geocodeMatches.remove(0);
            i++;
        }
        */

    }
    private void updatePlaces(){
        //update location

        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        lat = lastLoc.getLatitude();
        lng = lastLoc.getLongitude();
        if(userMarker!=null) {userMarker.remove();}
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                //"&radius=50000&sensor=true" +
                "&types=veterinary_care"+
                "&keyword="+removeSpaces(animal)+
                "&rankby=distance"+
                "&opennow"+
                "&key=AIzaSyA9D4WbPX9nk3I06wrpjxNKcpAJbKejhC8";
        new GetPlaces().execute(placesSearchStr);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        LatLng lastLatLng = new LatLng(lat, lng);
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .snippet("Your last recorded location; "));
        CameraUpdate center = CameraUpdateFactory.newLatLng(lastLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    private String removeSpaces(String keywords){
        String tempWord = "";
        for(int i=0;i<keywords.length();i++){
            if(keywords.charAt(i)==' '){
                tempWord += "%20";
            }else{
                tempWord += keywords.charAt(i);
            }
        }
        return tempWord;
    }
}
