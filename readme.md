Maven Archetypes Catalog Plugin for IntelliJ IDEA
=================================================

IntelliJ IDEA lacks the possibility to fetch external Maven Archetype
Catalog files (archetype-catalog.xml). You had to add external Maven
Archetypes by hand.

This plugin for IntelliJ IDEA aims to integrate the possibility to define
a list of external Maven Archetype Catalog files. Those files are
fetched and the containing Maven Archetypes are added to the available
archetypes in IntelliJ IDEA.

Usage
-----

Install the plugin.

It adds a new entry **Maven Archetype Catalogs** to the Settings menu at
**File** - **Settings** - **Build, Execution and Deployment** - **Build tools**.

Here you can add additional URLs pointing to external archetype-catalog.xml files.

After adding those Catalog files the Archetypes present in these files will be available
when creating new Maven projects or Maven modules based on Archetypes.
 

Development
-----------

The project is build up using Maven. The IntelliJ IDEA libraries are not present in the global
Maven repository, so they need to be installed in your local repository or in your company repository.

You can see the paths and the required maven instructions to install the artifacts in the `pom.xml`.

To be able to debug the plugin, you need to install an additional IDEA plugin [Intellij plugin development with Maven](https://plugins.jetbrains.com/plugin/7127?pr=).
This plugin tells IntelliJ IDEA to handle the module as a plugin module instead of a java module.

Additionally you have to manually configure the path to the `plugin.xml` in the module settings. Best
practice would be to point to `target/classes`.

At last, you have to configure an IntelliJ IDEA SDK as the project SDK (instead of a java SDK).


Extensions
----------

This plugin provides an extension point `archetypeCatalogDefinition` in the namespace `de.dm.intellij.maven-archetypes-catalog-plugin`.
Here you can add your custom Archetype Catalog files required for your IntelliJ plugins.
