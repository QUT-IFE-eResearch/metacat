<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eml="eml://ecoinformatics.org/eml-2.1.0">

    <!-- Util template to decide if a BoundingCoords element contains point data -->
    <xsl:template name="isPointData">
      <!-- We have point data if the east coords = west coords and the north coords = south coords.
           We use basic XPath string functions, to avoid problems with using 'compare' 
      -->
      <xsl:value-of select="string-length(//boundingCoordinates[1]/eastBoundingCoordinate/text()) = string-length(//boundingCoordinates[1]/westBoundingCoordinate/text()) 
                            and contains(//boundingCoordinates[1]/westBoundingCoordinate/text(), //boundingCoordinates[1]/eastBoundingCoordinate/text()) 
                            and string-length(//boundingCoordinates[1]/northBoundingCoordinate/text()) = string-length(//boundingCoordinates[1]/southBoundingCoordinate/text()) 
                            and contains(//boundingCoordinates[1]/northBoundingCoordinate/text(), //boundingCoordinates[1]/southBoundingCoordinate/text())" />
    </xsl:template>
    
</xsl:stylesheet>