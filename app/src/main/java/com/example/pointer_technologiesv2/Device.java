package com.example.pointer_technologiesv2;

public class Device {
    private String local_IP;
    private String name;
    private String id;
    private byte[] icon;
    private String iconCode;

    public Device(String name) {
        this.name=name;
        iconCode=null;
    }

    public Device(String name, String id) {
        this.name = name;
        this.id = id;
        iconCode="";
    }

    public Device() {
    }

    public Device(String name, String id, byte[] icon) {
        this.name = name;
        this.id = id;
        this.icon = icon;
        iconCode=null;
    }

    public Device(String name, String id, String iconCode) {
        this.name = name;
        this.id = id;
        this.iconCode = iconCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getIconCode() {
        return iconCode;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    public String getLocal_IP() {
        return local_IP;
    }

    public void setLocal_IP(String local_IP) {
        this.local_IP = local_IP;
    }
}
