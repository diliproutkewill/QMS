package com.foursoft.esupply.common.tags;

import java.io.PrintStream;
import java.util.ArrayList;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.*;

// Referenced classes of package com.foursoft.esupply.common.tags:
//            Script, Select

public class Option extends BodyTagSupport
{

    private String innerText;
    private String NL;

    public Option()
    {
        innerText = null;
        NL = "\n";
    }

    public int doEndTag()
        throws JspException
    {
       // System.out.println("ENTERED IN DO END OF OPTION TAG");
        Script script = (Script)TagSupport.findAncestorWithClass(this, com.foursoft.esupply.common.tags.Script.class);
       // System.out.println("SCRIPT :" + script);
        if(script == null)
        {
            throw new JspException("Text tag without Script tag");
        }
        if(!script.getIsOptionExists())
        {
            StringBuffer optionData = new StringBuffer("\n");
            optionData.append("function addOption(optionStr)" + NL);
            optionData.append("{" + NL);
            optionData.append("var optionNode = document.createElement(\"option\");" + NL);
            optionData.append("optionNode.setAttribute(\"innerText\",optionStr);" + NL);
            optionData.append("optionNode.setAttribute(\"value\",optionStr);" + NL);
            optionData.append("optionNode.text=optionStr;" + NL);
            optionData.append("return optionNode;" + NL);
            optionData.append("}" + NL);
            script.getScriptData().append(optionData.toString());
            script.setIsOptionExists(true);
        }
        Select select = (Select)TagSupport.findAncestorWithClass(this, com.foursoft.esupply.common.tags.Select.class);
        if(select == null)
        {
            throw new JspException("Text tag without Script tag");
        } else
        {
            select.getOptionList().add(innerText);
            return 6;
        }
    }

    public int doStartTag()
        throws JspException
    {
       // System.out.println("ENTERED IN DOSTART OF OPTION TAG");
        return 0;
    }

    public String getInnerText()
    {
        return innerText;
    }

    public void setInnerText(String innerText)
    {
        this.innerText = innerText;
    }
}
