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

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Stores information about the installation of MrBayes. This information is 
 * used by the Builder to call MrBayes. This information is stored within a 
 * Descriptor that is, by its turn, used in the Builder.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class MrBayesInstallation implements Serializable {

	private static final long serialVersionUID = 6977811573310920020L;

	/**
	 * A name for a MrBayes installation. This name is referenced in the Job 
	 * set up.
	 */
	private final String name;
	
	/**
	 * Path to MrBayes.
	 */
	private final String pathToExecutable;
	
	/**
	 * @param name the name for a MrBayes installation
	 * @param pathToExecutable the path for a MrBayes executable 
	 */
	@DataBoundConstructor
	public MrBayesInstallation(String name, String pathToExecutable) {
		this.name = name;
		this.pathToExecutable = pathToExecutable;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the pathToExecutable
	 */
	public String getPathToExecutable() {
		return pathToExecutable;
	}
	
}
