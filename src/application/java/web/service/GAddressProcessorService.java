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

	/**
	 * Process the spreadsheet, includes read, get coordinates and update
	 * spreadsheet
	 * 
	 * @param spreadsheetId
	 *            The ID of the spreadsheet
	 */
	public static void processAddress(String spreadsheetId) {
		try {
			System.out.println("**********BEGIN PROCESSING ADDRESS**********");
			GSpreadsheetService spreadsheetService = new GSpreadsheetService();

			// read the spreadsheet
			List<GAddress> addressList = spreadsheetService.readSpreadsheet(spreadsheetId,
					ConfigLoader.getInstance().getProperty("spreadsheet_read_range"));
			List<List<Object>> addressUpdateLists = new ArrayList<List<Object>>();
			System.out.println("*****Getting coordinates*****");
			for (GAddress address : addressList) {
				String fullAddress = address.getStreetAddress() + "," + address.getCity() + "," + address.getState()
						+ "," + address.getPostalCode() + "," + address.getCountry();

				// get the coordinated information based on the full address
				GeoCodingService gCodeService = new GeoCodingService();
				GAddress geoAddress = gCodeService.getCoordinates(fullAddress);

				ArrayList<Object> list = new ArrayList<Object>();

				// if GAddress is blank, output invalid address
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

			// update the spreadsheet with coordinates info
			spreadsheetService.updateSpreadsheet(spreadsheetId, addressUpdateLists,
					ConfigLoader.getInstance().getProperty("spreadsheet_update_range"));

			System.out.println("**********FINISHED PROCESSING ADDRESS**********");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process the uploaded file, will create new spreadsheet and process the
	 * address
	 * 
	 * @param fileName
	 *            The name of the uploaded file
	 * @param dataList
	 *            The list of data to be processed
	 * @return the newly created spreadsheet ID
	 */
	public static String processUploadedFile(String fileName, List<List<Object>> dataList) {
		GSpreadsheetService spreadsheetService = new GSpreadsheetService();

		// if data list is valid, process the uploaded file - includes create,
		// read and update file
		if (GAddressProcessorService.dataValidator(dataList)) {
			String spreadsheetId = spreadsheetService.createSpreadsheetWithValue(fileName, dataList);
			GAddressProcessorService.processAddress(spreadsheetId);
			return spreadsheetId;
		} else {
			System.out.println("Error: Data Invalid");
			return null;
		}
	}

	/**
	 * Validate whether the data input is valid - column has to match
	 * 
	 * @param dataList
	 *            The list of data to be validated
	 * @return whether the data is valid
	 */
	public static boolean dataValidator(List<List<Object>> dataList) {
		for (int i = 0; i < dataList.size(); i++) {
			List<Object> valueList = dataList.get(i);
			if (i == 0) {
				// the input data list columns must match column names in config
				// file
				String columnsNameStr = ConfigLoader.getInstance().getProperty("columns");
				List<String> columnList = new ArrayList<String>(Arrays.asList(columnsNameStr.split(",")));
				boolean listIsSame = columnList.equals(valueList);
				if (!listIsSame) {
					return false;
				}
			}
		}
		return true;
	}

	public static void main(String[] args) {
		String spreadsheetId = ConfigLoader.getInstance().getProperty("spreadsheet_id");
		processAddress(spreadsheetId);
	}
}