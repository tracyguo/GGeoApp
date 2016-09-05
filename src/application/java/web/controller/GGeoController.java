/**
* This is the controller for the file uploader
* 
* @author  Tracy Guo
* @since   9/4/2016
*/
package application.java.web.controller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import application.java.config.ConfigLoader;
import application.java.web.service.GAddressProcessorService;
import au.com.bytecode.opencsv.CSVReader;


@Controller
public class GGeoController {

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public void getFile(ModelMap model) {
		model.addAttribute("sampleSpreadSheetURL", ConfigLoader.getInstance().getProperty("spreadsheet_base_url")+ConfigLoader.getInstance().getProperty("spreadsheet_id"));
		System.out.println("In GGeopApp");
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public String uploadFileHandler(@RequestParam("file") MultipartFile file, ModelMap model) {
		System.out.println("File Submitted");

		try {
			if (!file.isEmpty()) {
				List<List<Object>> dataList = new ArrayList<List<Object>>();
				String[] nextLine; 
				CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
				while ((nextLine = reader.readNext()) != null) {
					ArrayList<Object> list = new ArrayList<Object>();
					for (int j = 0; j < nextLine.length; j++) {
						list.add(nextLine[j]);
					}
					dataList.add(list);
				}
			    reader.close();
				String spreadsheetId = GAddressProcessorService.processUploadedFile(file.getOriginalFilename(), dataList);
				model.addAttribute("spreadsheetURL", ConfigLoader.getInstance().getProperty("spreadsheet_base_url")+spreadsheetId);
				if(spreadsheetId == null){
					return "error";
				}
			}else {
				return "error";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

		return "success";
	}


}
