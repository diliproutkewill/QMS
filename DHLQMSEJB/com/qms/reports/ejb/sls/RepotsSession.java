package com.qms.reports.ejb.sls;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;

public interface RepotsSession extends EJBObject 
{

   public ArrayList  getAproveRRejectQuoteDetail()throws EJBException,RemoteException;
   public ArrayList getEscalatedQuoteReportDetails()throws EJBException,RemoteException;
   public void sendMail(String frmAddress, String toAddress, String message, String attachment) throws EJBException,RemoteException;
}