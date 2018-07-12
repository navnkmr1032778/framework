package com.solutionstar.swaftee.constants.enums;

public enum AffinityRECos {
	REALESTATECENTER("REC"),
	XOMERETAIL("XOMERETAIL"),
	FSBO("FSBO"),
	MRCOOPER("MRCOOPER"),
	NORECO("NORECO");
	
	private String recoID;
	
	AffinityRECos(String recoID){
		this.recoID = recoID;
	}
	
	public String getRecoID() {
		return this.recoID;
	}
}
