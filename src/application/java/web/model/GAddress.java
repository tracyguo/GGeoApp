/**
* Model Class 
* Name: GAddress
* Description: An google spreadsheet entry which includes address info
*
* @author  Tracy Guo
* @since   9/4/2016
*/
package application.java.web.model;

public class GAddress {

	private String streetAddress;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String formattedAddress;
	private double lat;
	private double lng;

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "GAddress [streetAddress=" + streetAddress + ", city=" + city + ", state=" + state + ", postalCode="
				+ postalCode + ", country=" + country + ", formattedAddress=" + formattedAddress + ", lat=" + lat
				+ ", lng=" + lng + "]";
	}

}
