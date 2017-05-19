package com.trafficcast.operator.traverse;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
	T node;
	List<TreeNode<T>> children;
	
	public TreeNode(T n) {
		node = n;
	}
	
	public TreeNode<T> addChild(T n) {
		TreeNode<T> e = new TreeNode<T>(n);
		addChild(e);
		return e;
	}
	
	public void addChild(TreeNode<T> n) {
		if (children == null) {
			children = new ArrayList<TreeNode<T>>();
		}
		children.add(n);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj instanceof TreeNode<?>) {
			TreeNode<?> that = (TreeNode<?>) obj;
			if (this.node == null)
				return that.node == null;
			else
				return this.node.equals(that.node);
		}
		
		return false;
	}
}
