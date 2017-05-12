package group1_getData.group1_getData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.json.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        getData();
        
    }
    
    private static void getData(){
		String res = null;
		
		JSONObject j;
		
		try {
			URL url = new URL("https://www.openfood.ch/api/v3/products");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Token token=\"089910995ea858abe18aec3a75ff95a8\"");
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			j = new JSONObject(in.readLine());
			
			in.close();
			
			JSONArray products = (JSONArray) j.get("data");
			
			Iterator<Object> iterator = products.iterator();
			
			while(iterator.hasNext()){
				System.out.println("==============");
				System.out.println("PRODUIT");
				System.out.println("==============");
				JSONObject product = (JSONObject) iterator.next();
				
				//System.out.println(product.toString());
				if(product.getJSONObject("name_translations").has("fr"))
					System.out.println(product.getJSONObject("name_translations").getString("fr"));
				if(product.getJSONObject("ingredients_translations").has("fr"))
					System.out.println(product.getJSONObject("ingredients_translations").getString("fr"));
				System.out.println(product.getInt("quantity"));
				System.out.println(product.getString("unit"));
				System.out.println(product.getInt("portion_quantity"));
				System.out.println(product.getString("portion_unit"));
			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
