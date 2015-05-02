/********************************************************************************
 *
 * Location Based Photo Browser
 *
 ********************************************************************************
 *		R E V I S I O N   H I S T O R Y
 ********************************************************************************
 *
 * Date        	Author  	  Description
 * ---------    ---------  	  ---------------------------------------------------
 * JUL 12		team		  Initial Version v0.1
 *
 *
 *******************************************************************************/

package team.photobrowser.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/**
 * Main Activity,
 * 
 * Show the Map (google map)
 * Allow the user to select an area for show the pictures
 * 
 */

public class LocationBasedPhotoBrowserActivity extends MapActivity {
    //MapActivity is a special sub-class of Activity, provided by the Maps library, which provides important map capabilities.
	
  	protected MapView mapView;
	protected Button showPhotosButton;
	protected Button exitButton;
	
	protected Intent photoActivityIntent; //Will be the Intent of the Photo Viewer activity.
	protected float topLatitude,bottomLatitude,leftLongitude,rightLongitude,
	centerLatitude,centerLongitude,latitudeSpan,longitudeSpan,radious; //Values of the map
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
    	
    	showPhotosButton = (Button) findViewById(R.id.showPhotosButton);
    	showPhotosButton.setOnClickListener(ShowPhotosButtonListener);
    	exitButton = (Button) findViewById(R.id.exitButton);
    	exitButton.setOnClickListener(ExitButtonListener);
    	
        mapView = (MapView) findViewById(R.id.mapview);
	    //Simple zoom feature built into the MapView class.
        mapView.setBuiltInZoomControls(true);
	}		
	
	/**
	 * Required.
	 * 
	 * This method is required for some accounting from the Maps service 
	 * to see if you're currently displaying any route information.
	 * 
	 */
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }     
    
    private boolean hasInternetConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Test for connection
        if (connManager.getActiveNetworkInfo() != null
                && connManager.getActiveNetworkInfo().isAvailable()
                && connManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    
    private OnClickListener ShowPhotosButtonListener = new OnClickListener() {
        public void onClick(View v) {        	
        	if(!hasInternetConnection()){
    			Toast toast = Toast.makeText(getApplicationContext(),"No Internet Connection. Can't show photos.", Toast.LENGTH_LONG);
    		    toast.show();
    		} else {
    			
	        	calculateArea();
	        	
	        	//send the bounding box information to the photo display activity
		    	photoActivityIntent = new Intent(LocationBasedPhotoBrowserActivity.this, PhotosDisplay_Activity.class );	
	        	photoActivityIntent.putExtra("topLatitude", topLatitude);
	        	photoActivityIntent.putExtra("bottomLatitude", bottomLatitude);
	        	photoActivityIntent.putExtra("leftLongitude", leftLongitude);
	        	photoActivityIntent.putExtra("rightLongitude", rightLongitude);
	        	      	
	        	startActivity(photoActivityIntent);
    		}
        }        	
    };  
    
    private OnClickListener ExitButtonListener = new OnClickListener() {
        public void onClick(View v) {        	
        	finish();
        }        	
    };    
    
    private void calculateArea(){ //calculate the minimum and maximum latitude and longitude
    	                          //based on the google maps information
    	   
    	GeoPoint mapTopLeft = mapView.getProjection().fromPixels(0, 0);
    	topLatitude = (float) (mapTopLeft.getLatitudeE6()/1E6); //getLatitudeE6() returns the latitude of this GeoPoint in microdegrees (degrees * 1E6)
    	leftLongitude = (float) ((float)(mapTopLeft.getLongitudeE6())/1E6);
    	    	   
    	GeoPoint mapBottomRight = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
    	bottomLatitude = (float) ((float)(mapBottomRight.getLatitudeE6())/1E6);
    	rightLongitude = (float) ((float)(mapBottomRight.getLongitudeE6())/1E6);    	
    }
   
    
}