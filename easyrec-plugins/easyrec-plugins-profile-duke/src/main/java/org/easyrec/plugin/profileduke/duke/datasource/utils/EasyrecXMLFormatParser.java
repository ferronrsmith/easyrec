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
            throws IOException
    {
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
    private void parse() throws IOException
    {
//        BufferedReader in = new BufferedReader(src);
//        line = in.readLine();

        //TODO: get XML Files  one by one


        //    Parse The XML Fileand add the records to handler    \\This is a directory to test
        ProfileDukeGenerator.logger.info("ProfileDuke Plugin. Profile Items Number: " + profileItems.size());

        for (ItemVO<Integer, Integer> item : profileItems) {
            //  String xmlProfile = profileService.getProfile(item);

            int profileTenant = item.getTenant();
            int ProfileItem = item.getItem();
            int profileType = item.getType();
            ProfileDukeGenerator.logger.debug("Profile Item: " + ProfileItem + "profileTenant" +
                    profileTenant + "profileType" + profileType);
            String xmlProfile = profileService.getProfile(item);

            ProfileDukeGenerator.logger.debug("XMLPROFILE: " + xmlProfile);

            xmlParser(xmlProfile, profileTenant, ProfileItem, profileType);
        }
    }


    private void xmlParser(String xmlString, int proTannent, int profItem, int proType) {

        String idString = Integer.toString(proTannent) + Integer.toString(profItem) + Integer.toString(proType);
        int idInteger = Integer.parseInt(idString);
        //File[] profileXMLFiles = getXMLFiles(inFile);

        try {

//            Set<ItemVO<Integer, Integer>> alreadyUsedOtherItems = new HashSet<ItemVO<Integer, Integer>>();
//            INNER:
//            for (int i = 0; i < config.getNumberOfRecs(); i++) {
//                // update the progress using the execution control. the progress will be displayed in the administration
//                // tool or - when using the commandline interface - on stdout.
//                executionControl.updateProgress(currentProgress, MAX_PROGRESS, "Generating item associations.");
//                currentProgress++;
//
//                // GET PROFILE FROM EASYREC PROFILE SYSTEM
//                String xmlProfile = profileService.getProfile(item);
//                int profileTannet = item.getTenant();
//                int profileType = item.getType();
//                int ProfileItem = item.getItem();


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlString));

            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            ProfileDukeGenerator.logger.debug("File: " + idString + " Root element: " +
                    doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("profile");

            String subject = idString;
            Boolean literal = true;
            Boolean numeric = false;
            handler.statement(subject, "ID", idString, literal);
            handler.statement(subject, "profiletenant", Integer.toString(proTannent), literal);
            handler.statement(subject, "profileitem", Integer.toString(profItem), literal);
            handler.statement(subject, "profiletype", Integer.toString(proType), literal);

//            System.out.println("PROPERTY: " + props.get(0).getName() + " Literal?: " + literal.toString());
//            System.out.println("PROPERTY: " + props.get(2).getName() + " Literal?: " + literal.toString());
//            System.out.println("PROPERTY: " + props.get(5).getName() + " Literal?: " + literal.toString());

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;


                    for (int propIndex = 0; propIndex < props.size(); propIndex++) {
                        if (!(props.get(propIndex).getName().equals("ID") || props.get(propIndex).getName().equals("profiletenant") ||
                                props.get(propIndex).getName().equals("profileitem") || props.get(propIndex).getName().equals("profiletype"))) {
                            if (isNumeric(props.get(propIndex).getName())) {
//                                System.out.println("PROPERTY: " + props.get(propIndex).getName() + "   Literal?: " + numeric.toString());
                                handler.statement(subject, props.get(propIndex).getName(), getTagValue(props.get(propIndex).getName(), eElement), numeric);
                            } else {
//                                System.out.println("PROPERTY: " + props.get(propIndex).getName() + "   Literal?: " + literal.toString());
                                handler.statement(subject, props.get(propIndex).getName(), getTagValue(props.get(propIndex).getName(), eElement), literal);
                            }
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

        return nValue.getNodeValue();
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
