/*
 * xmlreplicationtables_postgres.sql -- Add two columns to xml_replication tables 
 * in Production Metacat
 * 
 *      Created: 07/14/2002
 *       Author: Jing Tao
 * Organization: National Center for Ecological Analysis and Synthesis
 *    Copyright: 2000 Regents of the University of California and the
 *               National Center for Ecological Analysis and Synthesis
 *  For Details: http://www.nceas.ucsb.edu/
 *    File Info: '$Id: upgrade-db-to-1.2-postgres.sql 4080 2008-07-07 04:25:34Z daigle $'
 *
 */


/*
 * Add tow columns - datareplicate and hub to xml_replication 
 */
ALTER TABLE xml_replication ADD COLUMN datareplicate INT8;
ALTER TABLE xml_replication ADD COLUMN hub INT8;

