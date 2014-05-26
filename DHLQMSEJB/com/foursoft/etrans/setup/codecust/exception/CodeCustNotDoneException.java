/*
 * Copyright ©.
 */
package com.foursoft.etrans.setup.codecust.exception;

/**
 * @author  prasadr
 * @version etrans1.6
 */
public class CodeCustNotDoneException extends Exception 
{
  /**
   * @param
   */
	public CodeCustNotDoneException()
	{
        super("Code Customization not done");
	}
  /**
   * 
   * @param msg
   */
	public CodeCustNotDoneException(String msg)
	{
        super(msg);
	}
}
