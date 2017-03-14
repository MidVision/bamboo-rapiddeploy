package com.midvision.rapiddeploy.bamboo;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RapidDeployVariablesRetrieverTest {
	
	private RapidDeployVariablesRetriever rapidDeployVariablesRetriever;
	
	@Before
	public void init(){
		rapidDeployVariablesRetriever = new RapidDeployVariablesRetriever();
	}

	@Test
	public void shouldRetrieveVariablesStartingWithExpectedPrefix(){
		Map<String, String> bambooVariables = new HashMap<String, String>();
		bambooVariables.put("rapiddeploy.example1", "example1Value");
		bambooVariables.put("example2", "example2Value");
		bambooVariables.put("rapiddeployale.example3", "example3Value");
		bambooVariables.put("rapiddeploy.example4", "example4Value");
		bambooVariables.put("rapiddeploy.example5", "example5Value");
		rapidDeployVariablesRetriever.setBambooVariables(bambooVariables);
		
		Map<String, String> dictionaryItems = rapidDeployVariablesRetriever.retrieveRapidDeployDictionaryItems();
		
		Assert.assertEquals(dictionaryItems.size(), 3);
		Assert.assertNotNull(dictionaryItems.get("@@example1@@"));
		Assert.assertEquals(dictionaryItems.get("@@example1@@"), "example1Value");
		Assert.assertNotNull(dictionaryItems.get("@@example4@@"));
		Assert.assertNotNull(dictionaryItems.get("@@example5@@"));
	}
}
