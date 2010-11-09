<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes" encoding="utf-8"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:text>Пример чтения из базы</xsl:text>
                </title>
            </head>
            <body>
                <textarea cols="40" rows="20">
                    <xsl:copy-of select="page"/>
                </textarea>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>