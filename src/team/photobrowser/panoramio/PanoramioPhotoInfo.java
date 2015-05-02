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

public class PanoramioPhotoInfo {

	String Description; //Description of Photo Owner Information
	String photoUrl; //Photo Url
	
	public String getDescription() {
  	  return "From Panoramio.\n" +  this.Description;
    }
	
    public String getPhotoURL() {
  	  return this.photoUrl;
    }
    
    public void setDescription(String Description) {
  	  this.Description=Description;    			  
    }
    
    public void setPhotoURL(String photoUrl) {
  	  this.photoUrl=photoUrl;
    }
	
}
