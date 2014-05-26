package com.foursoft.esupply.common.tag;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class PageTracker extends TagSupport
{

    public PageTracker()
    {
        
    }

    public int doEndTag()
        throws JspException
    {
        try
        {
            super.pageContext.getOut().write(out.toString());
        }
        catch(IOException _ex)
        {
            throw new JspTagException("Fatal IO Error");
        }
        return 6;
    }

    public int doStartTag()
        throws JspException
    {
	out = new StringBuffer();
        boolean hasPreveous = false;
        boolean hasNext = false;
        int previousPageIndex = 0;
        int nextPageIndex = 0;
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
        if(hasPreveous)
            out.append("<a href='" + fileName + "?pageNo=" + previousPageIndex + "'>Previous</a>&nbsp;");
        int startNo = 0;
        int endNo = 0;
        if(currentPageNo > noOfSegments)
            startNo = noOfSegments;
        if(noOfPages > startNo + noOfSegments)
            endNo = noOfSegments;
        else
            endNo = noOfPages;
        for(int i = startNo; i < endNo; i++)
            if(currentPageNo == i + 1)
                out.append((i + 1) + "&nbsp;");
            else
                out.append("<a href='" + fileName + "?pageNo=" + (i + 1) + "'>" + (i + 1) + "</a>&nbsp;");

        if(hasNext)
            out.append("<a href='" + fileName + "?pageNo=" + nextPageIndex + "'>Next</a>&nbsp;");
        return 0;
    }

    public int getCurrentPageNo()
    {
        return currentPageNo;
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
