/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.java;

import java.util.Hashtable;


/**
 * File			: CachedTransactions.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is used to cached the transactions (id and name)
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	2002-04-25
 */
 
public class CachedTransactions implements Cacheable
{
    private java.util.Date	dateofExpiration = null;
    private Object			identifier = null;
    public	Hashtable			object = null;
    public CachedTransactions(Object obj, Object id, int minutesToLive)
    {
      this.object = (Hashtable)obj;
      this.identifier = id;

      if (minutesToLive != 0)
      {
        dateofExpiration = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(dateofExpiration);
        cal.add(java.util.Calendar.MINUTE, minutesToLive);
        dateofExpiration = cal.getTime();
      }
    }
    public boolean isExpired()
    {
        if (dateofExpiration != null)
        {
          if (dateofExpiration.before(new java.util.Date()))
          {
            //System.out.println("CachedResultSet.isExpired:  Expired from Cache! EXPIRE TIME: " + dateofExpiration.toString() + " CURRENT TIME: " + (new java.util.Date()).toString());
            return true;
          }
          else
          {
            //System.out.println("CachedResultSet.isExpired:  Expired not from Cache!");
            return false;
          }
        }
        else // This means it lives forever!
          return false;
    }
    public Object getIdentifier()
    {
      return identifier;
    }
	public Hashtable getTxList()
	{
		return object;
	}
}

