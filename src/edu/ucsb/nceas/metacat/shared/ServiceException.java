/**
 *  '$RCSfile$'
 *    Purpose: An Exception thrown when an error occurs because an 
 *             AccessionNumber was invalid or used incorrectly
 *  Copyright: 2008 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *    Authors: Michael Daigle
 *
 *   '$Author: daigle $'
 *     '$Date: 2009-08-05 07:32:58 +1000 (Wed, 05 Aug 2009) $'
 * '$Revision: 5015 $'
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ucsb.nceas.metacat.shared;


/**
 * Exception thrown when an error occurs in a configuration administrative
 * class
 */
public class ServiceException extends BaseException {
	
	private static final long serialVersionUID = -2982801310798091071L;

	/**
	 * Create a new AdminException.
	 *
	 * @param message The error or warning message.
	 */
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, BaseException deeperException) {
		super(message, deeperException);
	}
}
