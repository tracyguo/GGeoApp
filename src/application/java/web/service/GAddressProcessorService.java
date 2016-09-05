/**
* This service class process address data from google spreadsheet
*
* @author  Tracy Guo
* @since   9/4/2016
*/
package application.java.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.java.config.ConfigLoader;
import application.java.web.model.GAddress;

public class GAddressProcessorService {

	public static void processAddress(String spreadsheetId) {
		try {
			System.out.println("**********BEGIN PROCESSING ADDRESS**********");
			GSpreadsheetService spreadsheetService = new GSpreadsheetService();

			List<GAddress> addressList = spreadsheetService.readSpreadsheet(spreadsheetId, "Sheet1!A2:F");
			List<List<Object>> addressUpdateLists = new ArrayList<List<Object>>();
			System.out.println("*****Getting coordinates*****");
			for (GAddress address : addressList) {
				String fullAddress = address.getStreetAddress() + "," + address.getCity() + "," + address.getState()
						+ "," + address.getPostalCode() + "," + address.getCountry();

				GeoCodingService gCodeService = new GeoCodingService();
				GAddress geoAddress = gCodeService.getCoordinates(fullAddress);

				ArrayList<Object> list = new ArrayList<Object>();

				if (geoAddress == null) {
					list.add("Invalid Address");
					list.add("N/A");
					list.add("N/A");
				} else {
					list.add(geoAddress.getFormattedAddress());
					list.add(geoAddress.getLat());
					list.add(geoAddress.getLng());
				}

				addressUpdateLists.add(list);

			}

			spreadsheetService.updateSpreadsheet(spreadsheetId, addressUpdateLists, "Sheet1!G2:I");

			System.out.println("**********FINISHED PROCESSING ADDRESS**********");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String processUploadedFile(String fileName, List<List<Object>> dataList){
		GSpreadsheetService spreadsheetService = new GSpreadsheetService();

		
		if (GAddressProcessorService.dataValidator(dataList)) {
			String spreadsheetId = spreadsheetService.createSpreadsheetWithValue(fileName, dataList);
			GAddressProcessorService.processAddress(spreadsheetId);
			return spreadsheetId;
		} else {
			System.out.println("Error: Data Invalid");
			return null;
		}
	}

	public static boolean dataValidator(List<List<Object>> dataList) {
		for (int i = 0; i < dataList.size(); i++) {
			List<Object> valueList = dataList.get(i);
			if (i == 0) {
				// must match column names in config file
				String columnsNameStr = ConfigLoader.getInstance().getProperty("columns");
				List<String> columnList = new ArrayList<String>(Arrays.asList(columnsNameStr.split(",")));
				boolean listIsSame = columnList.equals(valueList);
				if (!listIsSame) {
					return false;
				}
			}/* else {
				if (valueList.size() != Integer.valueOf(ConfigLoader.getInstance().getProperty("columnSize"))) {
					return false;
				}
			}*/

		}
		return true;
	}
	
	public static void main(String[] args) {
		String spreadsheetId = ConfigLoader.getInstance().getProperty("spreadsheet_id");
		processAddress(spreadsheetId);
	}
}