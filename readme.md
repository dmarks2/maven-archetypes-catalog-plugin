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

It adds a new entry "Maven Archetype Catalogs" to the Settings menu at
**File** - **Settings** - **Build, Execution and Deployment** - **Build tools**.

Here you can add additional URLS pointing to external archetype-catalog.xml files.

After adding those Catalog files the Archetypes present in these files will be available
when creating new Maven projects or Maven modules based on Archetypes.
 
 
