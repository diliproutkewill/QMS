<%@ page buffer="1000kb" autoFlush="true"%>
<%
    byte[] buffer = ( byte[])request.getAttribute("buffer");
	request.removeAttribute("buffer");
   java.io.OutputStream outStream = response.getOutputStream();
	 response.setContentType( "application/pdf" );	 
	outStream.write(buffer);
	 outStream.flush();
	 outStream.close(); 
%>