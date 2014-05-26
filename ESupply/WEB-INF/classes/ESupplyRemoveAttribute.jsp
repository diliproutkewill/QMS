<%
String valueList            = request.getParameter("valueList");

 try
 {
	//System.out.println("valueList bef del:"+session.getAttribute(valueList));
    session.removeAttribute(valueList);
	//System.out.println("valueList aftdel:"+session.getAttribute(valueList));
	/*edited by Govind
		  for issue id 195170*/
 if(session.getAttribute("shipmentMode")!= null && session.getAttribute("legNo") != null  )
	 {
		session.removeAttribute("shipmentMode");
		session.removeAttribute("legNo");
		session.removeAttribute("temp");
		System.out.println(" Successfully Removed attributes ShipmentMode,legNo,temp(liastValues) from session");
	 }
 }
 catch(Exception ex)
 {
   //System.out.println("Error while removing attributes " + ex.getMessage());
 }

%>
<html>
<head>
<script>

function closeWindow()
{
  window.close();
}

</script>
</head>
<body onLoad="closeWindow()">
<form>
</form>
</body>
</html>