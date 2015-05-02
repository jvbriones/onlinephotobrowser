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


import java.util.LinkedList;

import android.annotation.SuppressLint;

/**
 * This class will use the SAX and DOM parsers
 *
 */
@SuppressLint({ "ParserError", "ParserError" })
public class FlickrHandler { 
	
	SAXHandler saxHandler;
	DOMHandler domHandler;
	
	public FlickrHandler() {
		domHandler = new DOMHandler();
		saxHandler = new SAXHandler(domHandler);
	}
	//calls the SAX Parser and returns a list of FlickrPhotoInfo elements (containing the photos' URL)
	//it gives the domHandler as a parameter to avoid having to create multiple instances of it.
	public LinkedList<FlickrPhotoInfo> getPhotoList(float minLongitude, float minLatitude, float maxLongitude, float maxLatitude) {
		return saxHandler.readPhotoUrlList(minLongitude, minLatitude, maxLongitude, maxLatitude);
	}
	
}
