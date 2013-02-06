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

package org.easyrec.plugin.profileduke.duke.matchers;

import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.matchers.AbstractMatchListener;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.profileduke.ProfileDukeGenerator;
import org.easyrec.service.core.ItemAssocService;

import java.util.*;

/**
 * ProfileDuke Plugin. <p/> <p><b>Company:&nbsp;</b> SAT,
 * Research Studios Austria</p> <p><b>Copyright:&nbsp;</b> (c) 2012</p> <p><b>last modified:</b><br/> $Author$<br/>
 * $Date$<br/> $Revision$</p>
 *
 * @author Soheil Khosravipour
 * @author Fabian Salcher
 */

public class EasyrecProfileMatcher extends AbstractMatchListener {
    private int matches;
    private int records;
    private int nonmatches; // only counted in record linkage mode
    private boolean showmaybe;
    private boolean showmatches;
    private boolean progress;
    private boolean linkage; // means there's a separate indexing step


    private List<ItemVO<Integer, Integer>> items;
    private static Integer confTanantId;
    private Integer sourceType;
    private Integer viewType;
    private static Integer assocType;
    private ArrayList<ItemAssocVO<Integer, Integer>> itemAssocArrayList;
    private ItemAssocService itemAssocService;

    private HashMap<Integer, HashMap<Integer, Double>> userToUsers =
            new HashMap<Integer, HashMap<Integer, Double>>();

    public void setItemAssocHashMap(HashMap<Integer, HashMap<Integer, Double>> userToUsers) {
        this.userToUsers = userToUsers;
    }

    public HashMap<Integer, HashMap<Integer, Double>> getItemAssocHashMap() {
        return userToUsers;
    }

    public void setItemAssocService(ItemAssocService itemAssocService) {
        this.itemAssocService = itemAssocService;
    }

    public void setConfTanantId(Integer confTanantId) {
        this.confTanantId = confTanantId;
    }

    public void setAssocType(Integer assocType) {
        this.assocType = assocType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public void setViewType(Integer viewType) {
        this.viewType = viewType;
    }

    public void setItemAssocArrayList(ArrayList<ItemAssocVO<Integer, Integer>> itemAssocArrayList) {
        this.itemAssocArrayList = itemAssocArrayList;
    }

    public EasyrecProfileMatcher(boolean showmatches, boolean showmaybe,
                                 boolean progress, boolean linkage) {
        this.matches = 0;
        this.records = 0;
        this.showmatches = showmatches;
        this.showmaybe = showmaybe;
        this.progress = progress;
        this.linkage = linkage;
    }

    public int getMatchCount() {
        return matches;
    }

    public void batchReady(int size) {
        if (linkage)
            records += size; // no endRecord() call in linkage mode
        if (progress)
            ProfileDukeGenerator.logger.info("Records: " + records);
    }

    public void matches(Record r1, Record r2, double confidence) {
        matches++;
        if (showmatches)
            show(r1, r2, confidence, "\nMATCH");
        if (matches % 1000 == 0 && progress)
            ProfileDukeGenerator.logger.info("" + matches + "  matches");
    }

    public void matchesPerhaps(Record r1, Record r2, double confidence) {
        if (showmaybe)
            show(r1, r2, confidence, "\nMAYBE MATCH");
    }

    public void endRecord() {
        records++;
    }

    public void endProcessing() {
        if (progress) {
            ProfileDukeGenerator.logger.info("");
            ProfileDukeGenerator.logger.info("Total records: " + records);
            ProfileDukeGenerator.logger.info("Total matches: " + matches);
            if (nonmatches > 0) // FIXME: this ain't right. we should know the mode
                ProfileDukeGenerator.logger.info("Total non-matches: " + nonmatches);
        }
    }

    public void noMatchFor(Record record) {
        nonmatches++;
        if (showmatches)
            ProfileDukeGenerator.logger.info("\nNO MATCH FOR:\n" + toString(record));
    }

    // =====

    public void show(Record r1, Record r2, double confidence,
                     String heading) {

//        System.out.println(heading + " " + confidence);
//        System.out.println(toString(r1));
//        System.out.println(toString(r2));
        Date execution = new Date();

        // create association  // This lines can be used if you want to produce association between items in future
        ItemAssocVO<Integer, Integer> itemAssoc = new ItemAssocVO<Integer, Integer>(
                confTanantId,
                new ItemVO(r1.getValue("profiletenant"), r1.getValue("profileitem"), r1.getValue("profiletype")),
                assocType, confidence,
                new ItemVO(r2.getValue("profiletenant"), r2.getValue("profileitem"), r2.getValue("profiletype")),
                sourceType, "ProfileDuke Plugin", viewType, null, execution);

        itemAssocService.insertOrUpdateItemAssoc(itemAssoc);
    }

    public static String toString(Record r) {
        StringBuffer buf = new StringBuffer();
        for (String p : r.getProperties()) {
            Collection<String> vs = r.getValues(p);
            if (vs == null)
                continue;

            buf.append(p + ": ");
            for (String v : vs)
                buf.append("'" + v + "', ");
        }

        //buf.append(";;; " + r);
        return buf.toString();
    }
}


