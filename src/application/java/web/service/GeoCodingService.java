/**
* This class use the Google GeoCoding API to get the coordinates
*
* @author  Tracy Guo
* @since   9/4/2016
*/
package application.java.web.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.JSONObject;

import application.java.config.ConfigLoader;
import application.java.web.model.GAddress;

public class GeoCodingService {

	/**
	 * Get the coordinates based on the full address
	 * 
	 * @param fullAddress
	 *            The full address to be checked
	 * @return GAddress with coordinates information
	 */
	public GAddress getCoordinates(String fullAddress) {

		URL url;
		try {
			String geoURL = ConfigLoader.getInstance().getProperty("geo_url");
			String geoKey = ConfigLoader.getInstance().getProperty("geo_key");

			url = new URL(geoURL + "?address=" + URLEncoder.encode(fullAddress, "UTF-8") + "&key=" + geoKey);

			// read from the URL
			Scanner scan = new Scanner(url.openStream());
			String str = new String();
			while (scan.hasNext())
				str += scan.nextLine();
			scan.close();

			// build a JSON object
			JSONObject obj = new JSONObject(str);
			if (!obj.getString("status").equals("OK")) {
				System.out.println("Invalid Address");
				return null;
			}

			// get the result
			JSONObject res = obj.getJSONArray("results").getJSONObject(0);
			String formattedAddress = res.getString("formatted_address");
			JSONObject loc = res.getJSONObject("geometry").getJSONObject("location");
			Double lat = loc.getDouble("lat");
			Double lng = loc.getDouble("lng");
			System.out.println("formattedAddress=" + formattedAddress + ", lat=" + lat + ", lng=" + lng);

			// set coordinates data into GAddress
			GAddress address = new GAddress();
			address.setFormattedAddress(formattedAddress);
			address.setLat(lat);
			address.setLng(lng);

			return address;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}