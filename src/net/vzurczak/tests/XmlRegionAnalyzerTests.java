/****************************************************************************
 *
 * Copyright (c) 2012, Vincent Zurczak - All rights reserved.
 * This source file is released under the terms of the BSD license.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *****************************************************************************/

package net.vzurczak.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;
import net.vzurczak.main.XmlRegion;
import net.vzurczak.main.XmlRegion.XmlRegionType;
import net.vzurczak.main.XmlRegionAnalyzer;

import org.junit.Test;

/**
 * Unit tests for {@link XmlRegionAnalyzer}.
 * @author Vincent Zurczak
 */
public class XmlRegionAnalyzerTests {

	private static final String ISTR_STD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";


	/**
	 * @throws Exception
	 */
	@Test
	public void testInstructions() throws Exception {

		// Let's try the basics first
		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();

		String input = "";
		List<XmlRegion> regions = analyzer.analyzeXml( input );
		testRegionsContiguity( regions, input );
		Assert.assertEquals( regions.size(), 0 );

		input = " ";
		regions = analyzer.analyzeXml( input );
		testRegionsContiguity( regions, input );

		Assert.assertEquals( regions.size(), 1 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 1 );

		// Check the standard declaration
		input = ISTR_STD;
		regions = analyzer.analyzeXml( input );
		testRegionsContiguity( regions, input );

		Assert.assertEquals( regions.size(), 1 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.INSTRUCTION );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), ISTR_STD.length());

		// Check an altered declaration
		input = " " + ISTR_STD + " ";
		regions = analyzer.analyzeXml( input );
		testRegionsContiguity( regions, input );

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 1 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.INSTRUCTION );
		Assert.assertEquals( regions.get( 1 ).getStart(), 1 );
		Assert.assertEquals( regions.get( 1 ).getEnd(), 1 + ISTR_STD.length());

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 1 + ISTR_STD.length());
		Assert.assertEquals( regions.get( 2 ).getEnd(), 2 + ISTR_STD.length());
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testMarkups() throws Exception {

		// Instruction + single mark-up
		StringBuilder sb = new StringBuilder( ISTR_STD );
		sb.append( "\n<test/>" );

		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
		List<XmlRegion> regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.INSTRUCTION );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), ISTR_STD.length());

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 1 ).getStart(), ISTR_STD.length());
		Assert.assertEquals( regions.get( 1 ).getEnd(), 1 + ISTR_STD.length());

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getStart(), 1 + ISTR_STD.length());
		Assert.assertEquals( regions.get( 2 ).getEnd(), 8 + ISTR_STD.length());


		// Instruction + altered single mark-up
		sb = new StringBuilder( ISTR_STD );
		sb.append( "\n<test />" );

		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getStart(), 1 + ISTR_STD.length());
		Assert.assertEquals( regions.get( 2 ).getEnd(), 6 + ISTR_STD.length());

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 6 + ISTR_STD.length());
		Assert.assertEquals( regions.get( 3 ).getEnd(), 7 + ISTR_STD.length());

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 4 ).getStart(), 7 + ISTR_STD.length());
		Assert.assertEquals( regions.get( 4 ).getEnd(), 9 + ISTR_STD.length());


		// Altered single mark-up but no instruction
		sb = new StringBuilder( "<test-twisted	 />" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 3 );

		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 13 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 1 ).getStart(), 13 );
		Assert.assertEquals( regions.get( 1 ).getEnd(), 15 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getStart(), 15 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 17 );


		// Test complex mark-ups
		sb = new StringBuilder( ISTR_STD );
		sb.append( "<root><sub-root><test1/><ss-root><test2 /></ss-root></sub-root></root>" );

		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 11 );

		int exceptionCpt = 0;
		for( XmlRegion xr : regions ) {
			if( xr.getXmlRegionType() != XmlRegionType.MARKUP )
				exceptionCpt ++;
		}

		Assert.assertEquals( exceptionCpt, 2 );
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testComments() throws Exception {

		// Instruction + single comment
		StringBuilder sb = new StringBuilder( ISTR_STD );
		sb.append( "<!-- Simple Comment -->" );

		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
		List<XmlRegion> regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 2 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.INSTRUCTION );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), ISTR_STD.length());

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 1 ).getStart(), ISTR_STD.length());
		Assert.assertEquals( regions.get( 1 ).getEnd(), sb.length());


		// Instruction + altered single comment
		sb = new StringBuilder( ISTR_STD );
		sb.append( "<!-- Simple Comment --> " );

		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 3 );

		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.INSTRUCTION );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), ISTR_STD.length());

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 1 ).getStart(), ISTR_STD.length());
		Assert.assertEquals( regions.get( 1 ).getEnd(), sb.length() - 1 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 2 ).getStart(), sb.length() - 1 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), sb.length());


		// Instruction + nasty comment
		sb = new StringBuilder( ISTR_STD );
		sb.append( "<!---->" );

		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 2 );

		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.INSTRUCTION );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), ISTR_STD.length());

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 1 ).getStart(), ISTR_STD.length());
		Assert.assertEquals( regions.get( 1 ).getEnd(), sb.length());


		// No instruction and several comments
		sb = new StringBuilder( "<!-- Comment \n Number 1 -->" );
		sb.append( "<!-- Comment -- Number > 2 -->" );

		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 2 );

		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.COMMENT );

		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), regions.get( 1 ).getStart());
		Assert.assertEquals( regions.get( 1 ).getEnd(), sb.length());


		// A more complex mix
		sb = new StringBuilder( ISTR_STD );
		sb.append( "\n<!-- A first comment-->\n" );
		sb.append( "<root><sub-root>\n<!-- Hop ! -->\n" );
		sb.append( "<test1/><ss-root><!-- Last One --><test2 /></ss-root></sub-root></root>\t" );

		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());
		Assert.assertEquals( regions.size(), 19 );

		int commentCpt = 0, markupCpt = 0, instrCpt = 0, whitespacesCpt = 0, otherCpt = 0;
		int end = 0;
		for( XmlRegion xr : regions ) {
			switch( xr.getXmlRegionType()) {
				case COMMENT: commentCpt ++; break;
				case MARKUP: markupCpt ++; break;
				case INSTRUCTION: instrCpt ++; break;
				case WHITESPACE: whitespacesCpt ++; break;
				default: otherCpt ++;
			}

			assertEquals( xr.toString(), end, xr.getStart());
			end = xr.getEnd();
		}

		Assert.assertEquals( otherCpt, 0 );
		Assert.assertEquals( commentCpt, 3 );
		Assert.assertEquals( instrCpt, 1 );
		Assert.assertEquals( markupCpt, 9 );
		Assert.assertEquals( whitespacesCpt, 6 );
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testAttributes() throws Exception {

		// A single attribute
		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
		StringBuilder sb = new StringBuilder( "<test arg0" );
		List<XmlRegion> regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), sb.length());


		// Another single attribute
		sb = new StringBuilder( "<test arg0=\"not closed" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 4 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), sb.length());


		// Another single attribute
		sb = new StringBuilder( "<test arg0=\"closed\"" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 4 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), sb.length());


		// Another single attribute
		sb = new StringBuilder( "<test arg0=\"closed\" />" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 6 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), sb.length() - 3 );

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 5 ).getXmlRegionType(), XmlRegionType.MARKUP );


		// Two attributes
		sb = new StringBuilder( "<test arg0=\"closed\" arg1=\"not-closed-correctly />" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 7 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), 19 );

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 5 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 5 ).getStart(), 20 );
		Assert.assertEquals( regions.get( 5 ).getEnd(), 24 );

		Assert.assertEquals( regions.get( 6 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 6 ).getStart(), 24 );
		Assert.assertEquals( regions.get( 6 ).getEnd(), sb.length());


		// Two attributes, again
		sb = new StringBuilder( "<test arg0=\"closed\" arg1=\"closed-correctly\"/>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 8 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), 19 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.WHITESPACE );
		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.WHITESPACE );

		Assert.assertEquals( regions.get( 5 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 5 ).getStart(), 20 );
		Assert.assertEquals( regions.get( 5 ).getEnd(), 24 );

		Assert.assertEquals( regions.get( 6 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 6 ).getStart(), 24 );
		Assert.assertEquals( regions.get( 6 ).getEnd(), sb.length() - 2 );

		Assert.assertEquals( regions.get( 7 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 7 ).getStart(), sb.length() - 2 );
		Assert.assertEquals( regions.get( 7 ).getEnd(), sb.length());


		// A nasty attribute
		sb = new StringBuilder( "<test arg0=\"Say \\\"Hi!\\\" Bob.\"/>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 5 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), sb.length() - 2 );

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.MARKUP );


		// A full mark-up with attributes
		sb = new StringBuilder( "<test arg0=\"a1\" arg1=\"b1\"> some value </test>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 10 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 5 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.WHITESPACE );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 2 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 10 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 10 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), 15 );

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.WHITESPACE );

		Assert.assertEquals( regions.get( 5 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE );
		Assert.assertEquals( regions.get( 5 ).getStart(), 16 );
		Assert.assertEquals( regions.get( 5 ).getEnd(), 20 );

		Assert.assertEquals( regions.get( 6 ).getXmlRegionType(), XmlRegionType.ATTRIBUTE_VALUE );
		Assert.assertEquals( regions.get( 6 ).getStart(), 20 );
		Assert.assertEquals( regions.get( 6 ).getEnd(), 25 );

		Assert.assertEquals( regions.get( 7 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 7 ).getEnd() - regions.get( 7 ).getStart(), 1 );

		Assert.assertEquals( regions.get( 8 ).getXmlRegionType(), XmlRegionType.MARKUP_VALUE );

		Assert.assertEquals( regions.get( 9 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 9 ).getStart(), sb.length() - 7 );
		Assert.assertEquals( regions.get( 9 ).getEnd(), sb.length());
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testMarkupValues() throws Exception {

		// Let's try the basics
		StringBuilder sb = new StringBuilder( "<test>Essai</test>" );

		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
		List<XmlRegion> regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 6 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.MARKUP_VALUE );
		Assert.assertEquals( regions.get( 1 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 1 ).getEnd(), 11 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getStart(), 11 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), sb.length());


		// Let's try again with the basics
		sb = new StringBuilder( "<test>  Essai  </test>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 6 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.MARKUP_VALUE );
		Assert.assertEquals( regions.get( 1 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 1 ).getEnd(), 15 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getStart(), 15 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), sb.length());
		
	  // Let's try with something like a XPath
		sb = new StringBuilder( "<test> /Xpath  </test>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 6 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.MARKUP_VALUE );
		Assert.assertEquals( regions.get( 1 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 1 ).getEnd(), 15 );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getStart(), 15 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), sb.length());


		// A mix with comments
		sb = new StringBuilder( "<test> \n <!-- A comment --> \n Essai  </test>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 5 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 6 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.WHITESPACE );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 2 ).getStart(), 9 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 27 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.MARKUP_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 27 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), 37 );

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 4 ).getStart(), 37 );
		Assert.assertEquals( regions.get( 4 ).getEnd(), sb.length());


		// Another mix with comments
		sb = new StringBuilder( "<test> \n <!-- A comment --> \n Essai  <!-- A second comment --></test>" );
		regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 6 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 0 ).getStart(), 0 );
		Assert.assertEquals( regions.get( 0 ).getEnd(), 6 );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.WHITESPACE );

		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 2 ).getStart(), 9 );
		Assert.assertEquals( regions.get( 2 ).getEnd(), 27 );

		Assert.assertEquals( regions.get( 3 ).getXmlRegionType(), XmlRegionType.MARKUP_VALUE );
		Assert.assertEquals( regions.get( 3 ).getStart(), 27 );
		Assert.assertEquals( regions.get( 3 ).getEnd(), 37 );

		Assert.assertEquals( regions.get( 4 ).getXmlRegionType(), XmlRegionType.COMMENT );
		Assert.assertEquals( regions.get( 4 ).getStart(), 37 );
		Assert.assertEquals( regions.get( 4 ).getEnd(), sb.length() - 7 );

		Assert.assertEquals( regions.get( 5 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 5 ).getStart(), sb.length() - 7 );
		Assert.assertEquals( regions.get( 5 ).getEnd(), sb.length());
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testCDataSections() throws Exception {

		// A simple CData section
		StringBuilder sb = new StringBuilder( "<test>" );
		sb.append( "<![CDATA[" );
		sb.append( "<sender>John Smith</sender>" );
		sb.append( "<!-- This is a comment inside a CData section! -->" );
		sb.append( "]]>" );
		sb.append( "</test>" );

		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
		List<XmlRegion> regions = analyzer.analyzeXml( sb.toString());
		testRegionsContiguity( regions, sb.toString());

		Assert.assertEquals( regions.size(), 3 );
		Assert.assertEquals( regions.get( 0 ).getXmlRegionType(), XmlRegionType.MARKUP );
		Assert.assertEquals( regions.get( 2 ).getXmlRegionType(), XmlRegionType.MARKUP );

		Assert.assertEquals( regions.get( 1 ).getXmlRegionType(), XmlRegionType.CDATA );
		Assert.assertEquals( regions.get( 1 ).getStart(), 6 );
		Assert.assertEquals( regions.get( 1 ).getEnd(), sb.length() - 7 );
	}


	/**
	 * This method tests a XML document that was not successfully processed by this class.
	 * <p>
	 * This is more like a performance test.
	 * The implementation was reviewed in consequence because of this test.
	 * </p>
	 * @throws Exception
	 */
	@Test
	public void testExampleThatFailed_1() throws Exception {

		String test = loadResource( "/net/vzurczak/tests/StackOverflowExample.xml" );
		Assert.assertNotNull( test );

		XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
		List<XmlRegion> regions = analyzer.analyzeXml( test );
		testRegionsContiguity( regions, test );
	}


	/**
	 * Verifies that all the XML regions in the list are contiguous.
	 * @param regions the analyzed regions
	 */
	private static void testRegionsContiguity( List<XmlRegion> regions, String xml ) {

		int end = 0;
		for( XmlRegion xr : regions ) {
			Assert.assertEquals( xr.getStart(), end );
			end = xr.getEnd();
		}

		Assert.assertEquals( end, xml.length());
	}


	/**
	 * Loads a resource from the class loader and returns its content as a string.
	 * @param resourceLocation the resource location
	 * @return a string, never null
	 * @throws IOException
	 */
	private static String loadResource( String resourceLocation ) {

		String result = null;
		InputStream in = null;
		try {
			in = XmlRegionAnalyzer.class.getResourceAsStream( resourceLocation );
			if( in != null ) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				byte[] buf = new byte[ 1024 ];
				int len;
				while((len = in.read( buf )) > 0) {
					os.write( buf, 0, len );
				}

				result = os.toString( "UTF-8" );
			}

		} catch( Exception e ) {
			// TODO

		} finally {

			if( in != null ) {
				try {
					in.close();

				} catch( IOException e ) {
					e.printStackTrace();
				}
			}
		}

		return result != null ? result : "";
	}
}
