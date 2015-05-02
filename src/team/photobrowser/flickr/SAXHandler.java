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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class SAXHandler extends DefaultHandler {
	
	protected LinkedList<FlickrPhotoInfo> photoURLList;
	protected SAXParserFactory mSAXParserFactory;
	protected SAXParser mSAXParser;
	protected XMLReader mXMLReader;
	
	private final String flickrApiKey = "af132e1981a446a441e273025b302f64"; //http://www.flickr.com/services/api/misc.api_keys.html
	private final String minUploadDate = "2011-01-01";
	private final int accuracy = 1;
	private final int photosPerPage = 100;
	private DOMHandler domHandler;
	
	
	public SAXHandler (DOMHandler domHandler) {
		
		this.domHandler = domHandler;
		mSAXParserFactory = SAXParserFactory.newInstance();
		
		try {			
			mSAXParser = mSAXParserFactory.newSAXParser();
			mXMLReader = mSAXParser.getXMLReader();
			mXMLReader.setContentHandler(this);	
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}		
    }
	
	public LinkedList<FlickrPhotoInfo> readPhotoUrlList (float minLongitude, float minLatitude, float maxLongitude, float maxLatitude) {
		URL queryUrl = null;
//http://api.flickr.com/services/rest?method=flickr.photos.search&api_key=af132e1981a446a441e273025b302f64&min_upload_date=2011-01-1&accuracy=1&per_page=100&bbox=9.00,48.4,9.25,48.90

		String urlString= "http://api.flickr.com/services/rest?method=flickr.photos.search&api_key="
						+ flickrApiKey + "&min_upload_date=" + minUploadDate + "&accuracy=" + accuracy
						+ "&per_page" + photosPerPage + "&bbox=" + minLongitude + "," + minLatitude + ","
						+ maxLongitude + "," + maxLatitude;  //make a query with a bounding box based on the latitude and longitude parameters
		try {			
			queryUrl = new URL(urlString);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		photoURLList = new LinkedList<FlickrPhotoInfo>();
		
		try {			
			mXMLReader.parse(new InputSource(queryUrl.openStream()));
			
		} catch (Exception e){	
			//e.printStackTrace();
		}
		if (!photoURLList.isEmpty())
			return photoURLList; //return a list of FlickPhotoInfo (containing the photo's URL)
		
		return null; //return null if list is empty
	}
	
	public void startDocument() throws SAXException
    { 
		
    }
	
	public void endDocument() throws SAXException
    {
	
    }

	public void startElement (String uri, String name, String qName, Attributes atts) {
		
		if (name.trim().equals("photo")) { //if this is the photo element, get its attributes
			
			SAXPhotoInfo photo = new SAXPhotoInfo();
			photo.farmId = atts.getValue("farm");
			photo.id = atts.getValue("id");
			photo.secret = atts.getValue("secret");
			photo.serverId = atts.getValue("server");
			FlickrPhotoInfo flickrPhotoInfo = new FlickrPhotoInfo(domHandler, SAXPhotoInfo.getURL(photo));
			photoURLList.add(flickrPhotoInfo); //add this flickrPhotoInfo (containing the photo URL) to the list
		}
	}

	public void endElement(String uri, String name, String qName) throws SAXException 
	{
	
	}

} // end SAXHandler class


/**
 * this class saves the photo's attributes and then generates 
 * its URL (in the static function)
 * 
 */

class SAXPhotoInfo { 
	
	public String farmId, serverId, id, secret;
	
	public SAXPhotoInfo()
	{
		
	}
		
	public static URL getURL (SAXPhotoInfo photo) {
		URL url = null;
		//http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
		String string =  "http://farm" + photo.farmId + ".staticflickr.com/" + photo.serverId
				+"/"+photo.id+"_"+photo.secret+".jpg";
		try {
			url = new URL(string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
} // end SAXPhotoInfo class

