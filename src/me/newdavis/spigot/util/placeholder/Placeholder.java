package me.newdavis.spigot.util.placeholder;

public class Placeholder {

    private final String identifier;
    private String value;

    public Placeholder(String identifier, String value) {
        this.identifier = identifier;
        this.value = value;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getValue() {
        return this.value;
    }

    public void updateValue(String value) {
        this.value = value;
    }

}
