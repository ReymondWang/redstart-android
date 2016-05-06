package com.purplelight.redstar.util;

public class EnumHelper {
	
	@SuppressWarnings("rawtypes")
	public static String getName(Class e, int index){
		String name = "";
		
		if (e.isEnum()){
			Object[] objs = e.getEnumConstants();
			
			if (objs.length > index){
				name = objs[index].toString();
			}
		}
		
		return name;
	}
	
	@SuppressWarnings("rawtypes")
	public static int getIndex(Class e, String name){
		int index = 0;
		
		if (e.isEnum()){
			Object[] objs = e.getEnumConstants();
			
			for(int i = 0; i < objs.length; i++){
				if(objs[i].toString() == name){
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
}
