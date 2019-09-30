package progweb.ciclodevidagpsemapas;

public class UserLocations {

    private double latitude;
    private double longitude;

    public UserLocations(double lat,double lon){
        latitude  =lat;
        longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String userLocationsToString(){
        return latitude+" "+longitude;
    }
}
