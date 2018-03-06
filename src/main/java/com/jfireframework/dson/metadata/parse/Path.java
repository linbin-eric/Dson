package com.jfireframework.dson.metadata.parse;

public interface Path
{
	interface Node
	{
		String name();
		
		boolean inIterator();
		
		/**
		 * 如果节点是处于数组或者集合中，返回所需节点的下标。
		 * 
		 * @return
		 */
		int index();
	}
}
