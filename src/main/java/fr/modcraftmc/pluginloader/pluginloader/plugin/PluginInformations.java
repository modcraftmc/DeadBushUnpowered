package fr.modcraftmc.pluginloader.pluginloader.plugin;

import java.util.List;

public class PluginInformations {

    private String id;
    private String name;
    private String mainClass;
    private String version;
    private String description;
    private List<String> authors;

    public PluginInformations(String id, String name, String mainClass, String version, String description, List<String> authors) {
        this.id = id;
        this.name = name;
        this.mainClass = mainClass;
        this.version = version;
        this.description = description;
        this.authors = authors;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMainClass() {
        return mainClass;
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
}
