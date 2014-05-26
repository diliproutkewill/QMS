package com.qms.setup.ejb.cmp;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public abstract class DesignationBean implements EntityBean 
{
    private EntityContext context;

    public DesignationPK ejbCreate(String designationId,String description, String levelNo,String invalidate)
    {
		setDesignationId(designationId);
		setDescription(description);
		setLevelNo(levelNo);
		setInvalidate(invalidate);
    return null;
    }
    public DesignationPK ejbCreate()
    {
          return null;
    }
	abstract public String getDesignationId();
	abstract public void setDesignationId(String designationId);

	abstract public String getDescription();
	abstract public void setDescription(String description);

	abstract public String getLevelNo();
	abstract public void setLevelNo(String levelNo);

	abstract public String getInvalidate();
	abstract public void setInvalidate(String invalidate);

    public void ejbPostCreate(String designationId,String description, String levelNo,String invalidate)
    {
    }
    public void ejbPostCreate()
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
}