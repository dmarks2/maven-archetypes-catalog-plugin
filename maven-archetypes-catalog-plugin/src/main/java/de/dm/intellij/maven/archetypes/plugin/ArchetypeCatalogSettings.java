package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 03.10.2015.
 */
@State(
        name = "ArchetypeCatalogSettings",
        storages = {
                @Storage("archetypeCatalogs.xml")
        }
)
public class ArchetypeCatalogSettings implements PersistentStateComponent<ArchetypeCatalogSettings> {

    private Set<String> archetypeCatalogs = new HashSet<String>();
    private boolean skipRepository;

    @NotNull
    public static ArchetypeCatalogSettings getInstance() {
        return ServiceManager.getService(ArchetypeCatalogSettings.class);
    }

    @NotNull
    @Property(surroundWithTag = false)
    @AbstractCollection(surroundWithTag = false, elementTag = "archetype-catalog", elementValueAttribute = "")
    public Set<String> getUrls() {
        return archetypeCatalogs;
    }

    public void setUrls(@NotNull Set<String> urls) {
        if (archetypeCatalogs != urls) {
            archetypeCatalogs.clear();
            archetypeCatalogs.addAll(urls);
        }
    }

    public boolean isSkipRepository() {
        return skipRepository;
    }

    public void setSkipRepository(boolean skipRepository) {
        this.skipRepository = skipRepository;
    }

    @Nullable
    @Override
    public ArchetypeCatalogSettings getState() {
        return this;
    }

    @Override
    public void loadState(ArchetypeCatalogSettings state) {
        archetypeCatalogs.clear();
        archetypeCatalogs.addAll(state.getUrls());
        skipRepository = state.isSkipRepository();
    }
}
