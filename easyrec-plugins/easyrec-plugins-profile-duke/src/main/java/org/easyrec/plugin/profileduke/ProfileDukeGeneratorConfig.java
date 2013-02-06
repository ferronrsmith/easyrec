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

package org.easyrec.plugin.profileduke;

import org.easyrec.plugin.configuration.PluginParameter;
import org.easyrec.plugin.generator.GeneratorConfiguration;

/**
 * Configuration object for the demo plugin. <p/> This class contains all parameters that can be configured and are
 * needed for the plugin to work correctly. <p/> <p><b>Company:&nbsp;</b> SAT, Research Studios Austria</p>
 * <p><b>Copyright:&nbsp;</b> (c) 2007</p> <p><b>last modified:</b><br/> $Author$<br/> $Date$<br/> $Revision$</p>
 *
 * @author Soheil Khosravipour
 * @author Fabian Salcher
 */
public class ProfileDukeGeneratorConfig extends GeneratorConfiguration {
    // ------------------------------ FIELDS ------------------------------

    // each configuration value needs to be annotaed with @PluginParameter.
    // displayName      - is the string that will be displayed for the value in the administration tool.
    // shortDescription - will be the first paragrah of the description when the help button is pressed in the admin tool.
    // description      - is the second paragraph displayed in the admin tool.
    //
    // each config value should be initialized with a default value. when a new configuration object is created
    // all config values are initialized with the default values and the configuration is named "Default Configuration" in
    // the superclass (GeneratorConfiguration.)


    @PluginParameter(description = "Absolute path to your DUKE config file.",
            displayName = "Duke Config:",
            shortDescription = "", displayOrder = 1)
    // TODO: change to a reasonable default value
    private String dukeConfig = "file:///C:\\DATA\\fsalcher\\_workspace\\dukeConfig.xml"; //"file:///tmp/dukeConfig.xml"

    @PluginParameter(description = "ItemType of the items which are to be matched.",
            displayName = "ItemType:",
            shortDescription = "", displayOrder = 2)
    private String itemType = "ITEM";

    @PluginParameter(description = "<b> Allowed Values: true / false </b> <br> " +
            "When this mode is active the items are assigned randomly to N blocks and " +
            "then the similarity is calculated only within these blocks. You can configure " +
            "the number of blocks in the \"[Block Calculation] number of blocks\" setting.",
            displayName = "[Block Calculation] enabled:",
            shortDescription = "", displayOrder = 10)
    private String blockCalculationMode = "false";

    @PluginParameter(description = "Defines the number of blocks used in the Block Calculation Mode.",
            displayName = "[Block Calculation] number of blocks:",
            shortDescription = "", displayOrder = 11)
    private Integer blockCalculationNumberOfBlocks = 10;

    private String viewType = "SYSTEM";

    // --------------------- GETTER / SETTER METHODS ---------------------

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(final String viewType) {
        this.viewType = viewType;
    }

    public String getDukeConfig() {
        return dukeConfig;
    }

    public void setDukeConfig(String dukeConfig) {
        this.dukeConfig = dukeConfig;
    }

    public String getBlockCalculationMode() {
        return blockCalculationMode;
    }

    public void setBlockCalculationMode(String blockCalculationMode) {
        this.blockCalculationMode = blockCalculationMode;
    }

    public Integer getBlockCalculationNumberOfBlocks() {
        return blockCalculationNumberOfBlocks;
    }

    public void setBlockCalculationNumberOfBlocks(Integer blockCalculationNumberOfBlocks) {
        this.blockCalculationNumberOfBlocks = blockCalculationNumberOfBlocks;
    }
}
