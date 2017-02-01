<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
           xmlns:wfs="http://www.opengis.net/wfs"
           xmlns:gml="http://www.opengis.net/gml"
           xmlns:metacat="http://knb.ecoinformatics.org/metacat"
           version="1.0">
     
<xsl:template match="/">
    <ul>
        <xsl:apply-templates select="wfs:FeatureCollection/gml:featureMember" />
    </ul>
</xsl:template>


<xsl:template match="metacat:data_points">
     <xsl:variable name="url" select="./metacat:url"/>
     <li>
      <a href='{$url}'>
         <xsl:value-of select="./metacat:title" />
         ( <xsl:value-of select="./metacat:docid" /> )
      </a>
     </li>
</xsl:template>

<xsl:template match="metacat:data_bounds">
     <xsl:variable name="url" select="./metacat:url"/>
     <li>
      <a href='{$url}'>
         <xsl:value-of select="./metacat:title" />
         ( <xsl:value-of select="./metacat:docid" /> )
      </a>
     </li>
</xsl:template>

<xsl:template match="metacat:metacat_testdata">
     <xsl:variable name="url" select="./metacat:url"/>
     <li>
      <a href='{$url}'>
         <xsl:value-of select="./metacat:docid" />
      </a>
     </li>
</xsl:template>

</xsl:stylesheet>
