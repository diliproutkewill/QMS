package com.foursoft.esupply.common.tags;

import java.io.PrintStream;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class Script extends BodyTagSupport
{

    private boolean isTextExists;
    private boolean isCheckBoxExists;
    private boolean isButtonExists;
    private boolean isSelectExists;
    private boolean isOptionExists;
    private boolean isHiddenExists;
    private String NL;
    private String tableId;
    private JspWriter out;
    private StringBuffer elementsData;
    private StringBuffer scriptData;

    public Script()
    {
        isTextExists = false;
        isCheckBoxExists = false;
        isButtonExists = false;
        isSelectExists = false;
        isOptionExists = false;
        isHiddenExists = false;
        NL = "\n";
        tableId = null;
        out = null;
        elementsData = new StringBuffer();
        scriptData = new StringBuffer();
    }

    public int doAfterBody()
        throws JspException
    {
        return 0;
    }

    public int doEndTag()
        throws JspException
    {
        out = super.pageContext.getOut();
        elementsData.append("tbody.appendChild(tr);" + NL);
        elementsData.append("idCounter++;" + NL);
        elementsData.append("}" + NL);
        scriptData.append(elementsData.toString());
        scriptData.append("</SCRIPT>" + NL);
        try
        {
            out.println(scriptData.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 6;
    }

    public int doStartTag()
        throws JspException
    {
        System.out.println("ENETERED IN SCRIPT DOSTART");
        scriptData.append("<SCRIPT LANGUAGE=\"JavaScript\">" + NL);
        scriptData.append("var idCounter = 0;" + NL);
        scriptData.append("var tableId = " + tableId + ";" + NL);
        scriptData.append("function deleteRow(str)" + NL);
        scriptData.append("{" + NL);
        scriptData.append("var tab = document.getElementsByTagName(\"table\").item(tableId);" + NL);
        scriptData.append("var tb = tab.getElementsByTagName(\"tbody\").item(0);" + NL);
        scriptData.append("if(document.all)" + NL);
        scriptData.append("{" + NL);
        scriptData.append("var tr = document.getElementById(\"tr\"+str);" + NL);
        scriptData.append("tr.removeNode(true);" + NL);
        scriptData.append("}" + NL);
        scriptData.append("else" + NL);
        scriptData.append("{" + NL);
        scriptData.append("var newTr = document.getElementById(\"tr\"+str);" + NL);
        scriptData.append("tb.removeChild(newTr);" + NL);
        scriptData.append("}" + NL);
        scriptData.append("var trLength = tb.getElementsByTagName(\"tr\").length;" + NL);
        scriptData.append("var TR = tb.getElementsByTagName(\"tr\");" + NL);
        scriptData.append("var TD;" + NL);
        scriptData.append("var nextElement;" + NL);
        scriptData.append("for(i=0;i<trLength;i++)" + NL);
        scriptData.append("{" + NL);
        scriptData.append("TD = TR.item(i).getElementsByTagName(\"td\").length;" + NL);
        scriptData.append("nextElement = TR.item(i).getElementsByTagName(\"td\").item(TD-1);" + NL);
        scriptData.append("if(i!=(trLength-1))" + NL);
        scriptData.append("{" + NL);
        scriptData.append("nextElement.removeChild(nextElement.childNodes.item(0));" + NL);
        scriptData.append("nextElement.appendChild(document.createTextNode('     '));" + NL);
        scriptData.append("}" + NL);
        scriptData.append("else" + NL);
        scriptData.append("{" + NL);
        scriptData.append("nextElement.removeChild(nextElement.childNodes.item(0));" + NL);
        scriptData.append("nextElement.appendChild(addButton('addButton','addRow()','>>','false','input'));" + NL);
        scriptData.append("}" + NL);
        scriptData.append("}" + NL);
        scriptData.append("}" + NL);
        elementsData.append("function addRow()" + NL);
        elementsData.append("{" + NL);
        elementsData.append("var td;" + NL);
        elementsData.append("var tab\t\t\t= document.getElementsByTagName(\"table\").item(tableId);" + NL);
        elementsData.append("var tbody\t\t\t= tab.getElementsByTagName(\"tbody\").item(0);" + NL);
        elementsData.append("var trLength\t\t= tbody.getElementsByTagName(\"tr\").length;" + NL);
        elementsData.append("var tdLength\t\t= tbody.getElementsByTagName(\"tr\").item(trLength-1).getElement" +
"sByTagName(\"td\").length;"
 + NL);
        elementsData.append("var nextElement \t= tbody.getElementsByTagName(\"tr\").item(trLength-1).getEleme" +
"ntsByTagName(\"td\").item(tdLength-1);"
 + NL);
        elementsData.append("if(idCounter!=0)" + NL);
        elementsData.append("{" + NL);
        elementsData.append("nextElement.removeChild(nextElement.childNodes.item(0));" + NL);
        elementsData.append("nextElement.appendChild(document.createTextNode('     '));" + NL);
        elementsData.append("}" + NL);
        elementsData.append("var tr  = document.createElement(\"tr\");" + NL);
        elementsData.append("tr.setAttribute(\"id\",\"tr\"+idCounter);" + NL);
        elementsData.append("if(idCounter==0)" + NL);
        elementsData.append("{" + NL);
        elementsData.append("td\t= document.createElement(\"td\");" + NL);
        elementsData.append("td.setAttribute(\"align\",\"center\");" + NL);
        elementsData.append("td.appendChild(document.createTextNode('     '));" + NL);
        elementsData.append("tr.appendChild(td);" + NL);
        elementsData.append("}" + NL);
        elementsData.append("else" + NL);
        elementsData.append("{" + NL);
        elementsData.append("td\t= document.createElement(\"td\");" + NL);
        elementsData.append("td.setAttribute(\"align\",\"center\");" + NL);
        elementsData.append("td.appendChild(addButton('delButton','deleteRow()','<<','false','input'));" + NL);
        elementsData.append("tr.appendChild(td);" + NL);
        elementsData.append("}" + NL);
        System.out.println("ENETERED IN SCRIPT END OF DOSTART");
        return 2;
    }

    public StringBuffer getElementsData()
    {
        return elementsData;
    }

    public boolean getIsButtonExists()
    {
        return isButtonExists;
    }

    public boolean getIsCheckBoxExists()
    {
        return isCheckBoxExists;
    }

    public boolean getIsHiddenExists()
    {
        return isHiddenExists;
    }

    public boolean getIsOptionExists()
    {
        return isOptionExists;
    }

    public boolean getIsSelectExists()
    {
        return isSelectExists;
    }

    public boolean getIsTextExists()
    {
        return isTextExists;
    }

    public StringBuffer getScriptData()
    {
        return scriptData;
    }

    public String getTableId()
    {
        return tableId;
    }

    public void setElementsData(StringBuffer elementsData)
    {
        this.elementsData = elementsData;
    }

    public void setIsButtonExists(boolean isButtonExists)
    {
        this.isButtonExists = isButtonExists;
    }

    public void setIsCheckBoxExists(boolean isCheckBoxExists)
    {
        this.isCheckBoxExists = isCheckBoxExists;
    }

    public void setIsHiddenExists(boolean isHiddenExists)
    {
        this.isHiddenExists = isHiddenExists;
    }

    public void setIsOptionExists(boolean isOptionExists)
    {
        this.isOptionExists = isOptionExists;
    }

    public void setIsSelectExists(boolean isSelectExists)
    {
        this.isSelectExists = isSelectExists;
    }

    public void setIsTextExists(boolean isTextExists)
    {
        this.isTextExists = isTextExists;
    }

    public void setScriptData(StringBuffer scriptData)
    {
        this.scriptData = scriptData;
    }

    public void setTableId(String tableId)
    {
        this.tableId = tableId;
    }
}
