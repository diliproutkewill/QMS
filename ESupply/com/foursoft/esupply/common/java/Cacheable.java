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

import java.util.*;
import java.io.*;

/**
 * File			: Cacheable.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * Every cache oBject has to implements Interface
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	2002-04-25
 */
 
public interface Cacheable 
{
	public boolean isExpired();

	public Object getIdentifier();
}
