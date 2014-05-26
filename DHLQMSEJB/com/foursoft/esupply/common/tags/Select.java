package com.foursoft.esupply.common.tags;

import java.io.PrintStream;
import java.util.ArrayList;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.*;

// Referenced classes of package com.foursoft.esupply.common.tags:
//            Script

public class Select extends BodyTagSupport
{

    private String name;
    private String className;
    private String NL;
    private StringBuffer selectData;
    private ArrayList optionList;

    public Select()
    {
        name = null;
        className = null;
        NL = "\n";
        selectData = new StringBuffer();
        optionList = new ArrayList();
    }

    public int doAfterBody()
        throws JspException
    {
        return 0;
    }

    public int doEndTag()
        throws JspException
    {
       // System.out.println("ENTERED IN DOEND OF SELECT");
        Script script = (Script)TagSupport.findAncestorWithClass(this, com.foursoft.esupply.common.tags.Script.class);
        if(script == null)
        {
            throw new JspException("Text tag without Script tag");
        }
       // System.out.println("IN ELSE CASE OF SCRIPT ELEMENT OF SELECT TAG");
        if(!script.getIsSelectExists())
        {
            StringBuffer selectData = new StringBuffer("\n");
            selectData.append("function addSelectNode(name,className)" + NL);
            selectData.append("{" + NL);
            selectData.append("var selectNode = document.createElement(\"select\");" + NL);
            selectData.append("selectNode.setAttribute(\"name\",name);" + NL);
            selectData.append("if(document.all)" + NL);
            selectData.append("selectNode.setAttribute(\"className\",className);" + NL);
            selectData.append("else" + NL);
            selectData.append("selectNode.setAttribute(\"class\",className);" + NL);
            selectData.append("for(i=0;i<optionsList.length;i++)" + NL);
            selectData.append("selectNode.appendChild(addOption(optionsList[i]));" + NL);
            selectData.append("return selectNode;" + NL);
            selectData.append("}" + NL);
            script.getScriptData().append(selectData.toString());
            script.setIsSelectExists(true);
        }
        StringBuffer selectBoxData = new StringBuffer();
        StringBuffer optionsData = new StringBuffer();
       
		optionsData.append("var optionsList = new Array(");
        int optionsListSize	=	optionList.size();
		for(int i = 0; i < optionsListSize; i++)
        {
            if(i == 0)
            {
                optionsData.append("'" + optionList.get(i) + "'");
            }
			else
            {
                optionsData.append(",'" + optionList.get(i) + "'");
            }
        }

        script.getScriptData().append(optionsData.toString()+");");

        selectBoxData.append("td\t= document.createElement(\"td\");" + NL);
        selectBoxData.append("td.setAttribute(\"align\",\"center\");" + NL);
        selectBoxData.append("\ttd.appendChild(addSelectNode('" + name + "','" + className + "'));" + NL);
        selectBoxData.append("tr.appendChild(td);" + NL);
        script.getElementsData().append(selectBoxData.toString());
        return 6;
    }

    public int doStartTag()
        throws JspException
    {
       // System.out.println("ENTERED IN DOSTART OF SELECT TAG");
        return 2;
    }

    public String getClassName()
    {
        return className;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList getOptionList()
    {
        return optionList;
    }

    public StringBuffer getSelectData()
    {
        return selectData;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSelectData(StringBuffer selectData)
    {
        this.selectData = selectData;
    }
}
