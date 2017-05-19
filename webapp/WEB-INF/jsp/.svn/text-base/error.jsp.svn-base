<%@ page isErrorPage="true" import="java.io.*"%>

<!DOCTYPE html>
<html>
	<head>		
		<title>Error Page</title>
	</head>
	<body>
		<div style="font-family: arial; font-size: 14px; font-weight: bold; color: red;">
		System error, please contact administrator!
		<a href=".">Go to site</a>
		<%
			Throwable e = (Throwable) request.getAttribute("operatorException");
			if (e != null) {
				out.println("<!--");
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				out.println(sw);
				out.println("-->");
			}
		%>
		</div>
	</body>
</html>