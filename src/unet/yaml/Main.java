package unet.yaml;

import unet.yaml.variables.YamlObject;

import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main(String[] args)throws Exception {
        File f = new File("/home/brad/IdeaProjects/WebServer/views/config.yml");

        byte[] buf = new byte[(int)f.length()];
        new FileInputStream(f).read(buf);

        long t = System.currentTimeMillis();
        YamlObject y = new YamlObject(buf, 0);

        System.out.println(System.currentTimeMillis()-t);

        System.out.println(y.toString());
    }
}