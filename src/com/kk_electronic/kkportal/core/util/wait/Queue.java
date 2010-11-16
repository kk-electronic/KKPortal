package com.kk_electronic.kkportal.core.util.wait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Queue<T> {
	HashMap<Object,List<T>> waiting = new HashMap<Object, List<T>>();
	

	public void enque(List<T> elements,Object waitsource){
		if(waiting.containsKey(waitsource)){
			waiting.get(waitsource).addAll(elements);
		} else {
			waiting.put(waitsource, elements);
		}
	}

	public void enque(T element,Object waitsource){
		if(waiting.containsKey(waitsource)){
			waiting.get(waitsource).add(element);
		} else {
			List<T> elements = new ArrayList<T>();
			elements.add(element);
			waiting.put(waitsource,elements);
		}
	}
	
	public List<T> deque(Object waitsource){
		if(waiting.containsKey(waitsource)){
			List<T> retval = waiting.remove(waitsource);
			return retval;
		}
		return Collections.emptyList();
	}
}
