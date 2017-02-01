/**
 *  '$RCSfile$'
 *  Copyright: 2003 Regents of the University of California.
 *
 * Author: Matthew Perry 
 * '$Date: 2008-07-29 09:07:10 +1000 (Tue, 29 Jul 2008) $'
 * '$Revision: 4167 $'
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
package edu.ucsb.nceas.metacat.spatial;

import edu.ucsb.nceas.metacat.util.SystemUtil;
import edu.ucsb.nceas.utilities.PropertyNotFoundException;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.MultiPoint;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeFactory;
import org.geotools.feature.SchemaException;

import org.apache.log4j.Logger;

/**
 * Class representing the geotools feature schemas and file paths
 * for the spatial data cache.
 */
public class SpatialFeatureSchema {

	private static Logger log = Logger.getLogger(SpatialFeatureSchema.class.getName());

	private static String certPath;
	static {
		try {
			certPath = SystemUtil.getContextDir();
		} catch (PropertyNotFoundException pnfe) {
			System.err.println("Error in SpatialFeatureSchema static block:"
                    + pnfe.getMessage());
            pnfe.printStackTrace();
		}
	}
	public static String polygonShpUri = certPath + "/data/metacat_shps/data_bounds.shp";
	public static String pointShpUri = certPath + "/data/metacat_shps/data_points.shp";


  // EPSG for latlong coordinate system w/ WGS84 datum
  public static int srid = 4326;

  /** empty constructor **/
  public SpatialFeatureSchema() {
         
  }

  /**
   * Creates the featuretype schema for polygon bounds
   */
  public static FeatureType getPolygonFeatureType() {
    try {
        AttributeType[] types = new AttributeType[4];
        types[0] = AttributeTypeFactory.newAttributeType("the_geom", com.vividsolutions.jts.geom.MultiPolygon.class);
        types[1] = AttributeTypeFactory.newAttributeType("docid", String.class);
        types[2] = AttributeTypeFactory.newAttributeType("url", String.class);
        types[3] = AttributeTypeFactory.newAttributeType("title", String.class);
        FeatureType boundsType = FeatureTypeFactory.newFeatureType(types, "bounds");
        return boundsType;
    } catch(SchemaException e) {
        log.error("schema exception : "+e);
        return null;
    }
  }

  /**
   * Creates the featuretype schema for point centroids
   */
  public static FeatureType getPointFeatureType() {
    try {
        AttributeType[] types = new AttributeType[4];
        types[0] = AttributeTypeFactory.newAttributeType("the_geom", com.vividsolutions.jts.geom.MultiPoint.class);
        types[1] = AttributeTypeFactory.newAttributeType("docid", String.class);
        types[2] = AttributeTypeFactory.newAttributeType("url", String.class);
        types[3] = AttributeTypeFactory.newAttributeType("title", String.class);
        FeatureType centroidsType = FeatureTypeFactory.newFeatureType(types, "centroids");
        return centroidsType;
    } catch(SchemaException e) {
        log.error("schema exception : "+e);
        return null;
    }
  }

}
