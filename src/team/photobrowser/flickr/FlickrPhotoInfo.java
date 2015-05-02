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

package team.photobrowser.flickr;

import android.annotation.SuppressLint;
import java.net.URL;


@SuppressLint("ParserError")
public class FlickrPhotoInfo {
	
	private DOMHandler domHandler;
	private URL photoUrl;
	
	public FlickrPhotoInfo (DOMHandler domHandler, URL photoUrl) { //received the domHandler as a parameter
																 //to avoid creating multiple instances of it
		this.domHandler = domHandler;
		this.photoUrl = photoUrl;
	}
	
	public String getDescription() { //the domHandler only parses when the getDescription function is called
		                            //this is more efficient than parsing all of the photos' informations in the beginning,
		                            //because the user might never see some of the photos' information (if he doesn't click the information button)
		return "From Flickr.\n"  +domHandler.readPhotoInfo(photoUrl);
	}
	
	public String getPhotoURL() {
		
  	  return this.photoUrl.toString();
    }
}
