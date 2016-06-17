package com.majorpotato.febridge.util.cache;


public class PermNode {
    protected String node;
    protected String value;

    public PermNode(String node, String value) {
        this.node = node;
        this.value = value;
    }

    public String getNode() { return node; }
    public String getValue() { return value; }
}
