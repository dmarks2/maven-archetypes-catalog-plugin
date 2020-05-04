package de.dm.intellij.maven.archetypes;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;

public final class PasswordManager {

    private static final Logger LOG = Logger.getInstance(PasswordManager.class);

    public static void storePassword(final String key, final String value) {
        UIUtil.invokeAndWaitIfNeeded(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PasswordSafe.getInstance().setPassword(new CredentialAttributes(PasswordManager.class.getName(), key), value);
                            success = true;
                        } catch (Exception e) {
                            Messages.showErrorDialog("Cannot store password\n" + e.getMessage(), "Error");
                            success = false;
                        }
                    }
                }
        );
    }

    private static String password;
    public static boolean success;

    public static String loadPassword(final String key) {
        UIUtil.invokeAndWaitIfNeeded(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            password = PasswordSafe.getInstance().getPassword(new CredentialAttributes(PasswordManager.class.getName(), key));
                        } catch (Exception e) {
                            Messages.showErrorDialog("Cannot load password\n" + e.getMessage(), "Error");
                        }
                    }
                }
        );
        return password;
    }
}