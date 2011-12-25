/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.plugins.mrbayes;

import hudson.AbortException;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;

import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Tests MrBayesBuilder.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestMrBayesBuilder extends HudsonTestCase {

	private MrBayesBuilder builder = null;
	
	private String name = "mrbayes-100.100";
	private String inputFile = "/tmp/input.nex";
	private Boolean enableMrBayesBlockCheck = Boolean.TRUE;
	
	/* (non-Javadoc)
	 * @see org.jvnet.hudson.test.HudsonTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		builder = new MrBayesBuilder(name, inputFile, enableMrBayesBlockCheck);
	}
	
	public void testGetters() {
		assertNotNull(builder.getName());
		assertNotNull(builder.getInputFile());
		assertNotNull(builder.getEnableMrBayesBlockCheck());
		
		assertEquals(builder.getName(), name);
		assertEquals(builder.getInputFile(), inputFile);
		assertEquals(builder.getEnableMrBayesBlockCheck(), enableMrBayesBlockCheck);
	}
	
	public void testMrBayesBlockCheck() {
		PrintStream out = System.out;
		
		final ClassLoader cl = TestMrBayesBuilder.class.getClassLoader();
		
		try {
			URL url = cl.getResource("jenkins/plugins/mrbayes/mrbayesblock1.nex");
			File inputFile = new File( url.getFile() );
			builder.checkMrBayesBlock(inputFile, out);
		} catch (AbortException ae) {
			fail(ae.getMessage());
		}
		
		try {
			URL url = cl.getResource("jenkins/plugins/mrbayes/mrbayesblock2.nex");
			File inputFile = new File( url.getFile() );
			builder.checkMrBayesBlock(inputFile, out);
			fail("Supposed to throw AbortException before getting here");
		} catch (AbortException ae) {
			// OK
		}
		
		try {
			URL url = cl.getResource("jenkins/plugins/mrbayes/mrbayesblock3.nex");
			File inputFile = new File( url.getFile() );
			builder.checkMrBayesBlock(inputFile, out);
			fail("Supposed to throw AbortException before getting here");
		} catch (AbortException ae) {
			// OK
		}
		
		try {
			URL url = cl.getResource("jenkins/plugins/mrbayes/primates.nex");
			File inputFile = new File( url.getFile() );
			builder.checkMrBayesBlock(inputFile, out);
		} catch (AbortException ae) {
			fail(ae.getMessage());
		}
	}
	
}
