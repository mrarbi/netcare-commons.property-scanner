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

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class JspParser extends DefaultHandler {
	private Boolean inElement;
	private StringBuffer contents;
	private Set<String> keys;
	
	public JspParser(){
		super();
	}
	
	@Override
	public void startElement(String uri, String name,
		      String qName, Attributes atts){
		if(name.equals("spring:message")){
			inElement = Boolean.TRUE;
			contents = new StringBuffer();
		}
	}
	
	@Override
    public void endElement (String uri, String name, String qName){
		if(name.equals("spring:message")){
			inElement = Boolean.FALSE;
			store(contents.toString());
		}
	}
	
	@Override
	public void characters (char ch[], int start, int length){
		if(inElement){
			contents.append(ch);
		}
    }
	
	private void store(String value){
		if(keys == null){
			keys = new HashSet<String>();
		}
		keys.add(value);
	}
	
	public Set<String> getKeys(){
		return keys;
	}

}
