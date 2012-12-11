package org.callistasoftware.maven.plugins.propertyscanner;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Goal which check for unused and required properties.
 *
 * @goal check-strings
 * 
 * @phase process-sources
 */
public class MyMojo
    extends AbstractMojo
{
	private static final String PROPERTY_FOUND_FLAG = "__found";

    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;
    
    /**
     * Location of scripts.
     * @parameter expression="${check-strings.javaSourceDirectory}"
     * @required
     */
    private File javaSourceDirectory;     
    
    /**
     * Location of scripts.
     * @parameter expression="${check-strings.scriptSourceDirectory}"
     * @required
     */
    private File scriptSourceDirectory;    
    
    /**
     * Script match pattern.
     * @parameter expression="${check-strings.javaMatchPattern}" default-value="\"([a-z]+?\\..+?)\""
     * @required
     */
    private String javaMatchPattern;    
    
    /**
     * Script match pattern.
     * @parameter expression="${check-strings.scriptMatchPattern}" default-value="<spring:message\\s+code=\"(.+?)\"\\s+/>"
     * @required
     */
    private String scriptMatchPattern;
    
    /**
     * Location of properties files.
     * @parameter expression="${check-strings.propertiesDirectory}"
     * @required
     */
    private File[] propertiesDirectories;
    
    private Log logger;

    public void execute()
        throws MojoExecutionException
    {
    	logger = getLog();
    	
    	//load all properties
    	Properties properties = loadProperties( logger);
    	
    	Set<String> allTokens = new HashSet<String>();
    	//read all jsp files
    	allTokens.addAll(scanJspFiles(logger));
    	
    	//read all java files
    	allTokens.addAll(scanJavaFiles(logger));
    	
    	//verify that all tokens are present in properties files
    	for (String token : allTokens){
    		if(!properties.containsKey(token)){
    			logger.warn("could not find translation for key: " + token);
    		}else{
    			properties.setProperty(token, PROPERTY_FOUND_FLAG);
    		}
    	}
    	
    	for (String key : properties.stringPropertyNames()){
    		if(properties.getProperty(key) != PROPERTY_FOUND_FLAG){
    			logger.warn("Unused property found, key:" + key + " and value:" + properties.getProperty(key));
    		}
    	}
    }
    
    private Properties loadProperties(Log logger) throws MojoExecutionException{
    	FilenameFilter filter = new SuffixFileFilter(".properties");
    	Properties allProperties = new Properties();
    	
    	for (File propertiesDirectory : propertiesDirectories){
    		if (!propertiesDirectory.exists()){
    			throw new MojoExecutionException("Could not find properties directory: " + propertiesDirectory);
    		}
    		
        	File[] propertiesFiles = propertiesDirectory.listFiles(filter);
        	for (File propertiesFile : propertiesFiles){
            	if (!propertiesFile.exists()){
            		throw new MojoExecutionException("Could not find properties file: " + propertiesFile);
            	}
            	
    	    	//loading properties
    	    	Properties properties = new Properties();
    	    	FileReader r = null;
    	    	try{
    	    		r = new FileReader(propertiesFile);
    	    		properties.load(r);
    	    	}
    	    	catch(IOException e){
    	    		throw new MojoExecutionException("Error loading properties from translation file: " + propertiesFile, e);
    	    	}finally{
    	    		try{
    	    			r.close();
    	    		}catch (Exception e){
    	    			//nothing
    	    		}
    	    	}
    	    	logger.debug("Loaded properties, read " + properties.size() + " entries");
    	    	allProperties.putAll(properties);
        	}
    	}
    	logger.info("Total properties loaded: " + allProperties.size());
    	return allProperties;
    }
    
    private Set<String> scanJspFiles(Log logger) throws MojoExecutionException{
    	logger.info("JSP source directory is " + scriptSourceDirectory);
    	
    	MyDirectoryWalker jspDirectoryWalker = new MyDirectoryWalker(FileFilterUtils.suffixFileFilter(".jsp"));
    	List<File> allJspFiles;
		try {
			allJspFiles = jspDirectoryWalker.getAllFiles(scriptSourceDirectory);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to walk through jsp directories: ", e);
		}
    	
    	FileParser fileParser = new FileParser(scriptMatchPattern, logger); 
    	for (File jspFile: allJspFiles.toArray(new File[allJspFiles.size()])){
    		fileParser.parseFile(jspFile);
    	}
    	
    	logger.info("the keys found in jsp are: " + fileParser.getKeys());
    	return fileParser.getKeys();	
    }
    
    private Set<String> scanJavaFiles(Log logger) throws MojoExecutionException{
    	logger.info("Java source directory is " + javaSourceDirectory);
    	
    	MyDirectoryWalker javaDirectoryWalker = new MyDirectoryWalker(FileFilterUtils.suffixFileFilter(".java"));
    	List<File> allJavaFiles;
		try {
			allJavaFiles = javaDirectoryWalker.getAllFiles(javaSourceDirectory);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to walk through java directories: ", e);
		}
    	
    	FileParser fileParser = new FileParser(javaMatchPattern, logger); 
    	for (File javaFile: allJavaFiles.toArray(new File[allJavaFiles.size()])){
    		fileParser.parseFile(javaFile);
    	}
    	
    	logger.info("the keys found in java are: " + fileParser.getKeys());
    	return fileParser.getKeys();	
    }
}
