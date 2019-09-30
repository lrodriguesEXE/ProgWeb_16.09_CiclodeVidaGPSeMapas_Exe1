package progweb.ciclodevidagpsemapas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ArrayList<UserLocations> arrayListLocations = new ArrayList<>();

    private static final int REQUEST_CODE_GPS = 1001;
    private static final double distanceFromLast = 200;

    private TextView locationTextView;
    private EditText textInputEditText;

    private double latitudeAtual;
    private double longitudeAtual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);
        textInputEditText = findViewById((R.id.textInputEditText));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location){
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                latitudeAtual = lat;
                longitudeAtual = lon;

                UserLocations actualLocation = new UserLocations(lat,lon);

                if(arrayListLocations.size() == 0 || getDistancia(actualLocation,arrayListLocations.get(arrayListLocations.size()-1)) >= distanceFromLast) {
                    if (arrayListLocations.size() == 50) {
                        arrayListLocations.remove(0);
                        arrayListLocations.add(new UserLocations(lat, lon));
                    } else {
                        arrayListLocations.add(new UserLocations(lat, lon));
                    }
                }

                String locPrintOut = "";
                for(int n = 0;n< arrayListLocations.size();n++){
                    locPrintOut += String.format("Localização %d: Lat= %f , Long= %f \n",(n+1),arrayListLocations.get(n).getLatitude(),arrayListLocations.get(n).getLongitude());
                }
                locationTextView.setText(String.format("Coordenadas Atuais = Lat: %f, Long: %f\n\n%s",lat,lon,locPrintOut));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    public void onClick(View view){
        String userInputedText  = textInputEditText.getText().toString();
        Uri gmmIntentUri = Uri.parse(String.format("geo: %f,%f?q=%s",latitudeAtual,longitudeAtual,userInputedText));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == REQUEST_CODE_GPS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,12000,200,locationListener);
                }
                else{
                    Toast.makeText(this,getString(R.string.no_gps_no_app),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public double getDistancia(UserLocations obj1, UserLocations obj2){
        double dlon, dlat, a, distancia;
        dlon = obj1.getLongitude() - obj1.getLongitude();
        dlat = obj1.getLatitude() - obj2.getLatitude();
        a = Math.pow(Math.sin(dlat/2),2) + Math.cos(obj1.getLatitude()) * Math.cos(obj2.getLatitude()) * Math.pow(Math.sin(dlon/2),2);
        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6378140 * distancia; /* 6378140 is the radius of the Earth in meters*/
    }


}


