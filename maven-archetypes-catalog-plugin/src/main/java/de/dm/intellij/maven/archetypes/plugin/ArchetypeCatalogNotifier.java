package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;

public class ArchetypeCatalogNotifier {
    private final NotificationGroup GROUP_DISPLAY_ID_INFO = new NotificationGroup("Maven Archetype Catalog Plugin", NotificationDisplayType.BALLOON, true);

    public void notify(String content, NotificationType notificationType) {
        final Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(content, notificationType);
        notification.notify(null);
    }
}
