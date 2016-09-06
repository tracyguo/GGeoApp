<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>File Upload</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<form method="post" action="submit" enctype="multipart/form-data">
			<h4 class="control-label">Please upload a csv file for address
				processing:</h4>
			<span> <input type="file" name="file"
				style="visibility: hidden; width: 1px;"
				onchange="$(this).parent().find('span').html($(this).val().replace('C:\\fakepath\\', ''))" />
				<!-- Chrome security returns 'C:\fakepath\'  --> <input
				class="btn btn-primary" type="button" value="Upload File.."
				onclick="$(this).parent().find('input[type=file]').click();" /> <!-- on button click fire the file click event -->
				&nbsp; <span class="badge badge-important"></span>
			</span>
			<h6>
				Not sure about file format? Click <a href="${sampleSpreadSheetURL}"
					target="_blank">Here</a>
			</h6>
			<br /> <input type="submit" value="Submit" class="btn btn-info" />
		</form>
	</div>
</body>
</html>