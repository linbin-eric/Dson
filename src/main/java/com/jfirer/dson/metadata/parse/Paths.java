package com.jfirer.dson.metadata.parse;

import java.util.ArrayList;
import com.jfirer.dson.metadata.parse.Path.Node;

public class Paths
{
	public Path of(String str)
	{
		int pred = 0;
		int offset = 0;
		char c;
		int length = str.length();
		return null;
	}
	
	class PathImpl extends ArrayList<Node>
	{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7756612341422830637L;
		
	}
	
	class NodeImpl implements Node
	{
		private String	name;
		private boolean	inIterator;
		private Integer	index;
		
		public NodeImpl(String name, boolean inIterator, Integer index)
		{
			this.name = name;
			this.inIterator = inIterator;
			this.index = index;
		}
		
		@Override
		public String name()
		{
			return name;
		}
		
		@Override
		public boolean inIterator()
		{
			return inIterator;
		}
		
		@Override
		public Integer index()
		{
			return index;
		}
		
	}
}
