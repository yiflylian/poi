/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.xssf.usermodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import javax.xml.namespace.QName;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ObjectData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;

/**
 * Represents binary object (i.e. OLE) data stored in the file.  Eg. A GIF, JPEG etc...
 */
public class XSSFObjectData extends XSSFSimpleShape implements ObjectData {
    private static final POILogger LOG = POILogFactory.getLogger(XSSFObjectData.class);
    
    /**
     * A default instance of CTShape used for creating new shapes.
     */
    private static CTShape prototype = null;

    private CTOleObject oleObject;

    protected XSSFObjectData(XSSFDrawing drawing, CTShape ctShape) {
        super(drawing, ctShape);
    }

    /**
     * Prototype with the default structure of a new auto-shape.
     */
    protected static CTShape prototype() {
        if(prototype == null) {
            prototype = XSSFSimpleShape.prototype();
        }
        return prototype;
    }

    @Override
    public String getOLE2ClassName() {
        return getOleObject().getProgId();
    }

    /**
     * @return the CTOleObject associated with the shape 
     */
    public CTOleObject getOleObject() {
        if (oleObject == null) {
            long shapeId = getCTShape().getNvSpPr().getCNvPr().getId();
            oleObject = getSheet().readOleObject(shapeId);
            if (oleObject == null) {
                throw new POIXMLException("Ole object not found in sheet container - it's probably a control element");
            }
        }
        return oleObject;
    }
    
    @Override
    public byte[] getObjectData() throws IOException {
        InputStream is = getObjectPart().getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        is.close();
        return bos.toByteArray();
    }
    
    /**
     * @return the package part of the object data
     */
    public PackagePart getObjectPart() {
        if (!getOleObject().isSetId()) {
            throw new POIXMLException("Invalid ole object found in sheet container");
        }
        POIXMLDocumentPart pdp = getSheet().getRelationById(getOleObject().getId());
        return (pdp == null) ? null : pdp.getPackagePart();
    }

    @Override
    public boolean hasDirectoryEntry() {
        InputStream is = null;
        try {
            is = getObjectPart().getInputStream();

            // If clearly doesn't do mark/reset, wrap up
            if (! is.markSupported()) {
                is = new PushbackInputStream(is, 8);
            }

            // Ensure that there is at least some data there
            byte[] header8 = IOUtils.peekFirst8Bytes(is);

            // Try to create
            return NPOIFSFileSystem.hasPOIFSHeader(header8);
        } catch (IOException e) {
            LOG.log(POILogger.WARN, "can't determine if directory entry exists", e);
            return false;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    @SuppressWarnings("resource")
    public DirectoryEntry getDirectory() throws IOException {
        InputStream is = null;
        try {
            is = getObjectPart().getInputStream();
            return new POIFSFileSystem(is).getRoot();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * The filename of the embedded image
     */
    @Override
    public String getFileName() {
        return getObjectPart().getPartName().getName();
    }
    
    protected XSSFSheet getSheet() {
        return (XSSFSheet)getDrawing().getParent();
    }

    @Override
    public XSSFPictureData getPictureData() {
        XmlCursor cur = getOleObject().newCursor();
        try {
            if (cur.toChild(XSSFRelation.NS_SPREADSHEETML, "objectPr")) {
                String blipId = cur.getAttributeText(new QName(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, "id"));
                return (XSSFPictureData)getDrawing().getRelationById(blipId);
            }
            return null;
        } finally {
            cur.dispose();
        }
    }
}
