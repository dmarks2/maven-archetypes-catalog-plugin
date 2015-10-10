package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import de.dm.intellij.maven.archetypes.Util;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Dominik on 10.10.2015.
 */
public class ArchetypeCatalogApplicationComponent implements ApplicationComponent {

    public static final Logger LOG = Logger.getInstance(ArchetypeCatalogApplicationComponent.class);

    public static final NotificationGroup GROUP_DISPLAY_ID_INFO = new NotificationGroup("Maven Archetype Catalog Plugin", NotificationDisplayType.BALLOON, true);

    public static void notify(String content, NotificationType notificationType) {
        final Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(content, notificationType);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Notifications.Bus.notify(notification);
            }
        });
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return Util.COMPONENT_NAME;
    }
}
