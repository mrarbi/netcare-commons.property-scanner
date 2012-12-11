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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class MyDirectoryWalker extends DirectoryWalker<File> {
    public MyDirectoryWalker(IOFileFilter fileFilter) {
        super(HiddenFileFilter.VISIBLE, fileFilter, -1);
        
    }
 
    protected void handleFile(File file, int depth, Collection<File> results) throws IOException{
    	results.add(file);
    }
    
    public List<File> getAllFiles(File startDirectory) throws IOException{
    	List<File> results = new ArrayList<File>();
    	walk(startDirectory, results);
    	return results;
    }
}
