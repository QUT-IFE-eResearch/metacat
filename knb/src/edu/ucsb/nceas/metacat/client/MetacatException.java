/**
 *  '$RCSfile$'
 *  Copyright: 2000 Regents of the University of California and the
 *             National Center for Ecological Analysis and Synthesis
 *
 *   '$Author: jones $'
 *     '$Date: 2003-08-12 08:24:46 +1000 (Tue, 12 Aug 2003) $'
 * '$Revision: 1784 $'
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

package edu.ucsb.nceas.metacat.client;

/**
 * Exception thrown when an error occurs during a metacat operation.
 */
public class MetacatException extends Exception {

    /**
     * Create a new MetacatException.
     *
     * @param message The error or warning message.
     */
    public MetacatException(String message) {
        super(message);
    }
}
