
<%
/*	Programme Name : QMSDensityGroupCodeProcess.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : The density group is used for the calculation of the volumetric weight.The DGC codes are 1 for                           Air,2 for Sea and 4 for Truck shipments.
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.setup.java.DensityGroupCodeDOB,
				  com.qms.setup.ejb.sls.QMSSetUpSession,
				  com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				  java.util.ArrayList,
				  javax.naming.InitialContext,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id ="densityGroupCodeObj" class= "com.qms.setup.java.DensityGroupCodeDOB" scope ="request" />

<jsp:setProperty name="densityGroupCodeObj" property="*" />

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSDensityGroupCodeProcess.jsp ";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);
    QMSSetUpSession			remote				=		null;
	QMSSetUpSessionHome     home				=		null;
    String						operation			=		request.getParameter("Operation");
	ArrayList                   dgcList				=		new ArrayList();
	InitialContext				ctxt				=		new InitialContext();
	ArrayList                   keyValueList		=		null;
	ErrorMessage                errorMessageObject	=		null; 
	boolean                     flag				=		false;
	
	
    try
	{
		home   =   (QMSSetUpSessionHome)ctxt.lookup("QMSSetUpSessionBean");
		remote =   (QMSSetUpSession)home.create();
    String perkg = (request.getParameter("perKG")!=null)?request.getParameter("perKG"):"0"	;
    String perlb = (request.getParameter("perLB")!=null)?request.getParameter("perLB"):"0"	;;
    if("Add".equals(operation))
		{     
        densityGroupCodeObj.setDGCCode(Integer.parseInt(request.getParameter("dgcCode")));
        densityGroupCodeObj.setPerKG(Double.parseDouble(perkg));
        densityGroupCodeObj.setPerLB(Double.parseDouble(perlb));
        
        //System.out.println("dgcDOB.getDGCCode() "+densityGroupCodeObj.getDGCCode());
				//System.out.println("dgcDOB.getPerKG() "+densityGroupCodeObj.getPerKG());
				//System.out.println("dgcDOB.getPerLB() "+densityGroupCodeObj.getPerLB());
				 flag=remote.insertDensityGroupCodeDetails(densityGroupCodeObj);
				 keyValueList = new ArrayList();
				 //System.out.println("flag123 "+flag);
				 if(flag)
				 {				//System.out.println("flag123 "+flag);	 
					 errorMessageObject = new ErrorMessage("Record successfully added ","QMSDensityGroupCodeAdd.jsp");
					 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else if(flag==false)
				{//System.out.println("flag123 "+flag);
					errorMessageObject = new ErrorMessage("Record Already Exist","QMSDensityGroupCodeAdd.jsp");
					 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				}
				 else
			     {					//System.out.println("flag1234 "+flag);
					errorMessageObject = new ErrorMessage("Error while adding record ","QMSDensityGroupCodeAdd.jsp");
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		}
		else if("Modify".equals(operation))
		{
				densityGroupCodeObj.setDGCCode(Integer.parseInt(request.getParameter("dgcCode")));
				densityGroupCodeObj.setPerKG(Double.parseDouble(perkg));
				densityGroupCodeObj.setPerLB(Double.parseDouble(perlb));
			     flag=remote.updateDensityGroupCodeDetails(densityGroupCodeObj);
				 keyValueList = new ArrayList();

				 if(flag==true)
			     {
					errorMessageObject = new ErrorMessage("Record successfully Modified ","QMSDensityGroupCodeEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else
			     {  
					 errorMessageObject = new ErrorMessage("Error while Modifing the Record ","QMSDensityGroupCodeEnterId.jsp");
					 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Modify")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		}
		else if("Delete".equals(operation))
		{
				densityGroupCodeObj.setDGCCode(Integer.parseInt(request.getParameter("dgcCode")));
				densityGroupCodeObj.setPerKG(Double.parseDouble(perkg));
				 flag=remote.deleteDensityGroupCodeDetails(densityGroupCodeObj);
				 keyValueList = new ArrayList();

				 if(flag==true)
			     {
					errorMessageObject = new ErrorMessage("Record successfully deleted","QMSDensityGroupCodeEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else
			     {
					errorMessageObject = new ErrorMessage("Error while Deleting the Record","QMSDensityGroupCodeEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Delete")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 //Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		}
		else if("Invalidate".equals(operation))
		{			
			     String dgcCode[]     =   request.getParameterValues("dgcCode");
				 String perKG[]       =   request.getParameterValues("perKG");
				 String perLB[]       =   request.getParameterValues("perLB");
				 String checkBoxValue[]=   request.getParameterValues("checkBoxValue");
				 DensityGroupCodeDOB   dgcDOB    = null;
				 StringBuffer  messageInvalidate = new StringBuffer();
				 StringBuffer  messageValidate   = new StringBuffer();
				 String        code              = "";

				 messageInvalidate.append("Records are successfully Invalidated  \n");
				 messageInvalidate.append("Invalidated details are\n");
				 messageValidate.append("Valid details are \n");

				 for(int i=0;i<dgcCode.length;i++)
				 {
						dgcDOB   =  new DensityGroupCodeDOB();					
						dgcDOB.setDGCCode(Integer.parseInt(dgcCode[i]));
						dgcDOB.setPerKG(Double.parseDouble(perKG[i]));
						dgcDOB.setPerLB(Double.parseDouble(perLB[i]));

						if(dgcDOB.getDGCCode()==1)
							code="Air";
						else if(dgcDOB.getDGCCode()==2)
							code="Sea";
						else if(dgcDOB.getDGCCode()==4)
							code="Truck";

						 if("true".equals(checkBoxValue[i])){
						 	dgcDOB.setInvalidate("T");
							messageInvalidate.append("DGCCode : "+code+"       PerKG : "+perKG[i]+"      PerLB : "+perLB[i]+" \n");							
						 }
						 else{
							dgcDOB.setInvalidate("F");
							messageValidate.append("DGCCode : "+code+"       PerKG : "+perKG[i]+"      PerLB : "+perLB[i]+" \n");
						 }
						 dgcList.add(dgcDOB);
				 }

				 messageInvalidate.append("\n\n"+messageValidate);

				 flag=remote.invalidateDensityGroupCodeDetails(dgcList);

				 if(flag==false)
						messageInvalidate=new StringBuffer("Error while invalidating the Records");

				 keyValueList = new ArrayList();
				 errorMessageObject = new ErrorMessage(messageInvalidate.toString(),"QMSDensityGroupCodeViewAllEnterId.jsp");

				 if(flag==true)
					 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 else
					 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	

				 keyValueList.add(new KeyValue("Operation","Invalidate")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 //Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		}
	 }	 
	 catch(Exception exp)
	 {
				 //Logger.error(FILE_NAME,"Error in QMSDensityGroupCodeProcess.jsp file ",exp.toString());
         logger.error(FILE_NAME+"Error in QMSDensityGroupCodeProcess.jsp file "+exp.toString());
				 
				 keyValueList       = new ArrayList();
				 if(operation.equals("Add"))
					 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSDensityGroupCodeAdd.jsp");
				 else
					 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSDensityGroupCodeEnterId.jsp?Operation="+operation);
				 
				 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>