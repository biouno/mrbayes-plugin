package jenkins.plugins.mrbayes;
import hudson.AbortException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

    @DataBoundConstructor
    public MrBayesBuilder(String name, String inputFile) {
        this.name = name;
        this.inputFile = inputFile;
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

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) 
    throws AbortException, InterruptedException, IOException {
        listener.getLogger().println("Invoking MrBayes...");
        
        MrBayesInstallation mrBayesInstallation = DESCRIPTOR.getInstallationByName(this.name);
        if(mrBayesInstallation == null) {
        	throw new AbortException("Invalid MrBayes installation.");
        }
        
        if(StringUtils.isBlank(this.inputFile)) {
        	throw new AbortException("Invalid MrBayes input file.");
        }
        
        final String command = mrBayesInstallation.getPathToExecutable() + " " + this.inputFile;
        listener.getLogger().println("MrBayes command: " + command);
        
        final ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(mrBayesInstallation.getPathToExecutable());
        args.add(this.inputFile);

        Map<String, String> env = build.getEnvironment(listener);
        
        final Integer exitCode = launcher.launch().cmds(args).envs(env).stdout(listener).pwd(build.getModuleRoot()).join();
        
        if(exitCode != 0) {
        	return Boolean.FALSE;
        } else {
        	return Boolean.TRUE;
        }
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public MrBayesBuilderDescriptor getDescriptor() {
        return (MrBayesBuilderDescriptor)super.getDescriptor();
    }

}

