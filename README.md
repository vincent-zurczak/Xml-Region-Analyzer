Xml Region Analyzer
===================

A light and simple lexical analyzer for XML.

This project was created because I needed a simple way to add syntax highlighting in a stand-alone SWT application.
Since there was no simple solution, I created one. Given a string representing a XML document, it finds regions and their positions.

Here is an example.

 ```java
List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml( yourXmlAsAString );
for( XmlRegion xr : regions ) {
	switch( xr.getXmlRegionType ()) {
		case XmlRegionType.MARKUP: break;
		case XmlRegionType.ATTRIBUTE: break;
		case XmlRegionType.ATTRIBUTE_VALUE: break;
		case XmlRegionType.MARKUP_VALUE: break;
		case XmlRegionType.COMMENT: break;
		case XmlRegionType.INSTRUCTION: break;
		case XmlRegionType.CDATA: break;
		case XmlRegionType.WHITESPACE: break;
		default: break;
	}
	
	int regionLength = xr.getEnd() - xr.getStart();
	System.out.println( "Region Length:" + regionLength );
}
```

To use this project, just copy the two classes **XmlRegion** and **XmlRegionAnalyzer** in your project.
Unit tests are provided. The source code is licensed under the terms of the BSD license.

Source files have a version, so that fixes can be easily found and identified.
 