/*
 * Copyright ©.
 */
package com.foursoft.esupply.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.io.*;

/**
 * @author  raghureddy
 */
public class ProcessingErrorException extends Exception {

  /**
   * Catches exceptions without a specified string
   *
   */
  public ProcessingErrorException() {}

  /**
   * Constructs the appropriate exception with the specified string
   *
   * @param message           String Exception message
   */
  public ProcessingErrorException(String message) {super(message);}
}
