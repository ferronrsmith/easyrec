/*
 * Copyright 2013 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.easyrec.plugin.profileduke.duke.datasource.utils;

import no.priv.garshol.duke.Property;
import no.priv.garshol.duke.StatementHandler;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.profileduke.ProfileDukeGenerator;
import org.easyrec.service.core.ProfileService;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Profile Based Matching Plugin. <p/> <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2012</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Soheil Khosravipour
 */
public class EasyrecXMLFormatParser {
    private Reader src;
    private StatementHandler handler;
    private int lineno;
    private int pos;
    private String line;
    private static List<Property> props;
    private static List<ItemVO<Integer, Integer>> profileItems;
    private static ProfileService profileService;

//    private    Integer confTanantId;
//    private    Integer sourceType;
//    private    Integer viewType;
//    private Integer assocType;
//    private    Integer itemType;

    public void setItems(List<ItemVO<Integer, Integer>> items) {
        this.profileItems = items;
    }

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }


    /**
     * Reads the NTriples file from the reader, pushing statements into
     * the handler.
     */
    public static void parse(Reader src, StatementHandler handler, ProfileService profileSrv, List<ItemVO<Integer, Integer>> items, List<Property> properties)
            throws IOException {
        profileItems = items;
        profileService = profileSrv;
        props = properties;
        new EasyrecXMLFormatParser(src, handler, profileSrv, items).parse();
    }

    private EasyrecXMLFormatParser(Reader src, StatementHandler handler, ProfileService profileSrv, List<ItemVO<Integer, Integer>> items) {
        this.src = src;
        this.handler = handler;
    }

    /**
     * Alternate entry point to the parser for when the driving loop is
     * outside the parser. Statements get passed to the handler.
     */
    public EasyrecXMLFormatParser(StatementHandler handler) {
        this(null, handler, profileService, profileItems);
    }

    /**
     * Push a line into the parser. If it contains a statement, that
     * statement will be passed to the handler.
     */
/*    public void parseLine(String line) {
      this.line = line;
      parseLine();
    }*/
    private void parse() throws IOException {
//        BufferedReader in = new BufferedReader(src);
//        line = in.readLine();

        //TODO: get XML Files  one by one


        //    Parse The XML Fileand add the records to handler    \\This is a directory to test
        ProfileDukeGenerator.logger.info("ProfileDuke Plugin. Profile Items Number: " + profileItems.size());

        for (ItemVO<Integer, Integer> item : profileItems) {
            //  String xmlProfile = profileService.getProfile(item);

            String xmlProfile = profileService.getProfile(item);
            if (xmlProfile == null)
                continue;

            int profileTenant = item.getTenant();
            int ProfileItem = item.getItem();
            int profileType = item.getType();
            ProfileDukeGenerator.logger.debug("Profile Item: " + ProfileItem + "profileTenant" +
                    profileTenant + "profileType" + profileType);

            ProfileDukeGenerator.logger.debug("XMLPROFILE: " + xmlProfile);

            xmlParser(xmlProfile, profileTenant, ProfileItem, profileType);
        }
    }

    /**
     * Takes an XML string with the profile and creates statements out
     * of the properties for the StatementHandler
     *
     * @param xmlString string with the profile XML
     * @param proTenant tenantId of the actual tenant
     * @param profItem itemId of the item with the profile
     * @param proType itemType of the item with the profile
     */

    private void xmlParser(String xmlString, int proTenant, int profItem, int proType) {

        String idString = Integer.toString(proTenant) + Integer.toString(profItem) + Integer.toString(proType);

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlString));


            String subject = idString;
            handler.statement(subject, "ID", idString, true);
            handler.statement(subject, "profiletenant", Integer.toString(proTenant), true);
            handler.statement(subject, "profileitem", Integer.toString(profItem), true);
            handler.statement(subject, "profiletype", Integer.toString(proType), true);

            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            ProfileDukeGenerator.logger.debug("File: " + idString + " Root element: " +
                    doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("profile");


            // create a map of props to check later if the properties from the profile are
            // also in the duke configuration
            HashSet<String> propertyNames = new HashSet<String>(props.size());
            for (Property property: props)
                propertyNames.add(property.getName());

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    NodeList propertyNodes = element.getElementsByTagName("*");
                    for (int j = 0; j < propertyNodes.getLength(); j++) {
                        String propertyName = propertyNodes.item(j).getNodeName();
                        String propertyValue = propertyNodes.item(j).getChildNodes().item(0).getNodeValue();
                        if (propertyNames.contains(propertyName)) {
                            handler.statement(subject, propertyName, propertyValue, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        if (nValue != null)
            return nValue.getNodeValue();
        else
            return null;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }


    public static File[] getXMLFiles(File folder) {
        List<File> aList = new ArrayList<File>();

        File[] files = folder.listFiles();
        for (File pf : files) {

            if (pf.isFile() && getFileExtensionName(pf).indexOf("xml") != -1) {
                aList.add(pf);
            }
        }
        return aList.toArray(new File[aList.size()]);
    }

    public static String getFileExtensionName(File f) {
        if (f.getName().indexOf(".") == -1) {
            return "";
        } else {
            return f.getName().substring(f.getName().length() - 3, f.getName().length());
        }
    }
}
