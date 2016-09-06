/**
* This class defines basic google spreadsheet operations service
* 
* including authorize, create, read, update
*
* @author  Tracy Guo
* @since   9/4/2016
*/
package application.java.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import application.java.config.ConfigLoader;
import application.java.web.model.GAddress;

public class GSpreadsheetService {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Sheets API Java";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/sheets.googleapis.com-java");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	private static Sheets service;
	private static Drive drive_service;
	private static Credential credential;

	private static final List<String> DRIVE_SCOPES = Arrays.asList(DriveScopes.DRIVE);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
			drive_service = getDriveService();
			service = getSheetsService();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize_drive() throws IOException {
		// Load client secrets.
		InputStream in = GSpreadsheetService.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, DRIVE_SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	public static Drive getDriveService() throws IOException {
		credential = authorize_drive();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * 
	 * @return an authorized Sheets API client service
	 * @throws IOException
	 */
	public static Sheets getSheetsService() throws IOException {
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

	/**
	 * Get the map between spreadsheet column to index
	 * 
	 * @return the sheet column to index map
	 */
	public static Map<String, Integer> getSheetColumnToIndexMap() {
		Map<String, Integer> sheetColumnToIndexMap = new HashMap<String, Integer>();
		sheetColumnToIndexMap.put("streetaddress",
				Integer.valueOf(ConfigLoader.getInstance().getProperty("streetaddress")));
		sheetColumnToIndexMap.put("city", Integer.valueOf(ConfigLoader.getInstance().getProperty("city")));
		sheetColumnToIndexMap.put("postalcode", Integer.valueOf(ConfigLoader.getInstance().getProperty("postalcode")));
		sheetColumnToIndexMap.put("state", Integer.valueOf(ConfigLoader.getInstance().getProperty("state")));
		sheetColumnToIndexMap.put("country", Integer.valueOf(ConfigLoader.getInstance().getProperty("country")));
		return sheetColumnToIndexMap;
	}

	/**
	 * Read the spreadsheet and process the input information
	 * 
	 * @param spreadsheetId
	 *            The ID of the spreadsheet
	 * @param range
	 *            The range of data to be read
	 * @return list of GAddress data
	 */
	public List<GAddress> readSpreadsheet(String spreadsheetId, String range) {
		try {
			// get spreadsheet data
			ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
			List<List<Object>> values = response.getValues();

			if (values == null || values.size() == 0) {
				System.out.println("No spreadsheet data found.");
				return null;
			} else {
				List<GAddress> addressList = new ArrayList<GAddress>();

				// get the map between column and index
				Map<String, Integer> sheetColumnToIndexMap = getSheetColumnToIndexMap();
				System.out.println("*****Reading spreadsheet*****");

				for (List<Object> row : values) {
					// set GAddress data and put into address list
					GAddress gAddress = new GAddress();
					gAddress.setStreetAddress(row.get(sheetColumnToIndexMap.get("streetaddress")).toString());
					gAddress.setCity(row.get(sheetColumnToIndexMap.get("city")).toString());
					gAddress.setState(row.get(sheetColumnToIndexMap.get("state")).toString());
					gAddress.setPostalCode(row.get(sheetColumnToIndexMap.get("postalcode")).toString());
					gAddress.setCountry(row.get(sheetColumnToIndexMap.get("country")).toString());

					System.out.println("gAddress=" + gAddress);

					addressList.add(gAddress);
				}

				System.out.println("addressList=" + addressList);
				return addressList;
			}

		} catch (IOException e) {
			System.out.println("Error read spreadsheet with IOException");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			System.out.println("Error read spreadsheet");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the spreadsheet with the data provided
	 * 
	 * @param spreadsheetId
	 *            The ID of the spreadsheet to be updated
	 * @param dataList
	 *            The list of data to be updated
	 * @param range
	 *            The range of spreadsheet to be updated
	 */
	public void updateSpreadsheet(String spreadsheetId, List<List<Object>> dataList, String range) {
		try {
			System.out.println("*****Updating spreadsheet*****");

			// update the spreadsheet with provided data
			BatchUpdateValuesRequest batchRequest = new BatchUpdateValuesRequest();
			batchRequest.setValueInputOption("RAW");
			ValueRange valueRange = new ValueRange();
			valueRange.setRange(range);
			valueRange.setValues(dataList);
			List<ValueRange> valueRangeList = new ArrayList<ValueRange>();
			valueRangeList.add(valueRange);
			batchRequest.setData(valueRangeList);
			service.spreadsheets().values().batchUpdate(spreadsheetId, batchRequest).execute();
		} catch (IOException e) {
			System.out.println("Error update spreadsheet with IOException");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error update spreadsheet");
			e.printStackTrace();
		}
	}

	/**
	 * Create the spreadsheet with the data provided
	 * 
	 * @param spreadsheetName
	 *            The name of the spreadsheet to be created
	 * @param dataList
	 *            The data of the spreadsheet to be created
	 * @return the newly created spreadsheet ID
	 */
	public String createSpreadsheetWithValue(String spreadsheetName, List<List<Object>> dataList) {
		try {
			Spreadsheet newSpreadsheet = new Spreadsheet();

			// create the spreadsheet
			SpreadsheetProperties properties = new SpreadsheetProperties();
			properties.setTitle(spreadsheetName);
			newSpreadsheet.setProperties(properties);
			Spreadsheet spreadsheet = service.spreadsheets().create(newSpreadsheet).execute();
			System.out.println("theResponse=" + spreadsheet);

			// set permission for the spreadsheet
			String spreadsheetId = spreadsheet.getSpreadsheetId();
			insertPermission(drive_service, spreadsheetId);

			// set data to the spreadsheet
			GSpreadsheetService sheetService = new GSpreadsheetService();
			sheetService.updateSpreadsheet(spreadsheetId, dataList, "Sheet1");
			return spreadsheetId;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Set Google Drive permission
	 * 
	 * @param service
	 *            Google Drive service
	 * @param fileId
	 *            The ID of the spreadsheet
	 * @return google drive permission
	 */
	private Permission insertPermission(Drive service, String fileId) throws Exception {
		// set permission for the file
		Permission newPermission = new Permission();
		newPermission.setType("anyone");
		newPermission.setRole("reader");
		newPermission.setValue("");
		newPermission.setWithLink(true);
		return service.permissions().insert(fileId, newPermission).execute();
	}

}