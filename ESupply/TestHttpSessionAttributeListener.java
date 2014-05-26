import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.foursoft.esupply.util.ObjectCloner;

public class TestHttpSessionAttributeListener implements javax.servlet.http.HttpSessionAttributeListener 
{
  private HttpSession session = null;



  public void attributeAdded(HttpSessionBindingEvent event)
  {
    String name = null;  
    name = event.getName();
    Object obj = event.getSession().getAttribute(name);
    
    if(obj instanceof  Serializable )
    {
      System.out.println("TestHttpSessionAttributeListener :attributeAdded() :: " +name +" is serilazable Object");
    }
    else
    {
      System.out.println("TestHttpSessionAttributeListener :attributeAdded() :: " +name +" is not serilazable Object XXXXXXXXXXXX");      
    }
    try{
    ObjectCloner.deepCopy(obj);
    }catch(Exception e)
    {
      System.out.println("exception occured at object Cloner:::::::::::"+e+"object Name::::  "+name);
      e.printStackTrace();
    }
  }

  public void attributeRemoved(HttpSessionBindingEvent event)
  {
  String name = null;  
    name = event.getName();
    //System.out.println("Session attributeRemoved :" +name);
    session = event.getSession();
  }

  public void attributeReplaced(HttpSessionBindingEvent event)
  {
  String name = null;  
    name = event.getName();
   
    Object obj = event.getSession().getAttribute(name);
    if(obj instanceof  Serializable )
    {
      System.out.println("TestHttpSessionAttributeListener :attributeAdded() :: " +name +" is serilazable Object");
    }
    else
    {
      System.out.println("TestHttpSessionAttributeListener :attributeAdded() :: " +name +" is not serilazable Object XXXXXXXXXXXX");      
    }
    
  }
}