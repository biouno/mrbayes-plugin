# Jenkins MrBayes Plugin

This plug-in integrates Jenkins and MrBayes.

MrBayes is a program for Bayesian inference and model choice across a wide range of phylogenetic and evolutionary models. MrBayes uses Markov chain Monte Carlo (MCMC) methods to estimate the posterior distribution of model parameters

## Basic structure

The plug-in consists of a Builder, which invokes mrbayes passing a NEXUS file 
as input. The program uses a modified version of biojava-legacy API. This 
version was used as the phylo module hasn't been ported to biojava3 package 
yet, and the parsers to Phylip and NEXUS are there.

Using biojava-legacy, by default the plug-in checks whether the user provided 
a mrbayes block. This setting is important when running in a non-interactive 
environment. This setting is enabled by default, but can be disabled, in case 
of a weird behaviour by the plug-in.

The resulting files or mrbayes analysis are kept in the job workspace. The 
console log is saved in the build, and can be backed up, as Jenkins uses 
XML to persist its data.

