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
import hudson.Extension;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

import jenkins.plugins.mrbayes.util.Messages;

import org.apache.commons.lang.StringUtils;
import org.biojava.bio.seq.io.ParseException;
import org.biojavax.bio.phylo.io.nexus.MrBayesBlock;
import org.biojavax.bio.phylo.io.nexus.NexusBlock;
import org.biojavax.bio.phylo.io.nexus.NexusFile;
import org.biojavax.bio.phylo.io.nexus.NexusFileBuilder;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * MrBayes builder.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
public class MrBayesBuilder extends Builder {

	@Extension
	public static final MrBayesBuilderDescriptor DESCRIPTOR = new MrBayesBuilderDescriptor();
	
    private final String name;
    
    private final String inputFile;
    
    private final Boolean enableMrBayesBlockCheck;

    @DataBoundConstructor
    public MrBayesBuilder(String name, String inputFile, Boolean enableMrBayesBlockCheck) {
        this.name = name;
        this.inputFile = inputFile;
        this.enableMrBayesBlockCheck = ((enableMrBayesBlockCheck == null) ? Boolean.TRUE : enableMrBayesBlockCheck);
    }

    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
    
    /**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}
	
	/**
	 * @return the enableMrBayesBlockCheck
	 */
	public Boolean getEnableMrBayesBlockCheck() {
		return ((enableMrBayesBlockCheck==null ? Boolean.TRUE : enableMrBayesBlockCheck));
	}

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) 
    throws AbortException, InterruptedException, IOException {
        listener.getLogger().println(Messages.MrBayesBuilder_InvokingMrBayes());
        
        MrBayesInstallation mrBayesInstallation = DESCRIPTOR.getInstallationByName(this.name);
        if(mrBayesInstallation == null) {
        	throw new AbortException(Messages.MrBayesBuilder_InvalidMrBayesInstallation());
        }
        
        if(StringUtils.isBlank(this.inputFile)) {
        	throw new AbortException(Messages.MrBayesBuilder_InvalidMrBayesInputFile());
        }
        
        final FilePath workspace = build.getWorkspace();
        
        if(this.getEnableMrBayesBlockCheck() != null && this.getEnableMrBayesBlockCheck() == Boolean.TRUE) {
        	workspace.act(new FileCallable<Void>() {

        		private static final long serialVersionUID = -1120251038503180781L;

				/* (non-Javadoc)
        		 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
        		 */
        		public Void invoke(File f, VirtualChannel channel)
        				throws IOException, InterruptedException {
        			checkMrBayesBlock(new File(f, inputFile), listener.getLogger()); // TBD: adapt to run in remote slave too
        			return null;
        		}
			});
        }
        
        final String command = mrBayesInstallation.getPathToExecutable() + " " + this.inputFile;
        listener.getLogger().println(Messages.MrBayesBuilder_MrBayesCommand(command));
        
        final ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(mrBayesInstallation.getPathToExecutable());
        
        args.add(this.inputFile);

        Map<String, String> env = build.getEnvironment(listener);
        
        final Integer exitCode = launcher.launch().cmds(args).envs(env).stdout(listener).pwd(build.getModuleRoot()).join();
        
        if(exitCode != 0) {
        	listener.getLogger().println(Messages.MrBayesBuilder_ErrorExecutingMrBayes(exitCode));
        	return Boolean.FALSE;
        } else {
        	listener.getLogger().println(Messages.MrBayesBuilder_Success());
        	return Boolean.TRUE;
        }
    }

    /**
     * Checks if a NEXUS file contains mrbayes block and, if so, if the proper 
     * settings for running as non-interactive have been enabled. Raises an 
     * AbortException if any of the previous predicates are not true.
     */
    protected void checkMrBayesBlock(final File nexusFile, PrintStream out) throws AbortException {
    	
    	FileInputStream fis = null;
    	
    	try {
    		fis = new FileInputStream(nexusFile);
	    	final NexusFileBuilder builder = new NexusFileBuilder();
	    	NexusFileFormat.parseInputStream(builder, fis);
	    	final NexusFile nexus1 = builder.getNexusFile();
	    	final MrBayesBlock block = getMrBayesBlock(nexus1);
	    	
	    	if(block == null) {
	    		throw new AbortException(Messages.MrBayesBuilder_MissingMrBayesBlock(nexusFile));
	    	}
	    	
	    	if(block.getAutoclose() == null || block.getAutoclose() == Boolean.FALSE) {
	    		throw new AbortException(Messages.MrBayesBuilder_NotConfiguredToAutoclose());
	    	}
	    	
	    	out.println("autoclose=yes");
	    	out.println("nowarn="+((block.getNowarn()!=null&&block.getNowarn()==Boolean.TRUE)?"yes":"no"));
	    	
	    	if(StringUtils.isNotBlank(block.getExecute())) {
	    		out.println(Messages.MrBayesBuilder_CallingExternalFile(block.getExecute().trim()));
	    	}
    	} catch (IOException ioe) {
    		ioe.printStackTrace(out);
    		throw new AbortException(Messages.MrBayesBuilder_ErrorReadingNexus(nexusFile, ioe.getMessage()));
    	} catch (ParseException pe) {
    		pe.printStackTrace(out);
    		throw new AbortException(Messages.MrBayesBuilder_ErrorParsingNexus(nexusFile, pe.getMessage()));
    	} finally {
    		if(fis!=null) {
    			try {
					fis.close();
				} catch (IOException ioe) {
					ioe.printStackTrace(out);
				}
    		}
    	}
	}
    
    /**
     * Returns the mrbayes block in a NEXUS file.
     * 
     * @param nexus a NEXUS file.
     * @return the mrbayes block or <code>null</code>.
     */
    private MrBayesBlock getMrBayesBlock(NexusFile nexus) {
		Iterator<?> it = nexus.blockIterator();
		NexusBlock block;
		while (it.hasNext()) {
			block = (NexusBlock) it.next();
			if (block.getBlockName().equals(MrBayesBlock.MRBAYES_BLOCK)) {
				return (MrBayesBlock) block;
			}
		}
		return null;
	}

	// Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public MrBayesBuilderDescriptor getDescriptor() {
        return (MrBayesBuilderDescriptor)super.getDescriptor();
    }

}

