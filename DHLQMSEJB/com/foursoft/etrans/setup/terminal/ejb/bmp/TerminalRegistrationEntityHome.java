package com.foursoft.etrans.setup.terminal.ejb.bmp;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean;

public interface TerminalRegistrationEntityHome extends EJBHome 
{
  TerminalRegistrationEntity create(TerminalRegJspBean terminalRegJspBean) throws RemoteException, CreateException;

  TerminalRegistrationEntity findByPrimaryKey(String terminalId) throws RemoteException, FinderException;
}