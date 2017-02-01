<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
    <sld:UserLayer>
        <sld:UserStyle>
            <sld:Name>Default Styler</sld:Name>
            <sld:Title>Default Styler</sld:Title>
            <sld:Abstract></sld:Abstract>
            <sld:FeatureTypeStyle>
                <sld:Name>name</sld:Name>
                <sld:Title>title</sld:Title>
                <sld:Abstract>abstract</sld:Abstract>
                <sld:FeatureTypeName>SOILS_VENTER</sld:FeatureTypeName>
                
                
                <sld:Rule>
                    <sld:Name>rule01</sld:Name>
                    <sld:Title>0 to 2</sld:Title>
                    <sld:Abstract>Abstract -- white</sld:Abstract>
                    <ogc:Filter>
                        <ogc:And>
                            <ogc:PropertyIsLessThanOrEqualTo>
                                <ogc:Literal>0</ogc:Literal>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                            </ogc:PropertyIsLessThanOrEqualTo>
                            <ogc:PropertyIsLessThan>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                                <ogc:Literal>2.5</ogc:Literal>
                            </ogc:PropertyIsLessThan>
                        </ogc:And>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>11.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">
                                <ogc:Literal>#8F92C9</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="fill-opacity">
                                <ogc:Literal>0.5</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Fill>
                        <sld:Stroke>
                            <sld:CssParameter name="stroke">
                                <ogc:Literal>#8F92C9</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linecap">
                                <ogc:Literal>butt</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linejoin">
                                <ogc:Literal>miter</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-opacity">
                                <ogc:Literal>0.5</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-width">
                                <ogc:Literal>1</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-dashoffset">
                                <ogc:Literal>0</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Stroke>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                
                <sld:Rule>
                    <sld:Name>rule02</sld:Name>
                    <sld:Title>3 to 5</sld:Title>
                    <sld:Abstract>Abstract -- yellow</sld:Abstract>
                    <ogc:Filter>
                        <ogc:And>
                            <ogc:PropertyIsLessThanOrEqualTo>
                                <ogc:Literal>2.6</ogc:Literal>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                            </ogc:PropertyIsLessThanOrEqualTo>
                            <ogc:PropertyIsLessThan>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                                <ogc:Literal>4.5</ogc:Literal>
                            </ogc:PropertyIsLessThan>
                        </ogc:And>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>11.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">
                                <ogc:Literal>#57C4C7</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="fill-opacity">
                                <ogc:Literal>.5</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Fill>
                        <sld:Stroke>
                            <sld:CssParameter name="stroke">
                                <ogc:Literal>#57C4C7</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linecap">
                                <ogc:Literal>butt</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linejoin">
                                <ogc:Literal>miter</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-opacity">
                                <ogc:Literal>0.5</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-width">
                                <ogc:Literal>1</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-dashoffset">
                                <ogc:Literal>0</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Stroke>
                    </sld:PolygonSymbolizer>
                </sld:Rule>

                
                <sld:Rule>
                    <sld:Name>rule03</sld:Name>
                    <sld:Title>6 to 9</sld:Title>
                    <sld:Abstract>Abstract -- blue </sld:Abstract>
                    <ogc:Filter>
                        <ogc:And>
                            <ogc:PropertyIsLessThanOrEqualTo>
                                <ogc:Literal>4.6</ogc:Literal>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                            </ogc:PropertyIsLessThanOrEqualTo>
                            <ogc:PropertyIsLessThan>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                                <ogc:Literal>5.5</ogc:Literal>
                            </ogc:PropertyIsLessThan>
                        </ogc:And>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>11.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">
                                <ogc:Literal>#AEC5E5</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="fill-opacity">
                                <ogc:Literal>.5</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Fill>
                        <sld:Stroke>
                            <sld:CssParameter name="stroke">
                                <ogc:Literal>#AEC5E5</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linecap">
                                <ogc:Literal>butt</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linejoin">
                                <ogc:Literal>miter</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-opacity">
                                <ogc:Literal>0.5</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-width">
                                <ogc:Literal>1</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-dashoffset">
                                <ogc:Literal>0</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Stroke>
                    </sld:PolygonSymbolizer>
                </sld:Rule>
                
                <sld:Rule>
                    <sld:Name>rule04</sld:Name>
                    <sld:Title>6 to 9</sld:Title>
                    <sld:Abstract>Abstract -- blue </sld:Abstract>
                    <ogc:Filter>
                        <ogc:And>
                            <ogc:PropertyIsLessThanOrEqualTo>
                                <ogc:Literal>5.6</ogc:Literal>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                            </ogc:PropertyIsLessThanOrEqualTo>
                            <ogc:PropertyIsLessThan>
                                <ogc:PropertyName>PRIMARY_</ogc:PropertyName>
                                <ogc:Literal>19</ogc:Literal>
                            </ogc:PropertyIsLessThan>
                        </ogc:And>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>11.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">
                                <ogc:Literal>#F9A870</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="fill-opacity">
                                <ogc:Literal>.5</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Fill>
                        <sld:Stroke>
                            <sld:CssParameter name="stroke">
                                <ogc:Literal>#F9A870</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linecap">
                                <ogc:Literal>butt</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-linejoin">
                                <ogc:Literal>miter</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-opacity">
                                <ogc:Literal>.5</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-width">
                                <ogc:Literal>1</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="stroke-dashoffset">
                                <ogc:Literal>0</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Stroke>
                    </sld:PolygonSymbolizer>
                </sld:Rule>



            </sld:FeatureTypeStyle>
        </sld:UserStyle>
    </sld:UserLayer>
</sld:StyledLayerDescriptor>

