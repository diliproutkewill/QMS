/*
	Program Name	:DensityGroupCodeBean.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode ENtityBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.cmp;
import java.util.ArrayList;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import com.qms.setup.java.DensityGroupCodeDOB;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


public abstract class DensityGroupCodeBean implements EntityBean 
{
    private EntityContext context;
    
    public static final String FILE_NAME = "DensityGroupCodeBean";
    private static Logger logger = null;
    
    public DensityGroupCodeBean()
    {
      logger  = Logger.getLogger(DensityGroupCodeBean.class);
    }
    
   public DensityGroupCodePK ejbCreate()
   {
        return null;
   }
   
    public void ejbPostCreate()
    {
    } 
  
    public DensityGroupCodePK ejbCreate(int dgcCode,double perKG,double perLB,String uom,String invaliDate)
    {    
      try
      {
         this.setDgcCode(dgcCode);
         this.setPerKG(perKG);
         this.setPerLB(perLB);
         this.setUom(uom);
         this.setInvaliDate(invaliDate);
        
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error in CMP"+e.toString());
        logger.error(FILE_NAME+"Error in CMP"+e.toString());
      }      
         return null;
    }

    public void ejbPostCreate(int dgcCode,double perKG,double perLB,String uom,String invaliDate)
    {
    }
       
    public void ejbActivate()
    {
    }

    public void ejbLoad()
    {
    }

    public void ejbPassivate()
    {
    }

    public void ejbRemove()
    {
    }

    public void ejbStore()
    {
    }

    public void setEntityContext(EntityContext ctx)
    {
        this.context = ctx;
    }

    public void unsetEntityContext()
    {
        this.context = null;
    }
    
    
    public abstract void setDgcCode(int dgcCode);
    
    public abstract void setPerKG(double perKG);
    
    public  abstract void setPerLB(double perLB);
    
    public abstract void setUom(String uom);
    
    public  abstract void setInvaliDate(String invaliDate);   
    
    public abstract int getDgcCode();
    
    public abstract double getPerKG();
    
    public  abstract double getPerLB();
    
    public abstract  String getUom();
    
    public  abstract  String getInvaliDate();
    
   
}