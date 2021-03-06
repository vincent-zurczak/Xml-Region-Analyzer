# XML Region Analyzer

[![Build Status](https://travis-ci.org/vincent-zurczak/Xml-Region-Analyzer.svg?branch=master)](https://travis-ci.org/vincent-zurczak/Xml-Region-Analyzer)
[![Coverage Status](https://coveralls.io/repos/github/vincent-zurczak/Xml-Region-Analyzer/badge.svg?branch=master)](https://coveralls.io/github/vincent-zurczak/Xml-Region-Analyzer?branch=master)
[![License](https://img.shields.io/badge/license-Apache%20v2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

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


## Using it

You can add this project in your Maven dependencies.

```xml
<dependency>
	<groupId>com.github.vincent-zurczak</groupId>
	<artifactId>xml-region-analyzer</artifactId>
	<version>1.0.0</version>
</dependency>
```

> This project has no dependency towards other libraries.


## Development

```properties
# Compile and package
mvn clean package

# Run the tests
mvn clean test

# Get code coverage (then check target/site/cobertura/)
mvn clean cobertura:cobertura
```


## License

The source code is licensed under the terms of the Apache license v2.
 