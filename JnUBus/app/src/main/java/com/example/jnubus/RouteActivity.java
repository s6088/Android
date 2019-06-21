package com.example.jnubus;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline currentPolyline;
    private String [] lat, lng, places;
    private List <Integer> index;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        index = new ArrayList<>();
        db  = FirebaseFirestore.getInstance();
        fatchRoute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
           googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        } catch (Resources.NotFoundException e) {
        }
        for(int i=0; i<index.size(); i++){
            int x = i;
            Log.d("TAG", places[i]);
            mMap.addMarker(new MarkerOptions().position( new LatLng(Double.parseDouble(lat[x]), Double.parseDouble(lng[x])) ) .title(places[x]) );
        }
        new FetchURL().execute(getUrl("driving"), "driving");
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(Double.parseDouble(lat[0]), Double.parseDouble(lng[0]))).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    public void fatchRoute (){
        db.collection("buses").whereEqualTo("name", getIntent().getStringExtra("busName"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String [] waypoints = document.getString("waypoints").split(" ");
                            getLatLng(waypoints);
                            return;
                        }
                    }
                });
    }

    public void getLatLng (final String [] waypoints){
        index.clear();
        int cnt = 0, fix = waypoints.length;
        lat = new String[fix];
        lng = new String[fix];
        places = new String[fix];
        for(int i=0; i<waypoints.length; i++){

            final int x = i;

            db.collection("places").document(waypoints[i]).get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            lat[x] = documentSnapshot.getString("lat");
                            lng[x] = documentSnapshot.getString("lng");
                            places[x] = documentSnapshot.getString("name");
                            index.add(x);
                            if(index.size() == waypoints.length)readyMap();
                        }
                    }
            );

        }
    }

    void readyMap (){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }




    private String getUrl(String directionMode) {
        int x = index.size()-1;
        String str_origin = "origin=" + lat[0] + "," + lng[0];
        String str_dest = "destination=" + lat[x] + "," + lng[x];
        String mode = "mode=" + directionMode;
        String waypoints = "&waypoints=";
        for(int i=1; i<x; i++){
            if(i==1) waypoints += "via:" + lat[i] + "," + lng[i];
            else waypoints += "|via:" + lat[i] + "," + lng[i];
        }
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String url = "https://maps.googleapis.com/maps/api/directions/json" + "?" + parameters + waypoints + "&alternatives=false" + "&key=AIzaSyDhhwfZgJv4DCVuX-RDuXLXfoHWL6FIPAw";
        return url;
    }

    class FetchURL extends AsyncTask<String, Void, String> {

        String directionMode = "driving";

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            directionMode = strings[0];
            try {
                data = downloadUrl(strings[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PointsParser parserTask = new PointsParser(directionMode);
            parserTask.execute(s);
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("mylog", "Exception downloading URL: " + e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }


    }

    class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String directionMode = "driving";

        public PointsParser(String directionMode) {
            this.directionMode = directionMode;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("mylog", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("mylog", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("mylog", "Executing routes");
                Log.d("mylog", routes.toString());

            } catch (Exception e) {
                Log.d("mylog", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                if (directionMode.equalsIgnoreCase("walking")) {
                    lineOptions.width(10);
                    lineOptions.color(Color.MAGENTA);
                } else {
                    lineOptions.width(20);
                    lineOptions.color(Color.BLUE);
                }
                Log.d("mylog", "onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (currentPolyline != null) currentPolyline.remove();
                currentPolyline = mMap.addPolyline(lineOptions);
                currentPolyline.setWidth(10);
                currentPolyline.setColor(Color.BLACK);
            } else {
                Log.d("mylog", "without Polylines drawn");
            }
        }
    }
}
