<?xml version="1.0" encoding="UTF-8"?>
<!--
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                exclude-result-prefixes="java"
                version="1.0">

    <xsl:param name="TITLE" select="''"/>

    <xsl:template match="role-list">
        <html>
            <body>
                <h1><xsl:value-of select="$TITLE"/></h1>
                <table border="0">
                    <tr>
                        <td>Name</td>
                        <td>Description</td>
                    </tr>
                    <xsl:apply-templates select="role" />
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="role">
        <tr>
            <td>
                <xsl:value-of select="@name"/>
            </td>
            <td>
                <xsl:value-of select="@description"/>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
