package org.skyme.core;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class FileThread implements Runnable{

    private Socket socket;
    private Properties properties ;

    public FileThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream in = Server.class.getClassLoader().getResourceAsStream("server.properties");
//
        try{
            properties=new Properties();
            properties.load(in);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filePath = properties.getProperty("filePath");
        InputStream inputStream = null;
        DataInputStream dataInputStream = null;
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        InputStream is = null;
        try {
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            String type = dataInputStream.readUTF();
            //将文件写给用户
            if("download".equals(type)){
                String s = dataInputStream.readUTF();
                File f=new File(filePath);
                if(!f.exists()){
                    boolean mkdirs = f.mkdirs();//创建目录
                }
                File file = new File(filePath,s);
                System.out.println("文件下载地址"+file);
                if(!file.exists()){
                    System.out.println("文件目录不存在");
                    //如果不存在则给个图片
                    file = new File(filePath,"default.png");
                }
                outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF(file.getName());//文件名
                dataOutputStream.flush();
                dataOutputStream.writeLong(file.length());//文件大小
                dataOutputStream.flush();
                is= new FileInputStream(file);
                byte [] bytes = new byte[1024];
                int len = 0;
                while((len = is.read(bytes)) != -1){
                    dataOutputStream.write(bytes,0,len);
                    dataOutputStream.flush();
                }
            }else if("upload".equals(type)){

                //上传文件

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String fileName = dis.readUTF();
                //找到对应的文件
                File file = new File(filePath,fileName);
                if(!file.exists()){
                    file = new File("filePath",fileName);
                }
                //读取文件的长度
                long fileSize = dis.readLong();
                // 创建本地文件，并写入接收到的数据
                FileOutputStream fos = new FileOutputStream(file);

                System.out.println("相对路径:"+ file.getPath());
                System.out.println("绝对路径:"+ file.getAbsolutePath());

                BufferedOutputStream bos = new BufferedOutputStream(fos);
                byte[] buffer = new byte[1024];
                int len;
                long receivedBytes = 0;
                while ((len = dis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                    receivedBytes += len;
                    if (receivedBytes >= fileSize) {
                        break;
                    }
                }
                System.out.println("写入完成");
                bos.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
