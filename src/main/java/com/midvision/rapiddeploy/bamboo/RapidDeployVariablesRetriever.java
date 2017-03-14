package com.midvision.rapiddeploy.bamboo;

import java.util.HashMap;
import java.util.Map;

public class RapidDeployVariablesRetriever {

	private static final String RAPIDDEPLOY_PREFIX = "rapiddeploy.";
	private Map<String, String> bambooVariables;
	
	public RapidDeployVariablesRetriever(){
		this.bambooVariables = new HashMap<String, String>();
	}
	
	public RapidDeployVariablesRetriever(Map<String, String> bambooVariables){
		this.bambooVariables = bambooVariables;
	}
	
	public Map<String, String> retrieveRapidDeployDictionaryItems(){
		if(bambooVariables == null || bambooVariables.isEmpty()){
			return new HashMap<String, String>();
		}
		Map<String, String> dataDictionary = new HashMap<String, String>();
		for(String variableKey : bambooVariables.keySet()){
			if(variableKey.startsWith(RAPIDDEPLOY_PREFIX)){
				String rdDictionaryKey = variableKey.substring(RAPIDDEPLOY_PREFIX.length());
				StringBuilder dictionaryKeyBuilder = new StringBuilder("@@");
				dictionaryKeyBuilder.append(rdDictionaryKey);
				dictionaryKeyBuilder.append("@@");
				dataDictionary.put(dictionaryKeyBuilder.toString(), bambooVariables.get(variableKey));
			}
		}
		return dataDictionary;
	}
	
	public Map<String, String> getBambooVariables() {
		return bambooVariables;
	}
	
	public void setBambooVariables(Map<String, String> bambooVariables) {
		this.bambooVariables = bambooVariables;
	}
}
