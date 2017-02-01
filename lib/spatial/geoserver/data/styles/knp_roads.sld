<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.0.0"
	xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
	xmlns="http://www.opengis.net/sld"
	xmlns:ogc="http://www.opengis.net/ogc"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<NamedLayer><Name>Simple Roads</Name>
	<UserStyle>
		
		<Title>Default Styler for simple road segments</Title>
		<Abstract></Abstract>
		<FeatureTypeStyle>
			<FeatureTypeName>Feature</FeatureTypeName>
			<Rule>
				<Name>simple roads</Name>
				<Title>Simple road segments</Title>
				<LineSymbolizer>
					<Stroke>
						<CssParameter name="stroke">
							<ogc:Literal>#CC0033</ogc:Literal>
						</CssParameter>
						<CssParameter name="stroke-width">
							<ogc:Literal>.5</ogc:Literal>
						</CssParameter>
					</Stroke>
				</LineSymbolizer>
			</Rule>
		</FeatureTypeStyle>
	</UserStyle>
	</NamedLayer>
</StyledLayerDescriptor>