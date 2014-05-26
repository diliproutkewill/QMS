package com.foursoft.esupply.common.util;
import java.util.ResourceBundle;

public class BundleFile implements java.io.Serializable 
{
    public BundleFile()
    {
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String newLanguage)
    {
        language = newLanguage;
    }

    public ResourceBundle getBundle()
    {
        //labelBundle = ResourceBundle.getBundle(language);
        labelBundle = ResourceBundle.getBundle("Lang",new java.util.Locale(language));
        return labelBundle;
    }

    public void setBundle(ResourceBundle bundle)
    {
        labelBundle = bundle;
    }

	private ResourceBundle labelBundle =  null;
    private String language = null;
}