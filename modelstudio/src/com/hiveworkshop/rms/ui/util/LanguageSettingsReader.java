package com.hiveworkshop.rms.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import javax.swing.*;

public class LanguageSettingsReader {
    private static String region;
    private static String languageDir;

    public static final LanguageSettingsReader instance = new LanguageSettingsReader();

    static {
        String path = null;
        try {
            Yaml yaml = new Yaml();
            File directory = new File(".");
            path = directory.getCanonicalPath();
            System.out.println("============Language Settings Reader Start============");
            languageDir = String.format("%s\\Languages", path);
            System.out.printf("Current Path: %s%n", path);
            System.out.printf("Current Language Path: %s%n", languageDir);
            File settingFile = new File(languageDir + "\\Settings.yml");
//            Object load =yaml.load(new FileInputStream(settingFile));
//            System.out.println(yaml.dump(load));
            Map<String, String> map =yaml.load(new FileInputStream(settingFile));
            System.out.println(map);
            for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                String key = stringStringEntry.getKey();
                String val = stringStringEntry.getValue();

                if (key.equals("region")) {
                    System.out.printf("key: %s, value: %s%n", key, val);
                    region = val;
                }
            }
            System.out.println("=============Language Settings Reader End=============");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    String.format("Reading %s\\Languages\\Settings.yml failed.", path));
        }
    }

    public String getRegion() {
        return region;
    }

    public String getLanguageDir(){
        return languageDir;
    }

}
