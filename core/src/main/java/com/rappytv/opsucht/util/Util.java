package com.rappytv.opsucht.util;

import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.notification.Notification;
import net.labymod.api.notification.Notification.Type;
import net.labymod.api.util.I18n;

public class Util {

    public static String getTranslation(String key, Object... args) {
        if(!I18n.has(key))
            return key;
        return I18n.getTranslation(key, args);
    }

    public static void notify(String title, String text, Icon icon) {
        Notification.Builder builder = Notification.builder()
            .title(Component.text(title))
            .text(Component.text(text))
            .icon(icon)
            .type(Type.ADVANCEMENT);
        Laby.labyAPI().notificationController().push(builder.build());
    }
}