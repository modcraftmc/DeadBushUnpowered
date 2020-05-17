package fr.modcraftmc.pluginloader.plugin;

import java.util.List;

public class Plugin {

    private String id;
    private String mainClass;
    private String displayName;
    private String version;
    private String description;
    private List<String> authors;


    public Plugin(String id, String mainClass, String displayName, String version, String description, List<String> authors) {
        this.id = id;
        this.mainClass = mainClass;
        this.displayName = displayName;
        this.version = version;
        this.description = description;
        this.authors = authors;
    }

    public String getId() {
        return id;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
