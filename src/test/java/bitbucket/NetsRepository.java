package bitbucket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.LIMIDType;
import org.openmarkov.core.model.network.type.MPADType;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.model.network.type.POMDPType;


/**
 * This class gets the networks avaible on the bitbucket repository
 *
 * @author Jorge Pérez Martín
 */
public class NetsRepository {

	/**
	 * Full path to the networks repository in bitbucket 
	 */
	private final String rootNetworksDirectory = "https://bitbucket.org/cisiad/org.probmodelxml.networks/raw/default/";
	/**
	 * Bitbucket's API with the JSON in which we have all the files in the directory
	 */
	private final String bitbucketNetworksURL = "https://bitbucket.org/!api/1.0/repositories/cisiad/org.probmodelxml.networks/directory";
	/**
	 * Constant for Baysian Networks
	 */
	public static final String NETWORK_BN = "bn";
	/**
	 * Constant for DAN Networks
	 */
	public static final String NETWORK_DAN = "dan";
	/**
	 * Constant for Influence Diagram Networks
	 */
	public static final String NETWORK_ID = "id";
	/**
	 * Constant for Limids Networks
	 */
	public static final String NETWORK_LIMIDS = "limids";
	/**
	 * Constant for MPAD Networks
	 */
	public static final String NETWORK_MPAD = "mpad";
	/**
	 * Constant for POMDP Networks
	 */
	public static final String NETWORK_POMDP = "pomdp";

	/**
	 * Method to obtain the complete list of URL of all networks in the repository
	 * @return URL of the networks
	 */
    public List<URL> getNetworks(){
		return getNetworks("");
    }

    
    /**
     * Method to obtain filtered networks in the repository by it network type
     * @param networkType NetWorkType of the net
     * @return List of filtered url networks
     */
    public List<URL> getNetworks(NetworkType networkType){
    	if(networkType.equals(BayesianNetworkType.getUniqueInstance())){
    		return getNetworks(NETWORK_BN);
    	}else if(networkType.equals(DecisionAnalysisNetworkType.getUniqueInstance())){
    		return getNetworks(NETWORK_DAN);
    	}else if(networkType.equals(InfluenceDiagramType.getUniqueInstance())){
    		return getNetworks(NETWORK_ID);
    	}else if(networkType.equals(LIMIDType.getUniqueInstance())){
    		return getNetworks(NETWORK_LIMIDS);
    	}else if(networkType.equals(MPADType.getUniqueInstance())){
    		return getNetworks(NETWORK_MPAD);
    	}else if(networkType.equals(POMDPType.getUniqueInstance())){
    		return getNetworks(NETWORK_POMDP);
    	}
    	return null;
    }
    
    /**
     * Method to obtain filtered networks in the repository by it network type
     * @param networkType constant to define the filter. Use the static constants defined in this class 
     * @return List of filtered url networks
     */
    private List<URL> getNetworks(String networkType){
    	List<URL> networksURL = new ArrayList<URL>();
    	JSONObject bitbucketDirectoryJSON = null;

    	// Read the JSON object given by the API of bitbucket
		try {
			bitbucketDirectoryJSON = readJsonFromUrl(bitbucketNetworksURL);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

    	// Get the array of files in the directory
    	JSONArray networksList = bitbucketDirectoryJSON.getJSONArray("values"); 
    	for(int i = 0; i < networksList.length(); i++){
    		String lastURLString = (String) networksList.get(i);
        	// For each file encountered we must know if the file is a valid network with the .pgmx extension
    		if(lastURLString.endsWith(".pgmx")){
    			// If the network is inside a directory, the path must contain the '/' symbol. The first part
    			// of this string will be the type of the network. If the network type is equals to the 
    			// filter or the filter is empty, we must recover this URL.
    			if((lastURLString.indexOf("/") != -1) &&
    					((lastURLString.substring(0, lastURLString.indexOf("/")).equals(networkType)) || (networkType.isEmpty()))){
    				// We get the url from that file and add it to the list
    				try {
						URL url = new URL(rootNetworksDirectory + lastURLString);
						networksURL.add(url);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
    			}
    		}
    	}
    	
    	return networksURL;
    }
    

    /**
     * This method reads a file and returns the file in an only one string format
     *
     * @param reader reader
     * @return String formatted file
     * @throws java.io.IOException If a read error occurred
     */
    private String readAll(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int position;
        while ((position = reader.read()) != -1) {
            stringBuilder.append((char) position);
        }
        return stringBuilder.toString();
    }

    /**
     * This methods reads a JSON from an URL
     *
     * @param url URL in which the file is hosted
     * @return JSON object
     * @throws java.io.IOException Read exception
     * @throws org.json.JSONException Bad JSON file exception
     */
    private JSONObject readJsonFromUrl(String url) throws IOException {
        
    	InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(reader);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            inputStream.close();
        }
    }

}
