
package com.foursoft.esupply.common.tag;

import java.io.IOException;
import java.io.PrintStream;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.TagSupport;

public class PageIterator extends TagSupport
{

    public PageIterator()
    {
        out = new StringBuffer();
    }

    public int doStartTag()
        throws JspException
    {
        boolean hasPreveous			= false;
        boolean hasNext				= false;
        int		previousPageIndex	= 0;
        int		nextPageIndex		= 0;
        int		startNo				= 0;
        int		endNo				= 0;
        int		multiplier			= 1;
		
		if(currentPageNo != 1)
        {
            hasPreveous = true;
            previousPageIndex = currentPageNo - 1;
        }
        
		if(currentPageNo != noOfPages)
        {
            hasNext = true;
            nextPageIndex = currentPageNo + 1;
        }
        
		//System.out.println("fileName fileNamefileNamefileNamefileName    "+  fileName  );
		if(hasPreveous)
		{
    
      //@@Commented and Modified by Kameswari for the WPBN issue-61289
		 /*	if("ListMasterMultipleLOV.jsp".equalsIgnoreCase(fileName)||"ETCLOVLocationIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVCountryIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVServiceLevelIds1.jsp".equalsIgnoreCase(fileName)||"ETransLOVCarrierIds1.jsp".equalsIgnoreCase(fileName) ||"QMSLOVZoneCodesMultiple.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleCustomers.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleSalesPerson.jsp".equalsIgnoreCase(fileName) 
      || "QMSQuoteIdsLOV1.jsp".equalsIgnoreCase(fileName) || "QMSLOVAllLevelTerminalIds2.jsp".equalsIgnoreCase(fileName))*/
      if("ListMasterMultipleLOV.jsp".equalsIgnoreCase(fileName)||"ETCLOVLocationIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVCountryIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVServiceLevelIds1.jsp".equalsIgnoreCase(fileName)||"ETransLOVCarrierIds1.jsp".equalsIgnoreCase(fileName) ||"QMSLOVZoneCodesMultiple.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleCustomers.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleSalesPerson.jsp".equalsIgnoreCase(fileName) 
      || "QMSQuoteIdsLOV1.jsp".equalsIgnoreCase(fileName) || "QMSLOVAllLevelTerminalIds2.jsp".equalsIgnoreCase(fileName)||"QMSIndustryIdMultipleLOV.jsp".equalsIgnoreCase(fileName))
			 //@@WPBN issue-61289
      {
			
            out.append("<a href=javascript:document.forms[0].action='"+fileName+"?pageNo="+previousPageIndex+"';document.forms[0].submit() onClick='setVar()'>Previous</a>&nbsp;");
        
			}else{
            out.append("<a href='" + fileName + "?pageNo=" + previousPageIndex + "' onClick='setVar()'>Previous</a>&nbsp;");
			}
		}
        
        
		if(currentPageNo > noOfSegments)
        {
            multiplier = currentPageNo / noOfSegments;
            if(currentPageNo % noOfSegments != 0)
                startNo = noOfSegments * multiplier;
            else
                startNo = noOfSegments * (multiplier - 1);
        }
        
		if(noOfPages > startNo)
        {
            if(noOfPages > startNo + noOfSegments)
                endNo = startNo + noOfSegments;
            else
                endNo = startNo + (noOfPages - startNo);
        } 
		else
        {
            endNo = noOfPages;
        }
        
		for(int i = startNo; i < endNo; i++)
             if(currentPageNo == i + 1)
                out.append((i + 1) + "&nbsp;");
             else{
             
				if("ListMasterMultipleLOV.jsp".equalsIgnoreCase(fileName)||"ETCLOVLocationIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVCountryIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVServiceLevelIds1.jsp".equalsIgnoreCase(fileName)||"ETransLOVCarrierIds1.jsp".equalsIgnoreCase(fileName) ||"QMSLOVZoneCodesMultiple.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleCustomers.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleSalesPerson.jsp".equalsIgnoreCase(fileName) 
        || "QMSQuoteIdsLOV1.jsp".equalsIgnoreCase(fileName) || "QMSLOVAllLevelTerminalIds2.jsp".equalsIgnoreCase(fileName))
			{
				 out.append("<a href=javascript:document.forms[0].action='"+fileName+"?pageNo="+(i+1)+"';document.forms[0].submit() onClick='setVar()'>"+(i + 1)+"</a>&nbsp;");
			}else{

                out.append("<a href='" + fileName + "?pageNo=" + (i + 1) + "' onClick='setVar()' >" + (i + 1) + "</a>&nbsp;");
			}
			}

        if(hasNext)
		{
			if("ListMasterMultipleLOV.jsp".equalsIgnoreCase(fileName)||"ETCLOVLocationIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVCountryIds1.jsp".equalsIgnoreCase(fileName)||"ETCLOVServiceLevelIds1.jsp".equalsIgnoreCase(fileName)||"ETransLOVCarrierIds1.jsp".equalsIgnoreCase(fileName) ||"QMSLOVZoneCodesMultiple.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleCustomers.jsp".equalsIgnoreCase(fileName) || "ETCLOVMultipleSalesPerson.jsp".equalsIgnoreCase(fileName) || "QMSLOVAllLevelTerminalIds2.jsp".equalsIgnoreCase(fileName))
			{
				 out.append("<a href=javascript:document.forms[0].action='"+fileName+"?pageNo=" + nextPageIndex + "';document.forms[0].submit()   onClick='setVar()'>Next</a>&nbsp;");//submitForm('"+nextPageIndex+"')
			}else{

            out.append("<a href='" + fileName + "?pageNo=" + nextPageIndex + "' onClick='setVar()' >Next</a>&nbsp;");
			}
		}
        return 0;
    }

    public int doEndTag()
        throws JspException
    {
        try
        {
            pageContext.getOut().write(out.toString());
        }
        catch(IOException _ex)
        {
            throw new JspTagException("Fatal IO Error");
        }
        return 6;
    }

    public int getCurrentPageNo()
    {
        return currentPageNo;
    }

    public void release()
    {
        out = new StringBuffer();
    }

    public void setCurrentPageNo(int currentPageNo)
    {
        this.currentPageNo = currentPageNo;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public void setNoOfPages(int noOfPages)
    {
        this.noOfPages = noOfPages;
    }

    public void setNoOfSegments(int noOfSegments)
    {
        this.noOfSegments = noOfSegments;
    }

    private int currentPageNo;
    private int noOfPages;
    private int noOfSegments;
    private String fileName;
    private StringBuffer out;
}
