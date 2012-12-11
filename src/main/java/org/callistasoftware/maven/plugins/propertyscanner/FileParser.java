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

package org.callistasoftware.maven.plugins.propertyscanner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class FileParser {
	private Set<String> keys;
	private Pattern pattern;
	private Log logger;
	
	FileParser(String matchPattern, Log logger){
		keys = new HashSet<String>();
    	this.pattern = Pattern.compile(matchPattern);
    	this.logger = logger;
	}
	
	public void parseFile(File aFile) throws MojoExecutionException{
		logger.info("parsing file: " + aFile);
		Scanner sc = null;
		try {
			sc = new Scanner(aFile);
			
			while(sc.hasNext()){
		       parseLine(sc.nextLine());
			}
			
		} catch (IOException e) {
    		throw new MojoExecutionException("Error in reading file " + aFile, e);
		}finally{
    		try{
			sc.close();
    		}catch (Exception e){
    			//ignore
    		}
		}
	}
	
	private void parseLine(String line){
		Matcher matcher = pattern.matcher(line);
		while(matcher.find()){
			logger.info("matched a string: " + matcher.group(0) + ". Selecting only: " + matcher.group(1));
			//get first group
			keys.add(matcher.group(1));
		}
	}
	
	public Set<String> getKeys(){
		return keys;
	}

}
