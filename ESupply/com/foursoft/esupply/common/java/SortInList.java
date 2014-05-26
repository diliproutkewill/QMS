package com.foursoft.esupply.common.java;
import java.util.ArrayList;
import java.sql.Timestamp;
import com.foursoft.esupply.common.util.ESupplyDateUtility;

public class SortInList 
{
  public SortInList()
  {
  }
  public ArrayList qSort(ArrayList unSortlist, int a, int b, String insType,String dateFormat)
  {
    ESupplyDateUtility esd = new ESupplyDateUtility();
    ArrayList list = unSortlist;
    try
    {
    
    int i=a+1, j=b;
    if (i <= j)
    {
      String pivot = ((SortObj)list.get(a)).getValue();
      Timestamp pivot1 = null;
      Timestamp pivot2 = null;
	  Integer ipivot= new Integer(0);
    
	  Double dpivot=new Double(0.0);

	  if(insType.equals("int"))
	  ipivot = ((SortObj)list.get(a)).getIntValue();

	  if(insType.equals("double"))
	   dpivot = ((SortObj)list.get(a)).getDoubleValue();

      if(insType.equals("Timestamp"))
        pivot2 = ((SortObj)list.get(a)).getTValue();       
        
      if(insType.equals("TimestampString"))
        pivot1 = esd.getTimestamp(dateFormat,pivot);
        
      while (i<j)
      {        
        if(insType.equals("TimestampString"))
        {                   
          while ((pivot1.after(esd.getTimestamp(dateFormat,((SortObj)list.get(i)).getValue())) || pivot1.equals(esd.getTimestamp(dateFormat,((SortObj)list.get(i)).getValue()))) && i<b)    i++;
          while ((pivot1.before(esd.getTimestamp(dateFormat,((SortObj)list.get(j)).getValue())) || pivot1.equals(esd.getTimestamp(dateFormat,((SortObj)list.get(i)).getValue()))) && j>a)   j--;          
        }
        else if(insType.equals("Timestamp"))
        {          
          while ((pivot2.after(((SortObj)list.get(i)).getTValue()) || pivot2.equals(((SortObj)list.get(i)).getTValue())) && i<b)   i++;
          //while ((pivot2.before(((SortObj)list.get(j)).getTValue())|| pivot2.equals(((SortObj)list.get(i)).getTValue())) && j>a)   j--;          
          while ((pivot2.before(((SortObj)list.get(j)).getTValue())) && j>a)   j--;          
        }
        else if(insType.equals("int"))
        {
          while ((ipivot.compareTo(((SortObj)list.get(i)).getIntValue()) >= 0) && i<b)   i++;
          while ((ipivot.compareTo(((SortObj)list.get(j)).getIntValue()) <= 0) && j>a)   j--;
        }
		else if(insType.equals("double"))
        {
          while ((dpivot.compareTo(((SortObj)list.get(i)).getDoubleValue()) >= 0) && i<b)   i++;
          while ((dpivot.compareTo(((SortObj)list.get(j)).getDoubleValue()) <= 0) && j>a)   j--;
        }
        else 
        {
          while ((pivot.compareTo(((SortObj)list.get(i)).getValue()) >= 0) && i<b)   i++;
          while ((pivot.compareTo(((SortObj)list.get(j)).getValue()) <= 0) && j>a)   j--;
        }

        if (i < j)
        {
          if(insType.equals("Timestamp"))
          {           
            int keyAtI = ((SortObj)list.get(i)).getLocation();
            int keyAtJ = ((SortObj)list.get(j)).getLocation();
            Timestamp valueAtI = ((SortObj)list.get(i)).getTValue();
            Timestamp valueAtJ = ((SortObj)list.get(j)).getTValue();
            ((SortObj)list.get(i)).setLocation(keyAtJ);
            ((SortObj)list.get(j)).setLocation(keyAtI);
            ((SortObj)list.get(i)).setTValue(valueAtJ);
            ((SortObj)list.get(j)).setTValue(valueAtI);
            
          }
		  else if(insType.equals("int"))
          {           
            int keyAtI = ((SortObj)list.get(i)).getLocation();
            int keyAtJ = ((SortObj)list.get(j)).getLocation();
            Integer valueAtI = ((SortObj)list.get(i)).getIntValue();
            Integer valueAtJ = ((SortObj)list.get(j)).getIntValue();
            ((SortObj)list.get(i)).setLocation(keyAtJ);
            ((SortObj)list.get(j)).setLocation(keyAtI);
            ((SortObj)list.get(i)).setIntValue(valueAtJ);
            ((SortObj)list.get(j)).setIntValue(valueAtI);
            
          }
		  else if(insType.equals("double"))
          {           
            int keyAtI = ((SortObj)list.get(i)).getLocation();
            int keyAtJ = ((SortObj)list.get(j)).getLocation();
            Double valueAtI = ((SortObj)list.get(i)).getDoubleValue();
            Double valueAtJ = ((SortObj)list.get(j)).getDoubleValue();
            ((SortObj)list.get(i)).setLocation(keyAtJ);
            ((SortObj)list.get(j)).setLocation(keyAtI);
            ((SortObj)list.get(i)).setDoubleValue(valueAtJ);
            ((SortObj)list.get(j)).setDoubleValue(valueAtI);
            
          }
          else
          {
            int keyAtI = ((SortObj)list.get(i)).getLocation();
            int keyAtJ = ((SortObj)list.get(j)).getLocation();
            String valueAtI = ((SortObj)list.get(i)).getValue();
            String valueAtJ = ((SortObj)list.get(j)).getValue();
            ((SortObj)list.get(i)).setLocation(keyAtJ);
            ((SortObj)list.get(j)).setLocation(keyAtI);
            ((SortObj)list.get(i)).setValue(valueAtJ);
            ((SortObj)list.get(j)).setValue(valueAtI);
          }  
        }
      }
      
	  if(insType.equals("TimestampString"))
      {      
        if(pivot1.after(esd.getTimestamp(dateFormat,((SortObj)list.get(j)).getValue())))
        {
          int keyAtPivot = ((SortObj)list.get(a)).getLocation();
          int keyAtJ = ((SortObj)list.get(j)).getLocation();
          String valueAtPivot = ((SortObj)list.get(a)).getValue();
          String valueAtJ = ((SortObj)list.get(j)).getValue();
          ((SortObj)list.get(a)).setLocation(keyAtJ);
          ((SortObj)list.get(j)).setLocation(keyAtPivot);
          ((SortObj)list.get(a)).setValue(valueAtJ);
          ((SortObj)list.get(j)).setValue(valueAtPivot);
        }
      }
      else if(insType.equals("Timestamp"))
      {        
        if(pivot2.after(((SortObj)list.get(j)).getTValue()))
        {
          int keyAtPivot = ((SortObj)list.get(a)).getLocation();
          int keyAtJ = ((SortObj)list.get(j)).getLocation();
          Timestamp valueAtPivot = ((SortObj)list.get(a)).getTValue();
          Timestamp valueAtJ = ((SortObj)list.get(j)).getTValue();
          ((SortObj)list.get(a)).setLocation(keyAtJ);
          ((SortObj)list.get(j)).setLocation(keyAtPivot);
          ((SortObj)list.get(a)).setTValue(valueAtJ);
          ((SortObj)list.get(j)).setTValue(valueAtPivot);
        }
        
      }
      else if(insType.equals("int"))
      {
        if (ipivot.compareTo(((SortObj)list.get(j)).getIntValue()) > 0)
        {
          int keyAtPivot = ((SortObj)list.get(a)).getLocation();
          int keyAtJ = ((SortObj)list.get(j)).getLocation();
          Integer valueAtPivot = ((SortObj)list.get(a)).getIntValue();
          Integer valueAtJ = ((SortObj)list.get(j)).getIntValue();
          ((SortObj)list.get(a)).setLocation(keyAtJ);
          ((SortObj)list.get(j)).setLocation(keyAtPivot);
          ((SortObj)list.get(a)).setIntValue(valueAtJ);
          ((SortObj)list.get(j)).setIntValue(valueAtPivot);
        }
      }

	  else if(insType.equals("double"))
      {
        if (dpivot.compareTo(((SortObj)list.get(j)).getDoubleValue()) > 0)
        {
          int keyAtPivot = ((SortObj)list.get(a)).getLocation();
          int keyAtJ = ((SortObj)list.get(j)).getLocation();
          Double valueAtPivot = ((SortObj)list.get(a)).getDoubleValue();
          Double valueAtJ = ((SortObj)list.get(j)).getDoubleValue();
          ((SortObj)list.get(a)).setLocation(keyAtJ);
          ((SortObj)list.get(j)).setLocation(keyAtPivot);
          ((SortObj)list.get(a)).setDoubleValue(valueAtJ);
          ((SortObj)list.get(j)).setDoubleValue(valueAtPivot);
        }
      }
      else
      {
        if (pivot.compareTo(((SortObj)list.get(j)).getValue()) > 0)
        {
          int keyAtPivot = ((SortObj)list.get(a)).getLocation();
          int keyAtJ = ((SortObj)list.get(j)).getLocation();
          String valueAtPivot = ((SortObj)list.get(a)).getValue();
          String valueAtJ = ((SortObj)list.get(j)).getValue();
          ((SortObj)list.get(a)).setLocation(keyAtJ);
          ((SortObj)list.get(j)).setLocation(keyAtPivot);
          ((SortObj)list.get(a)).setValue(valueAtJ);
          ((SortObj)list.get(j)).setValue(valueAtPivot);
        }
      }
      
      list = qSort(list,a,j-1,insType,dateFormat);
      list = qSort(list,j+1,b,insType,dateFormat);
    }
    //System.out.println("c1<c4===="+new String("c4").compareTo(new String("c1")));
    }
    catch(Exception e)
    {
      System.out.println("Exception in SortInList.java"+e.toString());
    }
  
      return list;
    
  }

  /*public static void main(String[] args)
  {
    ArrayList unSortList = null;
    DataObj dObj = null;
    SortInList sortInList = new SortInList();
    unSortList = sortInList.generateList();
    String dType="Timestamp";
    // In the below method(preparedList), the second argument will be the 
    // selected column for sorting. The values for the sample data will be 1-3.
    ArrayList columnList = sortInList.preparedList(unSortList,1);
    ArrayList sortedList = sortInList.qSort(columnList,0,columnList.size()-1,dType);
    /*System.out.println("The List before Sort :\n");
    System.out.println("Id\tName\tLocation");
    for (int i=0;i<unSortList.size();i++) 
      System.out.println(((DataObj)unSortList.get(i)).getId()+"\t"+((DataObj)unSortList.get(i)).getName()+"\t"+((DataObj)unSortList.get(i)).getLoc());
    
    System.out.println("The List after Sort :\n");
    System.out.println("Id\tName\tLocation");    
    for (int i=0;i<sortedList.size();i++)
    {
      dObj = (DataObj) unSortList.get(((SortObj)sortedList.get(i)).getLocation());
      System.out.println(dObj.getId()+"\t"+dObj.getName()+"\t"+dObj.getLoc());
    }    
  }*/
}