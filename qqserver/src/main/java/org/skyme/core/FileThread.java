package org.skyme.core;

import java.io.*;
import java.net.Socket;

public class FileThread implements Runnable{

    private Socket socket;

    public FileThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
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
                //用户需要下载的文件名
                String s = dataInputStream.readUTF();
                //找到对应的文件
                File file = new File("D://savaData",s);
                if(!file.exists()){
                    //如果不存在则给默认的图片
                    file = new File("D://savaData","default.png");
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
//                String s = dataInputStream.readUTF();
                //找到对应的文件
                File file = new File("D://savaData",fileName);
                if(!file.exists()){
                    file = new File("D://savaData",fileName);
                }
                //读取文件的长度
                long fileSize = dis.readLong();
                // 创建本地文件，并写入接收到的数据
                FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
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
