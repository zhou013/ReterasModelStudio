package com.hiveworkshop.rms.ui.util;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Here is how do I implement a costume i18n reader.
 * --Frostlock
 */

public class LanguageReader {
    private static final ResourceBundle rb;
    private static final String currRegion;
    private static final Locale currLocale;
    private static final LanguageSettingsReader settings;

    public static final LanguageReader instance = new LanguageReader();

    static {
        settings = new LanguageSettingsReader();
        System.out.printf("Current language setting: %s%n", settings.getRegion());
        if(settings.getRegion().equals("auto")) {
            currLocale = Locale.getDefault();
            currRegion = String.format("%s_%s", currLocale.getLanguage(), currLocale.getCountry());
            System.out.printf("System language: %s%n", currLocale.getLanguage());
            System.out.printf("System country: %s%n", currLocale.getCountry());
            System.out.printf("System Locale: %s%n", currRegion);
        } else {
            currRegion = settings.getRegion();
            currLocale = new Locale(currRegion);
        }

        rb = getCustomResourceBundle();
        /*
        String a = rb.getString("hello");
        System.out.println(new String(a.getBytes(StandardCharsets.UTF_8)));
        JOptionPane.showMessageDialog(null, String.format("读取: %s", rb.getString("hello")));
        */
    }

    private static ResourceBundle getCustomResourceBundle() {
        ResourceBundle resource = null;
        // Check whether the language file exits or not.
        String proFilePath;
        proFilePath = String.format("%s\\content%s.properties", settings.getLanguageDir(), ("_" + currRegion));
        File content = new File(proFilePath);
        // If specific language file does not exist, switch to default file.
        if (!content.exists()) {
            System.out.printf("%s dose not exist.%n", proFilePath);
            proFilePath = String.format("%s\\content.properties", settings.getLanguageDir());
            content = new File(proFilePath);
            // Try default language file
            if(!content.exists()) {
                System.out.printf("%s dose not exist.%n", proFilePath);
                JOptionPane.showMessageDialog(null,
                        String.format("No language files found in this directory:%n%s%n" +
                                        "Make sure at least \"content.properties\" file is in the directory above.",
                                settings.getLanguageDir()));
                System.exit(0);
            } else {
                System.out.printf("%s exists.%n", proFilePath);
            }
        } else {
            System.out.printf("%s exists.%n", proFilePath);
        }
        try {
            // Read file as UTF-8 encoding.
            InputStreamReader isr = new InputStreamReader(new FileInputStream(proFilePath), StandardCharsets.UTF_8);
            try {
                // This can choose a specific file as a language file.
                resource = new PropertyResourceBundle(isr);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        String.format("Error in loading language file: %s", proFilePath));
                System.exit(0);
            } finally {
                try {
                    isr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            String.format("Error in closing FileReader: %n%s", e1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    String.format("Error in loading file: %s", proFilePath));
            System.exit(0);
        }
        return resource;
    }

    public static String getCurrRegion() {
        return currRegion;
    }

    public static Locale getCurrLocale() {
        return currLocale;
    }

    public static ResourceBundle getRb() {
        return rb;
    }

    public static void main(String[] args) {
        LanguageReader lr = new LanguageReader();
    }
}
