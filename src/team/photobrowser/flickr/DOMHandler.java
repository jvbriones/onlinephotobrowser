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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMHandler {
	
	DocumentBuilderFactory mDOMFactory;
	DocumentBuilder mDOMbuilder;
	private final String flickrApiKey = "af132e1981a446a441e273025b302f64"; //http://www.flickr.com/services/api/misc.api_keys.html
	
	public DOMHandler() {	
		
		try {
			mDOMFactory = DocumentBuilderFactory.newInstance(); 
			mDOMbuilder = mDOMFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public String readPhotoInfo(URL photoUrl) { //will receive a URL of the type
											   //http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
		//get these informations from the URL
		String idAndSecret = photoUrl.toString().substring(photoUrl.toString().lastIndexOf('/')+1);		
		String photoId = idAndSecret.substring(0,idAndSecret.indexOf('_'));
		String secret = idAndSecret.substring(idAndSecret.indexOf('_'), idAndSecret.indexOf('.'));
		
		//make a query
		URL queryUrl = null;
		try {
			queryUrl = new URL("http://api.flickr.com/services/rest/?method=flickr.photos.getInfo&api_key="+
							  flickrApiKey + "&photo_id=" + photoId + "&secret=" + secret + "&format=rest");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		String title = "", authorUsername = "", authorRealName="";
		Document doc = null;
		
		try {			
			doc = mDOMbuilder.parse(new InputSource(queryUrl.openStream()));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(doc!=null) { //browse the tree and get the username, realname and title
			
			Element element = doc.getDocumentElement(); //root
			Node photo = element.getChildNodes().item(1);
			NodeList properties = photo.getChildNodes();
			
			for (int i = 0; i < properties.getLength(); i++) { 				
	            String nodeName = properties.item(i).getNodeName();	
	            
	            if (nodeName.equalsIgnoreCase("owner")) {	            	
	            	NamedNodeMap attributes = properties.item(i).getAttributes();
	            	
	            	for (int j = 0; j < attributes.getLength(); j++){	            		
	            	        Node att = attributes.item(j);
	            	        if(att.getNodeName().equalsIgnoreCase("username")){
	            	        	authorUsername = att.getNodeValue();
	            	        }
	            	        if(att.getNodeName().equalsIgnoreCase("realname")){
	            	        	authorRealName = att.getNodeValue();
	            	        }
	            	}	            	
	            } else if(nodeName.equalsIgnoreCase("title")) {
	            	title = properties.item(i).getFirstChild().getNodeValue(); //first child is the text field of "title"
	            }
			}
		}
		
		String description = ""; 
		if(!title.isEmpty()) 
			description += "Title: " + title + '\n'; //only add this line if the title is not empty
		if(!authorUsername.isEmpty())
			description += "Author Username: "+ authorUsername + '\n';
		if(!authorRealName.isEmpty())
			description += "Author Real Name: " + authorRealName;
		
		return description;//return a string containing the photo information
	}

}
	            