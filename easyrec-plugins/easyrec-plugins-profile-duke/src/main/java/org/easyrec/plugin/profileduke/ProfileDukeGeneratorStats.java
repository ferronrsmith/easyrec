package org.easyrec.plugin.profileduke;

import org.easyrec.plugin.stats.GeneratorStatistics;

/**
 * @author fkleedorfer
 */
public class ProfileDukeGeneratorStats extends GeneratorStatistics {
    private int numberOfItems = 0;

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void incNumberOfItems() {
        this.numberOfItems++;
    }

}
