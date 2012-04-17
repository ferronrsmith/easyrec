/**
 * Copyright 2012 Research Studios Austria Forschungsgesellschaft mBH
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

package org.easyrec.service.core;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemVO;
import org.easyrec.service.core.impl.ProfileServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fsalcher
 */

@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({
        "spring/easyrecDataSource.xml",
        "spring/core/service/ProfileService.xml",
        "spring/core/dao/ProfileDAO.xml",
        "spring/core/dao/IDMappingDAO.xml",
        "spring/domain/service/TypeMappingService.xml"})

@DataSet("/dbunit/core/service/profile.xml")
public class ProfileServiceTest {

    private static final Log logger = LogFactory.getLog(ProfileServiceTest.class);

    //private final TenantVO TENANT_EASYREC = new TenantVO(1, "cluster", "cluster tenant", 1, 10, 5.5d);
    private static final int TENANT_ID = 1;
    private static final String ITEM_ID_SINGLE_VALUE = "1";
    private static final String ITEM_ID_MULTI_VALUE = "2";

    private static final String ITEM_ID_NOT_EXISTING = "99";
    private static final String ITEM_TYPE = "ITEM";

    @SpringBeanByName
    private ProfileServiceImpl profileService;

    @Test
    public void testGetProfile() {
        String profileExpected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><profile><description>Description stored as a profile.</description><name>profileItem</name><property1>propvalue1</property1></profile>";
        String profileActual = profileService.getProfile(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE);
        Assert.assertEquals(profileExpected, profileActual);
    }

    @Test
    public void testStoreProfile() {
        String profileExpected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><profile><description>Description stored as a profile. Plus an additional sentence.</description><name>profileItem</name></profile>";

        // Item exists
        boolean success = profileService.storeProfile(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, profileExpected);
        String profileActual = profileService.getProfile(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE);
        Assert.assertTrue(success);
        Assert.assertEquals(profileExpected, profileActual);

        // Item does not exist
        success = profileService.storeProfile(TENANT_ID, ITEM_ID_NOT_EXISTING, ITEM_TYPE, profileExpected);
        profileActual = profileService.getProfile(TENANT_ID, ITEM_ID_NOT_EXISTING, ITEM_TYPE);
        Assert.assertTrue(success);
        Assert.assertEquals(profileExpected, profileActual);
    }

    @Test
    public void testGetSimpleDimensionValue() {
        String valueExpected = "profileItem";
        String valueActual = profileService.getSimpleDimensionValue(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, "/profile/name");
        Assert.assertEquals(valueExpected, valueActual);
    }

    @Test
    public void testGetMultiDimensionValue() {
        Set<String> valueExpected = new HashSet<String>();
        valueExpected.add("g1");
        valueExpected.add("g2");
        valueExpected.add("g3");

        Set<String> valueActual = profileService.getMultiDimensionValue(
                TENANT_ID, ITEM_ID_MULTI_VALUE, ITEM_TYPE, "/profile/genre");
        Assert.assertEquals(valueExpected, valueActual);
    }

    @Test
    @DataSet("/dbunit/core/service/profile.xml")
    public void testInsertOrUpdateSimpleDimension() {
        String updatePath = "/profile/name";
        String insertPath = "/profile/id";

        String updatedValueExpected = "my new name";
        String insertedValueExpected = "id 1";

        profileService.insertOrUpdateSimpleDimension(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, updatePath, updatedValueExpected);
        profileService.insertOrUpdateSimpleDimension(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, insertPath, insertedValueExpected);

        String updatedValueActual = profileService.getSimpleDimensionValue(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, updatePath);
        String insertedValueActual = profileService.getSimpleDimensionValue(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, insertPath);
        Assert.assertEquals(updatedValueExpected, updatedValueActual);
        Assert.assertEquals(insertedValueExpected, insertedValueActual);
    }

    @Test
    @DataSet("/dbunit/core/service/profile.xml")
    public void testInsertOrUpdateMultiDimension() {
        String updatePath = "/profile/genre";
        String insertPath = "/profile/child";

        List<String> updatedValuesExpected = new ArrayList<String>();
        List<String> insertedValuesExpected = new ArrayList<String>();

        updatedValuesExpected.add("g1");
        updatedValuesExpected.add("g4");
        updatedValuesExpected.add("g5");
        updatedValuesExpected.add("g6");

        insertedValuesExpected.add("c1");
        insertedValuesExpected.add("c2");
        insertedValuesExpected.add("c3");

        profileService.insertOrUpdateMultiDimension(
                TENANT_ID, ITEM_ID_MULTI_VALUE, ITEM_TYPE, updatePath, updatedValuesExpected);
        profileService.insertOrUpdateMultiDimension(
                TENANT_ID, ITEM_ID_MULTI_VALUE, ITEM_TYPE, insertPath, insertedValuesExpected);

        Set<String> updatedValueActual = profileService.getMultiDimensionValue(
                TENANT_ID, ITEM_ID_MULTI_VALUE, ITEM_TYPE, updatePath);
        Set<String> insertedValueActual = profileService.getMultiDimensionValue(
                TENANT_ID, ITEM_ID_MULTI_VALUE, ITEM_TYPE, insertPath);

        // add the values which where already in the profile
        updatedValuesExpected.add("g1");
        updatedValuesExpected.add("g2");
        updatedValuesExpected.add("g3");

        Assert.assertEquals(6, updatedValueActual.size());
        Assert.assertEquals(3, insertedValueActual.size());
        Assert.assertEquals(new HashSet<String>(updatedValuesExpected), updatedValueActual);
        Assert.assertEquals(new HashSet<String>(insertedValuesExpected), insertedValueActual);
    }

    @Test
    @DataSet("/dbunit/core/service/profile.xml")
    public void testDeleteValue() {

        try {
        profileService.deleteProfileField(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE, "/profile/property1");
        } catch (Exception e) {
          e.printStackTrace();
        }

        String profileExpected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><profile><description>Description stored as a profile.</description><name>profileItem</name></profile>";
        String profileActual = profileService.getProfile(TENANT_ID, ITEM_ID_SINGLE_VALUE, ITEM_TYPE);
        Assert.assertEquals(profileExpected, profileActual);
    }

    @Test
    @DataSet("/dbunit/core/service/profile.xml")
    public void testGetItemsByDimensionValue() {

        List<ItemVO<Integer, Integer>> searchActual = profileService.getItemsByDimensionValue(TENANT_ID, ITEM_TYPE, "/profile/property1", "propvalue1");

        List<ItemVO<Integer, Integer>> searchExpected = new ArrayList<ItemVO<Integer, Integer>>();
        searchExpected.add(new ItemVO<Integer, Integer>(TENANT_ID, 1, 1));
        searchExpected.add(new ItemVO<Integer, Integer>(TENANT_ID, 3, 1));

        Assert.assertEquals(searchExpected, searchActual);
    }

    @Test
    @DataSet("/dbunit/core/service/profile.xml")
    public void testGetItemsByItemType() {
        List<ItemVO<Integer, Integer>> actualResult = profileService.getItemsByItemType(TENANT_ID, "ANOTHER_ITEM", 100);

        List<ItemVO<Integer, Integer>> expectedResult = new ArrayList<ItemVO<Integer, Integer>>();
        expectedResult.add(new ItemVO<Integer, Integer>(TENANT_ID, 4, 2));
        expectedResult.add(new ItemVO<Integer, Integer>(TENANT_ID, 5, 2));

        Assert.assertEquals(expectedResult, actualResult);

        actualResult = profileService.getItemsByItemType(TENANT_ID, "ANOTHER_ITEM", 1);
        Assert.assertEquals(1, actualResult.size());
    }
}
