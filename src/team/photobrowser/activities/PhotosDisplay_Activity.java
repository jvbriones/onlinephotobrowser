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

import java.util.LinkedList;

import team.photobrowser.flickr.FlickrHandler;
import team.photobrowser.flickr.FlickrPhotoInfo;
import team.photobrowser.panoramio.PanoramioHandler;
import team.photobrowser.panoramio.PanoramioPhotoInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Photo Display Activity,
 * 
 * Show the photos included in the area provided.
 * Allow the user to navigate between the photos.
 * Allow the user to go back and select another area.
 * 
 */

@SuppressLint({ "ParserError", "ParserError", "ParserError", "ParserError" })
public class PhotosDisplay_Activity extends Activity {
	
    protected Button backButton;
    protected Button prevPhotoButton;
    protected Button nextPhotoButton;
    protected Button infoPhotoButton;
    
	protected float maxLatitude,minLatitude,maxLongitude,minLongitude;
    protected int panoramioIndex = 0; //photoList index
    protected int flickerIndex = 0;
    protected WebView myWebView;
    protected LinkedList<PanoramioPhotoInfo> panoramioPhotoList; // here we'll have the photos' information
	protected LinkedList<FlickrPhotoInfo> flickrPhotoList;
	protected boolean showingPanoramio;
	protected Toast toast;
	
	protected PanoramioHandler panoramioHandler;
	protected FlickrHandler flickrHandler;
	
	@Override
	public void onCreate(Bundle mBundle) {
		super.onCreate(mBundle);
		
		setContentView(R.layout.photodisplay);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			maxLatitude = bundle.getFloat("topLatitude");
			minLatitude = bundle.getFloat("bottomLatitude");
			maxLongitude = bundle.getFloat("rightLongitude");
			minLongitude = bundle.getFloat("leftLongitude");
		}
		toast = Toast.makeText(this, "" , Toast.LENGTH_LONG ); // we'll use only one toast, changing its text when we need to show it
		                                                       //this prevents many toasts from showing if the user presses the information button many times
		
		backButton = (Button) findViewById(R.id.backSelectArea);
		backButton.setOnClickListener(BackSelectAreaButtonListener);
		prevPhotoButton = (Button) findViewById(R.id.prevPhotoButton);
		prevPhotoButton.setOnClickListener(PrevPhotoButtonListener);
		nextPhotoButton = (Button) findViewById(R.id.nextPhotoButton);
		nextPhotoButton.setOnClickListener(NextPhotoButtonListener);
		infoPhotoButton = (Button) findViewById(R.id.infoPhotoButton);
		infoPhotoButton.setOnClickListener(InfoPhotoButtonListener);

		myWebView = (WebView)findViewById(R.id.webview);
		myWebView.getSettings().setSupportZoom(true);
		myWebView.setBackgroundColor(0);
		
		//this is a way to reposition the zoom controls (so they don't overlap our buttons), in newer API, there's a better way to do this,
		//but not in API 10
		FrameLayout zoomfl = (FrameLayout)findViewById(R.id.layoutzoom);
		zoomfl.removeAllViews();
		zoomfl.setEnabled(true);
		zoomfl.addView(myWebView.getZoomControls());
		
		queryPhotos();
    }
	
	private boolean hasInternetConnection() { //avoids crashes if user loses connection
		
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null
                && connManager.getActiveNetworkInfo().isAvailable()
                && connManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    
	private OnClickListener BackSelectAreaButtonListener = new OnClickListener() {
        public void onClick(View v) {        	
        	finish(); 
        }        	
    }; 
    
    private OnClickListener PrevPhotoButtonListener = new OnClickListener() {
        public void onClick(View v) {        	
        	showPrevPhoto();        
        }        
    }; 
    
    private OnClickListener NextPhotoButtonListener = new OnClickListener() {
        public void onClick(View v) {         
        	showNextPhoto(); 
        }        	
    }; 
    
    private OnClickListener InfoPhotoButtonListener = new OnClickListener() {
        public void onClick(View v) {        	
        	showPhotoInfo();
        }        
    };
    
    protected void showPhotoInfo() {
    	
    	if (hasInternetConnection()) {
	    	String currentPhotoInfo;
			if(showingPanoramio) {
				currentPhotoInfo = panoramioPhotoList.get(panoramioIndex).getDescription(); //show panoramio photo description
			} else {
				currentPhotoInfo = flickrPhotoList.get(flickerIndex).getDescription(); //show flickr photo description
			}		        
			toast.setText(currentPhotoInfo);
	   	    toast.show();
    	} else {
    		toast.setText("No Internet Connection.");
    		toast.show();
    	}    	
    }
    //Previous next will display next photo from Either Panoramio or Flicker based on conditions  
    @SuppressLint("ParserError")
	protected void showPrevPhoto() { //checks which photo to show, and checks if we should disable the "next" button
    	
    	nextPhotoButton.setEnabled(true);
    	if(showingPanoramio) { //if we're currently showing a panoramio photo
    	
    		if(panoramioIndex >0) {
    			
    			if(panoramioIndex == 1) {  //we're showing photo 1 and we're about to show photo 0
    				prevPhotoButton.setEnabled(false); //disable because there should be no "prev" option when we're in photo 0
    			}
    			panoramioIndex--;
    			displayPhoto(panoramioPhotoList.get(panoramioIndex).getPhotoURL());
    		}
    	} else {
    	 	if(flickerIndex>0) {	
    	 		if(flickerIndex == 1 && panoramioPhotoList == null) { //we're showing photo 1 and we're about to show photo 0
    	 															 //and there are no paranoramio pictures to go back to
    	 			prevPhotoButton.setEnabled(false);
    	 		}
    	 		flickerIndex--;
    	 		displayPhoto(flickrPhotoList.get(flickerIndex).getPhotoURL());
    	 	}
    	 	else if(flickerIndex == 0) { //we're showing photo 0, now we want to show the last panoramio picture
    	 							   //note that this will never happen if the panoramio list is null (in that case, the previous button would have been disabled)
	 			showingPanoramio = true;
	 			panoramioIndex = panoramioPhotoList.size() - 1;
	 			displayPhoto(panoramioPhotoList.get(panoramioIndex).getPhotoURL());
    	 	}	    		
		}   	
    }
    //Move next will display next photo from Either Panoramio or Flicker based on conditions
	protected void showNextPhoto() {
		
    	prevPhotoButton.setEnabled(true); 
    	
		if(showingPanoramio) {
			
			if(panoramioIndex < (panoramioPhotoList.size() - 1)) {
				if(panoramioIndex == (panoramioPhotoList.size() - 2)) { // 2nd last picture (going to show last picture now)
					if(flickrPhotoList==null) {    //this is the last panoramio picture, so if there are no flickr pictures, there should be no "next" button
						nextPhotoButton.setEnabled(false);
					}
				}
				panoramioIndex++;
				displayPhoto(panoramioPhotoList.get(panoramioIndex).getPhotoURL());
				
			} else if(panoramioIndex== (panoramioPhotoList.size() - 1)) { //last panoramio photo, now we want to move to the first flickr photo
				showingPanoramio=false;
				flickerIndex = 0;
    	 		displayPhoto(flickrPhotoList.get(flickerIndex).getPhotoURL());
    	 		if(flickrPhotoList.size() == 1) //if there is only one flickr photo, we shouldn't show the next button anymore
    	 			nextPhotoButton.setEnabled(false); 
			}
			
    	}else {
			if(flickerIndex < (flickrPhotoList.size() - 1)) {
				if(flickerIndex == (flickrPhotoList.size() - 2)) { // 2nd last picture (going to show last picture now)
					nextPhotoButton.setEnabled(false);            //disable the next button
				}
				flickerIndex++;   	
    	 		displayPhoto(flickrPhotoList.get(flickerIndex).getPhotoURL());
	    	}			
		}
    }

	public void queryPhotos() {	
		
		//Check for Internet Connection
    	if(hasInternetConnection()) {    		
	    	panoramioHandler = new PanoramioHandler();
	    	flickrHandler = new FlickrHandler();
	    	//get List of Photos based on Lat lon from Panoramion
	    	panoramioPhotoList = panoramioHandler.getPhotoList(minLongitude, maxLongitude, minLatitude, maxLatitude);
	    	//get List of Photos based on Lat lon from Flicker
		    flickrPhotoList = flickrHandler.getPhotoList(minLongitude, minLatitude, maxLongitude, maxLatitude);
		    //Check for null in case no image were found for that bounding box
	    	
		    if(panoramioPhotoList==null && flickrPhotoList ==null) {
	  	     
		    	toast.setText("No photos found for this area.");
		    	toast.show();
		    	finish();
	    	} else {
	    		if(panoramioPhotoList != null) {
			    	//Display first image from Panoramio if Panoramio is not null
	    			//we will show all panoramio photos, and then all flickr photos
	    			// (we assume the user just wants to see pictures from that area, and that it is not important to him which service the pictures are coming from)
	    			displayPhoto(panoramioPhotoList.getFirst().getPhotoURL());
			    	showingPanoramio=true;
			    	prevPhotoButton.setEnabled(false);
			    	if(panoramioPhotoList.size() == 1)
			    		nextPhotoButton.setEnabled(false);
	    		} else {
	    			//Display Photos from Flicker in case Panoramio is null
	    			displayPhoto(flickrPhotoList.getFirst().getPhotoURL());
			    	showingPanoramio=false;
			    	prevPhotoButton.setEnabled(false);
			    	if(flickrPhotoList.size() == 1)
			    		nextPhotoButton.setEnabled(false);
	    		}
	    	}	    	
    	} else {
    		toast.setText("No Internet Connection. Can't show photos.");
    		toast.show();
    	    finish();
    	}
    }
	
    /**
     * This function will display photo from web to WebView for Panoramio and Flickr.
     * @param url
     */
    protected void displayPhoto(String url) {    	
    	try {	       
    		//Check for Internet Connection
    		if(hasInternetConnection()){
    			myWebView.loadUrl(url);
    			return;
    		}
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}   	
    	toast.setText("No Internet Connection. Can't show photo.");
    	toast.show();
    }
    	
}
