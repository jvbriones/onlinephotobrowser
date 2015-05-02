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

package team.photobrowser.panoramio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PanoramioHandler {  //this class uses the JSON Parser
	
	//Url for Panoramip API
	private static final String panoramioURL = "//www.panoramio.com/map/get_panoramas.php?order=popularity&set=public&from=0&to=100&miny=%f&minx=%f&maxy=%f&maxx=%f";
	
	/**
	 * This function will return the link list of Photos for Specific Maxlat, MinLat, Maxlon and MinLon values
	 * 
	 * @param minLongitude
	 * @param maxLongitude
	 * @param minLatitude
	 * @param maxLatitude
	 * 
	 * @return
	 */
	public LinkedList<PanoramioPhotoInfo> getPhotoList(float minLongitude,
			                                     float maxLongitude, float minLatitude, float maxLatitude) {
		String url = panoramioURL;
		//Format the string url and add lat lon values
		url = String.format(url, minLatitude, minLongitude, maxLatitude,
				maxLongitude);
		// url="//www.panoramio.com/map/get_panoramas.php?order=popularity&set=public&from=0&to=20&minx=-180&miny=-90&maxx=180&maxy=90&size=medium";
		
		try {			
			URI uri = new URI("http", url, null);
			HttpGet get = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			//Convert Entity content to String
			String str = convertStreamToString(entity.getContent());
			//Call for JSon Object and parse the string
			JSONObject json = new JSONObject(str);
			//Return the linked list with photo information from Panoramio API
			
			return parse(json);
		} catch (Exception e) {

		}
		return null;
	}
	
	/**
	 * Json Parser is used in the following function to parse the Panoromio API response 
	 * and save all the information related to photos in a linkedlist
	 * 
	 * @param json
	 * @return
	 */
	private LinkedList<PanoramioPhotoInfo> parse(JSONObject json) {
		
		LinkedList<PanoramioPhotoInfo> photoInformations = new LinkedList<PanoramioPhotoInfo>();
		try {			
			JSONArray array = json.getJSONArray("photos");
			int count = array.length();

			for (int i = 0; i < count; i++) {
				
				JSONObject obj = array.getJSONObject(i);
				PanoramioPhotoInfo photoInfo = new PanoramioPhotoInfo();
				String title = obj.getString("photo_title");
				String owner = obj.getString("owner_name");

				String description = "";
				if (!title.isEmpty())
					description += "Title: " + title + '\n';
				if (!owner.isEmpty())
					description += "Author: " + owner;

				photoInfo.setDescription(description);
				photoInfo.setPhotoURL(obj.getString("photo_file_url"));
				photoInformations.add(photoInfo);
			}

		} catch (JSONException e) {
			return null;
		}
		if (!photoInformations.isEmpty())
			return photoInformations;
		else
			return null;
	}
	
	/**
	 * This function is used to convert the Stream received from Panoramio API to a string
	 * 
	 * @param is input stream
	 * @return
	 */
	private String convertStreamToString(InputStream is) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
