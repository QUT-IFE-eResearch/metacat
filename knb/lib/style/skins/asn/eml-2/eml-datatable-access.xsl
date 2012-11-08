<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" encoding="utf-8" indent="yes" />

<xsl:template match="dataTable" name="datasetaccesslist">
	<xsl:param name="dUrl"/><xsl:param name="dataId"/>

    <xsl:variable name="denied" select="./physical/distribution/access/deny"/>
	<xsl:variable name="allowed" select="./physical/distribution/access/allow"/>

	<div style="display:none"><xsl:attribute name="id">dUrl_<xsl:value-of select="position()"/></xsl:attribute><xsl:value-of select="$dUrl"/></div>
	<div style="display:none"><xsl:attribute name="id">dataId_<xsl:value-of select="position()"/></xsl:attribute><xsl:value-of select="$dataId"/></div>
	<div style="display:none"><xsl:attribute name="id">entityNameIndex_<xsl:value-of select="position()"/></xsl:attribute><xsl:value-of select="./entityName"/></div>
	<div style="display:none"><xsl:attribute name="id">objectNameIndex_<xsl:value-of select="position()"/></xsl:attribute><xsl:value-of select="./physical/objectName"/></div>
	<div style="display:none"><xsl:attribute name="id">numberOfTables</xsl:attribute><xsl:value-of select="last()"/></div>
	<div style="display:none"><xsl:attribute name="id">principal_<xsl:value-of select="position()"/></xsl:attribute>
		<ul>
		<xsl:if test="(./physical/distribution/access/allow)">
			<xsl:for-each select="$allowed">
				<xsl:variable name="permited" select="./principal"/>
				<li><xsl:value-of select="$permited"/><![CDATA[ % allow]]></li>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="(./physical/distribution/access/deny)">
			<xsl:for-each select="$denied">
				<xsl:variable name="notPermited" select="./principal"/>
				<li><xsl:value-of select="$notPermited"/><![CDATA[ % deny]]></li>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="not(./physical/distribution/access/allow) and not(./physical/distribution/access/deny)"><li><![CDATA[ public % allow ]]></li>
		</xsl:if>
		</ul>
	</div>
		
	<a><xsl:attribute name="id">linkDownload_<xsl:value-of select="position()"/></xsl:attribute>
	<xsl:attribute name="href">#</xsl:attribute> Download File </a> 
</xsl:template>

<xsl:template match="dataTable" name="datatableaccess">
	<xsl:param name="dUrl"/><xsl:param name="dataId"/>
	<xsl:variable name="denied" select="../../access/deny"/>
	<xsl:variable name="allowed" select="../../access/allow"/>

	<div style="display:none"><xsl:attribute name="id">dUrl_<xsl:value-of select="$entityindex"/></xsl:attribute><xsl:value-of select="$dUrl"/></div>
	<div style="display:none"><xsl:attribute name="id">dataId_<xsl:value-of select="$entityindex"/></xsl:attribute><xsl:value-of select="$dataId"/></div>
	<div style="display:none"><xsl:attribute name="id">entityNameIndex_<xsl:value-of select="$entityindex"/></xsl:attribute><xsl:value-of select="../../../../entityName"/></div>
	<div style="display:none"><xsl:attribute name="id">objectNameIndex_<xsl:value-of select="$entityindex"/></xsl:attribute><xsl:value-of select="../../../objectName"/></div>
	<div style="display:none"><xsl:attribute name="id">principal_<xsl:value-of select="$entityindex"/></xsl:attribute>
		<ul>
		<xsl:if test="(../../access/allow)">
			<xsl:for-each select="$allowed">
				<xsl:variable name="permited" select="./principal"/>
				<li><xsl:value-of select="$permited"/><![CDATA[ % allow]]></li>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="(../../access/deny)">
			<xsl:for-each select="$denied">
				<xsl:variable name="notPermited" select="./principal"/>
				<li><xsl:value-of select="$notPermited"/><![CDATA[ % deny]]></li>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="not(../../access/allow) and not(../../access/deny)"><li><![CDATA[ public % allow ]]></li>
		</xsl:if>
		</ul>
	</div>
	<a><xsl:attribute name="id">linkDownload_<xsl:value-of select="$entityindex"/></xsl:attribute>
	<xsl:attribute name="href">#</xsl:attribute> Download File </a> 

</xsl:template>

<xsl:template match="dataset" name="viewLicence">
	
	<a id="openLicence"><xsl:attribute name="href" ><![CDATA[javascript:viewLicence(]]><![CDATA[)]]></xsl:attribute>View Licence</a>
	
</xsl:template>

</xsl:stylesheet>