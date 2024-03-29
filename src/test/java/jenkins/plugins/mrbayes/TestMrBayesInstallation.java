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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests MrBayesInstallation.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class TestMrBayesInstallation extends TestCase {

	@Test
	public void testInstallationBean() {
		
		final String name = "/home/kinow/biouno/mrbayes/input.nex";
		final String pathToExecutable = "/usr/bin/mb";
		MrBayesInstallation installation = new MrBayesInstallation(name, pathToExecutable);
		
		assertNotNull(installation);
		assertNotNull(installation.getName());
		assertNotNull(installation.getPathToExecutable());
		
		assertEquals(installation.getName(), name);
		assertEquals(installation.getPathToExecutable(), pathToExecutable);
	}
	
}
