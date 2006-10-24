<?xml version="1.0"?>
<!-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt 
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>
	<xsl:param name="lamsLanguage"/>
	<xsl:param name="resourcesFile"/>
	<xsl:param name="transitionFile"/>
	<xsl:template match="/">
		<manifest xmlns="http://www.imsglobal.org/xsd/imscp_v1p1" identifier="LAMS_example">
			<metadata>
				<schema>IMS Metadata</schema>
				<schemaversion>1.2</schemaversion>
			</metadata>
			<organizations default="default">
				<learning-design identifier="default" uri="" level="A" xmlns="http://www.imsglobal.org/xsd/imsld_v1p0" xmlns:lams="http://www.lmasfoundation.org/xsd/lams_ims_export_v1p0">
					<title>
						<lams:langstring>
							<xsl:value-of select="*/title"/>
						</lams:langstring>
					</title>
					<!-- ================================== lams LD ================================== -->
					<xsl:apply-templates/>
					<components>
						<roles>
							<learner identifier="Learner">
								<title>Learner</title>
							</learner>
						</roles>
						<activities>
							<xsl:apply-templates select="activities"/>
							<xsl:apply-templates select="transitions"/>
						</activities>
						<xsl:apply-templates select="activities" mode="env"/>
					</components>
					<method>
						<play>
							<act>
								<role-part>
									<role-ref ref="Learner"/>
									<activity-structure-ref ref="A-Sequence"/>
								</role-part>
							</act>
						</play>
					</method>
				</learning-design>
			</organizations>
			<resources>
				<xsl:apply-templates select="resource"/>
			</resources>
		</manifest>
	</xsl:template>
	<!-- ================================== lams activities ================================== -->
	<xsl:template match="activities">
		<xsl:if test="not((*//activityTypeID = 6) or (*//activityTypeID = 7) or (*//activityTypeID = 8))">
			<xsl:apply-templates mode="tool"/>
		</xsl:if>
		<xsl:if test="(*//activityTypeID = 6) or (*//activityTypeID = 7) or (*//activityTypeID = 8)">
			<xsl:apply-templates mode="complex"/>
		</xsl:if>
	<xsl:template>
	
	<!-- ================================== lams Tool activities ================================== -->
	<xsl:template match="activities" name="tool">
		<learning-activity xsl:use-attribute-sets="toolIdentifier">
			<title>
				<lams:langstring>
					<xsl:value-of select="*//activityTitle"/>
				</lams:langstring>
			</title>
			<environment-ref xsl:use-attribute-sets="toolEnvRef"/>
			<complete-activity>
				<user-choice/>
			</complete-activity>
			<xsl:copy-of select="*"/>
		</learning-activity>
	</xsl:template>
	<!-- ================================== lams Complex activities ================================== -->
	<xsl:template match="activities" name="complex">
		<activity-structure xsl:use-attribute-sets="complexAttr">
			<title>
				<lams:langstring>
					<xsl:value-of select="*//activityTitle"/>
				</lams:langstring>
			</title>
			<xsl:variable name="myid" select="*//activityID"/>
			<xsl:for-each select="*">
				<xsl:sort select="*//orderID" order="ascending"/>
				<xsl:if test="*//parentActivityID = $myid">
					<learning-activity-ref  xsl:use-attribute-sets="toolRef"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:copy-of select="*"/>
		</activity-structure>
	</xsl:template>
	<!-- ================================== lams Tansitions ================================== -->
	<xsl:template match="transitions">
		<activity-structure identifier="A-sequence" structure-type="sequence">
			<title>LAMS Learning design sequence</title>
			<!-- copy sorted transition learning-activity-ref -->
			<xsl:copy-of select="document($transitionFile)"/>
			
			<xsl:copy-of select="*"/>
		</activity-structure>
	</xsl:template>
	<!-- ================================== Tools' content ================================== -->
	<xsl:template match="activities" mode="env">
		<environments>
			<environment xsl:use-attribute-sets="toolEnvIdentifier"/>
				<title>
					<lams:langstring>
						<xsl:value-of select="*//activityTitle"/>
					</lams:langstring>
				</title>
				<service  xsl:use-attribute-sets="serviceAttr">
					<tool_interface xmlns="http://www.lmasfoundation.org/xsd/lams_ims_export_v1p0">
						<tool_id>
							<identifier type="URN">URN:LAMS:<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/></identifier>
						</tool_id>
						<tool_version><xsl:value-of select="*//toolVersion"/></tool_version>
						<tool_contents>
							<xsl:copy-of select="document(*//toolContentID+'.xml')"/>
						</tool_contents>
					</tool_interface>
				</service>
			</environment>
		</environments>
	</xsl:template>
	<!-- ================================== resources ================================== -->
	<xsl:template match="resource">
		<xsl:copy-of select="document($resourcesFile)"/>
	</xsl:template>
	
	<!-- ================================== some attributes ================================== -->		
	<xsl:attribute-set name="toolIdentifier">
		<xsl:attribute name="identifier">
			A-<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/>
		</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name="toolRef">
		<xsl:attribute name="ref">
			A-<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/>
		</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="complexAttr">
		<xsl:attribute name="identifier">
			S-<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/>
		</xsl:attribute>
		<xsl:attribute name="structure-type">
			<xsl:if test="*//activityTypeID=6">
				<xsl:value-of select="PARALLEL"/>
			</xsl:if>
			<xsl:if test="*//activityTypeID=7">
				<xsl:value-of select="OPTIONS"/>
			</xsl:if>
			<xsl:if test="*//activityTypeID=8">
				<xsl:value-of select="SEQUENCE"/>
			</xsl:if>
		</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="toolEnvIdentifier">
		<xsl:attribute name="identifier">
			E-<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/>
		</xsl:attribute>
	</xsl:attribute-set>	
	<xsl:attribute-set name="toolEnvRef">
		<xsl:attribute name="ref">
			E-<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/>
		</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="serviceAttr">
		<xsl:attribute name="identifier">
			S-<xsl:value-of select="*//toolSignature"/>-<xsl:value-of select="*//toolContentID"/>
		</xsl:attribute>
		<xsl:attribute name="isvisible">
			true
		</xsl:attribute>
	</xsl:attribute-set>	
	
	</xsl:stylesheet>

