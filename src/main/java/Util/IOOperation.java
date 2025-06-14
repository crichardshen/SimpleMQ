package com.SimpleMQ.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IOOperation {
    public static void SaveFile(String storagePath, String fileName, String content)
            throws IOException {
        //define path
        Path filePath = Paths.get(storagePath, fileName);
        //create folder is not existing
        if(!Files.exists(filePath))
        {
            Files.createDirectories(filePath.getParent());
        }
        //write the message
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(String.valueOf(filePath),true)))
        {
            bw.write(content);
            bw.newLine();
        }
        catch (IOException e) {
            throw e;
        }

    }

    public static String ToStringFromByte(byte[] data)
    {
        byte[] receivedData = new byte[data.length];
        System.arraycopy(data,0,receivedData,0,data.length);
        return new String(receivedData);
    }

    public static HashMap<String, List<Path>> GetAllFileAndFolders(String basePath)
            throws IOException {
        HashMap<String,List<Path>> infor = new HashMap<>();
        infor.put("folder",new ArrayList<>());
        infor.put("file",new ArrayList<>());

        Path startPath = Paths.get(basePath);
        Files.walk(startPath).forEach(
                path -> {
                    if(Files.isDirectory(path))
                    {
                        infor.get("folder").add(path);
                    }
                    else
                    {
                        infor.get("file").add(path);
                    }
                }
        );

        return infor;
    }

    public static void HousekeepFolder(Path targetPath){
        if(!Files.isDirectory(targetPath))
        {
            System.out.println("The " + targetPath + " is not a folder path");
            return;
        }

        if(Files.notExists(targetPath))
        {
            System.out.println("The " + targetPath + " is not existed");
            return;
        }

        try {
            Files.walkFileTree(targetPath,new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file); // 删除文件
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                        throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
