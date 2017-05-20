package group1_getData.group1_getData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
    	Iterator<Object> iterator = products.iterator();
		
		while(iterator.hasNext()){
			productsNumber++;
			JSONObject product = (JSONObject) iterator.next();
			
			HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 

			try {

			    HttpPost request = new HttpPost("http://localhost:8080/products");
			    StringEntity params =new StringEntity(getProductAsJSON(product).toString(), "UTF-8");
			    request.addHeader("content-type", "application/json;charset=UTF-8");
			    request.setEntity(params);
			    httpClient.execute(request);
			}catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
    }
    
    private JSONObject getProductAsJSON(JSONObject obj){
    	JSONObject product = new JSONObject();
    	JSONObject nutrient;
    	
    	product.put("name", (obj.getJSONObject("name_translations").has("fr") ? obj.getJSONObject("name_translations").get("fr") : ""));
    	product.put("ingredients", (obj.getJSONObject("ingredients_translations").has("fr") ? obj.getJSONObject("ingredients_translations").get("fr") : ""));
    	product.put("quantity", (obj.has("quantity") ? obj.get("quantity") : 0));
    	product.put("unit", (obj.has("unit") ? obj.get("unit") : ""));
    	product.put("portion_quantity", (obj.has("portion_quantity") ? obj.get("portion_quantity") : 0));
    	product.put("portion_unit", (obj.has("portion_unit") ? obj.get("portion_unit") : ""));
    	
    	if(obj.has("nutrients")){
    		JSONObject nutrients = obj.getJSONObject("nutrients");
    		
    		for (Object key : nutrients.keySet()) {
    	        // get key and value
    	        String keyName = (String)key;
    	        JSONObject value = (JSONObject) nutrients.get(keyName);

    	        nutrient = new JSONObject();
    	        nutrient.put("name", (value.getJSONObject("name_translations").has("fr") ? value.getJSONObject("name_translations").get("fr") : ""));
    	        nutrient.put("unit", (value.has("unit") ? value.get("unit") : ""));
    	        nutrient.put("perHundred", (value.has("per_hundred") ? value.get("per_hundred") : 0));
    	        nutrient.put("perPortion", (value.has("per_portion") ? value.get("per_portion") : 0));
    	        nutrient.put("perDay", (value.has("per_day") ? value.get("per_day") : 0));

    	        product.append("nutrients", nutrient);
    	    }
    	}
    	
    	return product;
    }
}