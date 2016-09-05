<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
       pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Upload</title>
</head>
<body>
<form method="post" action="submit" enctype="multipart/form-data">
Select file to upload:
<input type="file" name="file" id="fileChooser"/><br/><br/>
Not sure about file format? Click <a href="${sampleSpreadSheetURL}" target="_blank">Here</a><br/><br/>
<input type="submit" value="Submit" />
</form>
</body>
</html>