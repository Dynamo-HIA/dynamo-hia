<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" />

	<xsl:template match="/">
		<xsl:for-each select="Function">
<xsl:element name="dutch-population" >
		
			<xsl:call-template name="level2" />
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="level2">
		<xsl:for-each select="Function">
			<xsl:element name="gender">
			<xsl:call-template name="level3" />
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="level3">
		<xsl:for-each select="Number">
			<xsl:element name="ageclass">
				<xsl:value-of select="." />
			</xsl:element>
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="gender">
		<xsl:for-each select="Number">
			<xsl:element name="jantje">
				<xsl:copy-of select="." />
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

<xsl:template match="*">
<xsl:element name="default" />
</xsl:template>

</xsl:stylesheet>