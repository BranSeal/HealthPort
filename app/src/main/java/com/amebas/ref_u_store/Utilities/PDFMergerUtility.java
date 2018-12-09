/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amebas.ref_u_store.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDResources;

import org.apache.pdfbox.io.IOUtils;

/**
 * This class will take a list of pdf documents and merge them, saving the
 * result in a new document.
 *
 * @author Ben Litchfield. Edited for this project's use.
 */
public class PDFMergerUtility
{
    private final List<Object> sources;
    private String destinationFileName;

    /**
     * Instantiate a new PDFMergerUtility.
     */
    public PDFMergerUtility()
    {
        sources = new ArrayList<>();
    }

    /**
     * Set the name of the destination file.
     *
     * @param destination The destination to set.
     */
    public void setDestinationFileName(String destination)
    {
        destinationFileName = destination;
    }

    /**
     * Add a source file to the list of files to merge.
     *
     * @param source File representing source document
     *
     * @throws FileNotFoundException If the file doesn't exist
     */
    public void addSource(File source) throws FileNotFoundException
    {
        FileInputStream stream = new FileInputStream(source);
        sources.add(stream);
    }

    /**
     * Merge the list of source documents, saving the result in the destination
     * file.
     *
     * @throws IOException If there is an error saving the document.
     */
    public void mergeDocuments() throws IOException
    {
        PDDocument destination = null;
        try
        {
            destination = new PDDocument();
            PDFCloneUtility cloner = new PDFCloneUtility(destination);

            for (Object sourceObject : sources)
            {
                PDDocument sourceDoc = null;
                try
                {
                    if (sourceObject instanceof File)
                    {
                        sourceDoc = PDDocument.load((File) sourceObject);
                    }
                    else
                    {
                        sourceDoc = PDDocument.load((InputStream) sourceObject);
                    }

                    for (PDPage page : sourceDoc.getPages())
                    {
                        PDPage newPage = new PDPage((COSDictionary) cloner.cloneForNewDocument(page.getCOSObject()));
                        newPage.setCropBox(page.getCropBox());
                        newPage.setMediaBox(page.getMediaBox());
                        newPage.setRotation(page.getRotation());
                        PDResources resources = page.getResources();
                        if (resources != null)
                        {
                            // this is smart enough to just create references for resources that are used on multiple pages
                            newPage.setResources(new PDResources((COSDictionary) cloner.cloneForNewDocument(resources)));
                        }
                        else
                        {
                            newPage.setResources(new PDResources());
                        }
                        destination.addPage(newPage);
                    }
                }
                finally
                {
                    IOUtils.closeQuietly(sourceDoc);
                }
            }
            destination.save(destinationFileName);
        }
        finally
        {
            IOUtils.closeQuietly(destination);
        }
    }
}