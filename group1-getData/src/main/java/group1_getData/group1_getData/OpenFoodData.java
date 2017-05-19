package group1_getData.group1_getData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenFoodData {
	private int pageSize = 200;
	private int pageNumber = 1;
	private int productsNumber = 0;
	
	public void getData(){
    	
		try {
			// Required objects
			JSONObject j;
			URL url;			
			URIBuilder uri;
			HttpURLConnection con;			
			BufferedReader in;
			boolean remainingProducts = true;
			
			// Loop to get all products
			while(remainingProducts){
				// Create uri with parameters
				uri = new URIBuilder("https://www.openfood.ch/api/v3/products");
				uri.addParameter("page[size]", String.valueOf(pageSize));
				uri.addParameter("page[number]", String.valueOf(pageNumber));
				
				// Generate URL
				url = uri.build().toURL();
				
				// Open the connection and add required request properties and set the method to "GET"
				con = (HttpURLConnection) url.openConnection();
				con.setDoOutput(true);
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Authorization", "Token token=\"089910995ea858abe18aec3a75ff95a8\"");
				con.setRequestMethod("GET");
				
				// Get the data from the server
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				
				// Parse server answers into JSON
				j = new JSONObject(in.readLine());
				
				// Close bufferedreader
				in.close();
				
				// Get data into products array
				JSONArray products = (JSONArray) j.get("data");
				
				// If there is products => add to db and if not, stop the queries
				if(products.length() > 0){
					addDataToDB(products);
					pageNumber++;
				}
				else{
					remainingProducts = false;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}
    
    private void addDataToDB(JSONArray products){
    	// TODO: add data to db when webservice is done!
    	// TODO: remove the displaying of all products
    	Iterator<Object> iterator = products.iterator();
		
		while(iterator.hasNext()){
			productsNumber++;
			JSONObject product = (JSONObject) iterator.next();
			System.out.println(product.toString());
			/*System.out.println("==============");
			System.out.println("PRODUIT " + productsNumber);
			System.out.println("==============");			
			
			//System.out.println(product.toString());
			if(product.getJSONObject("name_translations").has("fr"))
				System.out.println(product.getJSONObject("name_translations").getString("fr"));
			if(product.getJSONObject("ingredients_translations").has("fr"))
				System.out.println(product.getJSONObject("ingredients_translations").getString("fr"));
			System.out.println(product.getInt("quantity"));
			System.out.println(product.getString("unit"));
			System.out.println(product.getInt("portion_quantity"));
			System.out.println(product.getString("portion_unit"));*/
		}
    }

}
