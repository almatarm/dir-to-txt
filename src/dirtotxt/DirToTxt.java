/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dirtotxt;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author almatarm
 */
public class DirToTxt {

    FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if( pathname.getName().contains(".idea")) {
                return false;
            }
            if(
                    pathname.getAbsolutePath().contains("/src/main/resources") ||
                    pathname.getName().endsWith("java") ||
                    pathname.getName().endsWith("form") ||
                    pathname.getName().endsWith("xml") ||
                    pathname.getName().endsWith("properties") ||
                    pathname.getName().endsWith("mf") ||
                    pathname.isDirectory()) {
                return true;
            } 
            return false;
        }
    };
    
    public void toTxt(File out, File root) {
        try {
            StringBuilder buff = new StringBuilder();
            process(root, buff, root);
            Files.write(out.toPath(), buff.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(DirToTxt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void process(File file, StringBuilder buff, File root) {
        if(file.isDirectory()) {
            for(File f : file.listFiles(filter)) {
                process(f, buff, root);
            }
        } else {
            appendTxt(file, buff, root);
        }
    }
    
    String prefix = "***$$$@@@ ";
    String postfix = "*** end *** end ***";
    
    private void appendTxt(File file, StringBuilder buff, File root) {
        try {
            String filePath = file.getAbsolutePath().replace(root.getAbsolutePath(), "");
            buff.append(prefix).append(filePath).append("\n");
            String content = new String(Files.readAllBytes(file.toPath()));
            buff.append(content).append("\n").append(postfix).append("\n");
        } catch (IOException ex) {
            Logger.getLogger(DirToTxt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void toDir(File file, File root) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            StringBuilder buff = new StringBuilder();
            File out  = null;
            boolean readingLines = false;
            for(String line : lines) {
                if (line.startsWith(prefix)) {
                    out = new File(root, line.replace(prefix, ""));
                } else if (line.startsWith(postfix)) {
                    createParentDir(out);
                    Files.write(out.toPath(), buff.toString().getBytes());
                    buff = new StringBuilder();
                } else {
                    buff.append(line).append("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DirToTxt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void createParentDir(File file) {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new DirToTxt().toTxt(
//                new File("mybatishelper.txt"), 
//                new File("/Users/almatarm/Dropbox/projects/code/java//util/mybatishelper/"));
//        
//        new DirToTxt().toDir(
//                new File("mybatishelper.txt"), 
//                new File("mybatishelper"));


        new DirToTxt().toTxt(
                new File("java-money.txt"), 
                new File("/Users/almatarm/Dropbox/projects/code/java/money"));
        
//        new DirToTxt().toDir(
//                new File("dirtotxt.txt"), 
//                new File("DirToTxt"));
        
    }
    
}
