<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
							xmlns:eml="eml://ecoinformatics.org/eml-2.1.0"
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					xmlns:gmd="http://www.isotc211.org/2005/gmd"
                    xmlns:gco="http://www.isotc211.org/2005/gco"
                    xmlns:gml="http://www.opengis.net/gml"
                    xmlns:srv="http://www.isotc211.org/2005/srv"
                    xmlns:ADO="http://www.defence.gov.au/ADO_DM_MDP"
                    xmlns:date="http://exslt.org/dates-and-times"
                    xmlns:joda="java:org.fao.geonet.util.JODAISODate"
                    xmlns:mime="java:org.fao.geonet.util.MimeTypeFinder"
					xmlns="http://ands.org.au/standards/rif-cs/registryObjects">
  
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
          
  <xsl:strip-space elements="*"/>
  
  
<xsl:strip-space elements="*"/>

<xsl:template match="root">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="eml:eml">
	<xsl:element name="registryObjects"  xmlns="http://ands.org.au/standards/rif-cs/registryObjects">
		
		<xsl:apply-templates select="." mode="collection"/>
	</xsl:element>
</xsl:template>

<xsl:template match="phone">
	<xsl:element name="electronic">
		<xsl:attribute name="type">
			<xsl:text>voice</xsl:text>
		</xsl:attribute>
		<xsl:element name="value">
			<xsl:value-of select="concat('tel:',translate(translate(.,'+',''),' ','-'))"/>
		</xsl:element>
	</xsl:element>
</xsl:template>


<xsl:template match="electronicMailAddress">
	<xsl:element name="electronic">
		<xsl:attribute name="type">
			<xsl:text>email</xsl:text>
		</xsl:attribute>
		<xsl:element name="value">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:element>

</xsl:template>

<xsl:template match="onlineUrl">
	<xsl:element name="electronic">
		<xsl:attribute name="type">
			<xsl:text>url</xsl:text>
		</xsl:attribute>
		<xsl:element name="value">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:element>
</xsl:template>



<xsl:template match="beginDate">
    <xsl:choose>
        <xsl:when test='contains(., "T")'>
            <xsl:value-of select="."/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="concat(., 'T00:00:00Z')"/> 
        </xsl:otherwise>
    </xsl:choose>

</xsl:template>

<xsl:template match="endDate">
    <xsl:choose>
        <xsl:when test='contains(., "T")'>
            <xsl:value-of select="."/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="concat(., 'T00:00:00Z')"/> 
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template match="organizationName">
	<xsl:element name="addressPart">
		<xsl:attribute name="type">
			<xsl:text>locationDescriptor</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>

</xsl:template>


<xsl:template match="deliveryPoint">
	<xsl:element name="addressPart">
		<xsl:attribute name="type">
			<xsl:text>addressLine</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>


<xsl:template match="city">
	<xsl:element name="addressPart">
		<xsl:attribute name="type">
			<xsl:text>suburbOrPlaceOrLocality</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>

</xsl:template>


<xsl:template match="administrativeArea">
	<xsl:element name="addressPart">
		<xsl:attribute name="type">
			<xsl:text>stateOrTerritory</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>


<xsl:template match="postalCode">
	<xsl:element name="addressPart">
		<xsl:attribute name="type">
			<xsl:text>postCode</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>

</xsl:template>


<xsl:template match="country">
	<xsl:element name="addressPart">
		<xsl:attribute name="type">
			<xsl:text>country</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>


<xsl:template match="title">
	<xsl:value-of select="."/>
</xsl:template>


<xsl:template match="boundingCoordinates">
	<xsl:element name="spatial">
		<xsl:attribute name="type">
			<xsl:text>iso19139dcmiBox</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="concat('northlimit=',northBoundingCoordinate,'; southlimit=',southBoundingCoordinate,'; westlimit=',westBoundingCoordinate,'; eastLimit=',eastBoundingCoordinate)"/>
		<xsl:text>; projection=WGS84</xsl:text>
	</xsl:element>

</xsl:template>


<xsl:template match="geographicDescription">
	<xsl:element name="spatial">
		<xsl:attribute name="type">
			<xsl:text>text</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>


<xsl:template match="keywordSet">
	<xsl:for-each select="keyword">
  	<xsl:element name="subject">
			<xsl:attribute name="type">
				<xsl:text>local</xsl:text>
			</xsl:attribute>
			<xsl:value-of select="."/>
  	</xsl:element>
	</xsl:for-each>

</xsl:template>


<xsl:template match="abstract">
	<xsl:element name="description">
		<xsl:attribute name="type">
			<xsl:text>brief</xsl:text>
		</xsl:attribute>
	    <xsl:value-of select="."/>
	</xsl:element>
</xsl:template>

		

<!--
	CREATE COLLECTION OBJECT
-->
<xsl:template match="eml:eml" mode="collection">


	<!-- the originating source --> 
  <xsl:param name="origSource" select="dataset/creator/organizationName"/>
        
  <!-- the registry object group -->
  <xsl:param name="group" select="dataset/creator/organizationName"/>

  <xsl:variable name="originatingSource" select="dataset/creator/organizationName"/>
	<xsl:variable name="ge" select="dataset/coverage/geographicCoverage"/>
	<xsl:variable name="te" select="dataset/coverage/temporalCoverage"/>
	
  <xsl:variable name="formattedFrom">
		<xsl:choose>
			<xsl:when test="$te[1]/rangeOfDates/beginDate">
				<xsl:apply-templates select="$te[1]/rangeOfDates/beginDate"/>
			</xsl:when>
			<xsl:when test="$te[1]/singleDateTime">
				<xsl:apply-templates select="$te[1]/singleDateTime"/>
			</xsl:when>
		</xsl:choose>					
	</xsl:variable>
			
	<xsl:variable name="formattedTo">
		<xsl:if test="$te[1]/rangeOfDates/endDate">
			<xsl:apply-templates select="$te[1]/rangeOfDates/endDate"/>
		</xsl:if>
	</xsl:variable>
	
    <xsl:variable name="packageId">
	  <xsl:value-of select="@packageId" />
	</xsl:variable>
	
	<xsl:variable name="from" select="$formattedFrom"/>
			
	<xsl:variable name="to" select="$formattedTo"/>
	
	<xsl:variable name="metacat-server">http://tern-supersites.net.au/knb/metacat</xsl:variable>
	
	<xsl:element name="registryObject">
		<xsl:attribute name="group">
			<xsl:value-of select="$group"/>
		</xsl:attribute>
		<xsl:element name="key"> <!-- first alternateIdentifier -->
			<xsl:value-of select="$packageId"/>
		</xsl:element>
		<xsl:element name="originatingSource">
            <xsl:choose>
                <xsl:when test="not($originatingSource)">
                    <xsl:value-of select="$origSource"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$originatingSource"/>
                </xsl:otherwise>
            </xsl:choose>
		</xsl:element>
		<xsl:element name="collection">
			<xsl:attribute name="type">
				<xsl:value-of select="'dataset'"/>
			</xsl:attribute>
			<xsl:element name="identifier">
				<xsl:attribute name="type">
					<xsl:text>local</xsl:text>
				</xsl:attribute>
				<!-- first alternateIdentifier -->
				<xsl:value-of select="$packageId"/>
			</xsl:element>
			
			<xsl:element name="name">
				<xsl:attribute name="type">
					<xsl:text>primary</xsl:text>
				</xsl:attribute>
				<xsl:element name="namePart">
					<xsl:attribute name="type">
						<xsl:text>full</xsl:text>
					</xsl:attribute>
					<xsl:apply-templates select="dataset/title[1]"/>
				</xsl:element>
			</xsl:element>

			<xsl:element name="location">
				<xsl:element name="address">
					<xsl:element name="electronic">
						<xsl:attribute name="type">
							<xsl:text>url</xsl:text>
						</xsl:attribute>
						<xsl:element name="value">
							<xsl:value-of select="concat($metacat-server,'/', $packageId ,'/xml')"/>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:element>

			<xsl:if test="$ge/boundingCoordinates">
				<xsl:element name="location">
					<xsl:attribute name="type">
						<xsl:text>coverage</xsl:text>
					</xsl:attribute>
					<!-- date time -->
					<xsl:if test="not($formattedFrom='')">
						<xsl:attribute name="dateFrom">
							<xsl:value-of select="$formattedFrom"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="not($formattedTo='')">
						<xsl:attribute name="dateTo">
							<xsl:value-of select="$formattedTo"/>
						</xsl:attribute>						
					</xsl:if>

					<xsl:apply-templates select="$ge/boundingCoordinates"/>
					
				</xsl:element>
			</xsl:if>

			
			
			<!-- parties generated here - implied by name of element -->
			<xsl:for-each select="dataset/*[(individualName/surName!='' or positionName!='' or organizationName!='') and (name()='creator' or name()='metadataProvider') and not(role)]" >
				<xsl:element name="relatedObject">
					<xsl:element name="key">
                    <xsl:choose>                        
                        <xsl:when test="individualName">
													<xsl:apply-templates select="individualName"/>
                        </xsl:when>                        
                        <xsl:when test="string(positionName)">
                            <xsl:value-of select="positionName"/>
                        </xsl:when>
                        <xsl:otherwise>                            
                            <xsl:value-of select="organizationName"/>
                        </xsl:otherwise>
                    </xsl:choose>
					</xsl:element>
					<xsl:element name="relation">
						<xsl:attribute name="type">
							<xsl:value-of select="name(.)"/>
						</xsl:attribute>
					</xsl:element>		
				</xsl:element>
			</xsl:for-each>
			
      <xsl:apply-templates select="dataset/keywordSet"/>
			<xsl:apply-templates select="dataset/abstract"/>

	 <!-- for intellectualRights -->
      <xsl:for-each select="dataset/intellectualRights/para">
        <xsl:element name="description">
        	<xsl:attribute name="type">
          	<xsl:text>rights</xsl:text>
          </xsl:attribute>
          <xsl:value-of select="." />
        </xsl:element>
      </xsl:for-each>

			<xsl:if test="$te/rangeOfDates">
				<xsl:if test="not($from='') and $formattedFrom=''">
					<xsl:element name="description">
						<xsl:attribute name="type">
							<xsl:text>temporal</xsl:text>
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="$from = $to or $to=''">
								<xsl:text>Time period: </xsl:text>
								<xsl:value-of select="$from"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>Time period: </xsl:text>
								<xsl:value-of select="concat($from, ' to ', $to)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:element>
				</xsl:if>
			</xsl:if>		

		</xsl:element>
	</xsl:element>

	

</xsl:template>

<xsl:template match="individualName">
	<xsl:choose>
		<xsl:when test="givenName">
       <xsl:value-of select="concat(givenName,' ',surName)" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="surName"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="createPartyRegistryObject">
	<xsl:param name="group"/>
	<xsl:param name="originatingSource"/>
	<xsl:param name="origSource"/>

			<xsl:attribute name="group">
				<xsl:value-of select="$group"/>
			</xsl:attribute>
      <xsl:element name="key">
        <xsl:choose>
        	<xsl:when test="individualName">
						<xsl:apply-templates select="individualName"/>
          </xsl:when>
          <xsl:when test="string(positionName)">
          	<xsl:value-of select="positionName"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="organizationName"/>
          </xsl:otherwise>
        </xsl:choose>
			</xsl:element>
			<xsl:element name="originatingSource">
        <xsl:choose>
          <xsl:when test="not($originatingSource)">
            <xsl:value-of select="$origSource"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$originatingSource"/>
          </xsl:otherwise>
        </xsl:choose>
			</xsl:element>
			<xsl:element name="party">
				<xsl:attribute name="type">
         <xsl:choose>
           <xsl:when test="individualName">
           	<xsl:value-of select="'person'" />                        
           </xsl:when>
           <xsl:when test="string(positionName)">
           	<xsl:value-of select="'person'"/>
           </xsl:when>
           <xsl:otherwise>
             <xsl:value-of select="'group'"/>
           </xsl:otherwise>
         </xsl:choose>
					<!--<xsl:text>person</xsl:text>-->
				</xsl:attribute>
				<xsl:element name="name">
					<xsl:attribute name="type">
						<xsl:text>primary</xsl:text>
					</xsl:attribute>
					<xsl:element name="namePart">
						<xsl:attribute name="type">
							<xsl:text>full</xsl:text>
						</xsl:attribute>
            <xsl:choose>
               <xsl:when test="individualName">
								<xsl:apply-templates select="individualName"/>
               </xsl:when>
               <xsl:when test="string(positionName)">
                <xsl:value-of select="positionName"/>
               </xsl:when>
               <xsl:otherwise>
                <xsl:value-of select="organizationName"/>
               </xsl:otherwise>
            </xsl:choose>
						<!--<xsl:value-of select="current-grouping-key()"/>-->
					</xsl:element>
				</xsl:element>
				<xsl:call-template name="fillOutAddress"/>
			</xsl:element>

</xsl:template>

<xsl:template name="fillOutAddress">	

				

</xsl:template>

<xsl:template name="fixSingle">
    <xsl:param name="value"/>

    <xsl:choose>
      <xsl:when test="string-length(string($value))=1">
        <xsl:value-of select="concat('0',$value)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

	<!-- ================================================================== -->

	<xsl:template name="getMimeTypeFile">
    <xsl:param name="datadir"/>
    <xsl:param name="fname"/>
		<xsl:value-of select="mime:detectMimeTypeFile($datadir,$fname)"/>
  </xsl:template>

<!-- ==================================================================== -->

	<xsl:template name="getMimeTypeUrl">
    <xsl:param name="linkage"/>
		<xsl:value-of select="mime:detectMimeTypeUrl($linkage)"/>
  </xsl:template>

<!-- ==================================================================== -->
	<xsl:template name="fixNonIso">
		<xsl:param name="value"/>
		<xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
		<xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

		<xsl:value-of select="translate($value, $smallcase, $uppercase)" />

		<xsl:variable name="now" select="date:date-time()"/>
		<xsl:choose>
		<xsl:when test="$value='' or $value='unknown' or  $value='current' or  $value='now'">
			<xsl:variable name="miy" select="date:month-in-year($now)"/>
			<xsl:variable name="month">
				<xsl:call-template name="fixSingle">
					<xsl:with-param name="value" select="$miy" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="dim" select="date:day-in-month($now)"/>
			<xsl:variable name="day">
				<xsl:call-template name="fixSingle">
					<xsl:with-param name="value" select="$dim" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:value-of select="concat(date:year($now),'-',$month,'-',$day,'T23:59:59')"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$value"/>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<!-- ==================================================================== -->

	<xsl:template name="newGmlTime">
		<xsl:param name="begin"/>
		<xsl:param name="end"/>


		<xsl:variable name="value1">
			<xsl:call-template name="fixNonIso">
				<xsl:with-param name="value" select="normalize-space($begin)"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="value2">
			<xsl:call-template name="fixNonIso">
				<xsl:with-param name="value" select="normalize-space($end)"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- must be a full ISODateTimeFormat - so parse it and make sure it is 
		     returned as a long format using the joda Java Time library -->
		<xsl:variable name="output" select="joda:parseISODateTimes($value1,$value2)"/>
		<xsl:value-of select="$output"/>
		
	</xsl:template>

	<!-- ================================================================== -->

	
	
<xsl:template match="node()"/>

</xsl:stylesheet>
