package server.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class splice {
    private static final String dir="C:\\Users\\ylchen\\Desktop\\upload\\00000000-0000-0009-0000-000000000008\\00000000-0000-0006-0000-00000000090f";

    public static void main(String[] args) throws Exception{
        File Dir=new File(dir);
        File[] files=Dir.listFiles();
        int[] name=new int[files.length];
        for(int i=0;i<files.length;i++){
            name[i]=Integer.parseInt(files[i].getName());
        }
        Arrays.sort(name);
        RandomAccessFile randomFile = new RandomAccessFile(dir+File.separatorChar+"splice.wav", "rw");
        for(int i=0;i<name.length;i++){
            File file=new File(dir+File.separatorChar+name[i]);
            System.out.println("处理："+file.getAbsolutePath());
            byte[] data=new byte[1024];
            FileInputStream in=new FileInputStream(file);
            while(in.read(data,0,data.length)>-1){
                long fileLength = randomFile.length();
                randomFile.seek(fileLength);
                randomFile.write(data);
            }
        }
        randomFile.close();
    }
}
