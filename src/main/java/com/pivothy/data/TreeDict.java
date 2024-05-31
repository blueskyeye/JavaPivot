package com.pivothy.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author 石浩炎
 */
public class TreeDict<K, V> {
	private K key;
    private V value;
    private Map<K, TreeDict<K, V>> children;
    
    public TreeDict(K key, V value) {
        this.key = key;
        this.value = value;
        this.children = new LinkedHashMap<>();
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Map<K, TreeDict<K, V>> getChildren() {
        return children;
    }

    public void addChild(K childKey, V childValue) {
    	TreeDict<K, V> childNode = new TreeDict<>(childKey, childValue);
        children.put(childKey, childNode);
    }
    
    public void addChild(TreeDict<K, V> dict) {
    	children.put((K)dict.getKey(), dict);
    }

    public boolean removeChild(K childKey) {
        return children.remove(childKey) != null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
    public int getDepth() {
        return getDepth(this);
    }

    private int getDepth(TreeDict<K, V> node) {
        if (node.isLeaf()) {
            return 1;
        } else {
            int maxDepth = 0;
            for (TreeDict<K, V> child : node.getChildren().values()) {
                int childDepth = getDepth(child);
                if (childDepth > maxDepth) {
                    maxDepth = childDepth;
                }
            }
            return maxDepth + 1;
        }
    }
    
}
