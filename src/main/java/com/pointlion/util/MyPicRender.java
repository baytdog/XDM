/**
 * 
 */
package com.pointlion.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

import com.jfinal.render.Render;

/** 
 * @ClassName: MyPicRender 
 * @Description: TODO
 * @author: bkkco
 * @date: 2020年8月26日 下午12:42:57  
 */
public class MyPicRender extends Render {

	/* (non-Javadoc)
	 * @see com.jfinal.render.Render#render()
	 */
	
	private final File  file;
	
	public MyPicRender(File file) {
		
		this.file=file;
		
	}
	
	
	
	
	@Override
	public void render() {
		

      //  BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		
		 
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        
        byte[] file2byte = File2byte(this.file);
        ServletOutputStream sos = null;
        try {
            sos = response.getOutputStream();
            
            sos.write(file2byte);
            
           // ImageIO.write(file, "jpeg",sos);
            
          // ImageIO.write(im, formatName, output)
           // response.getWriter().write(buf);
           
           
       
            
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if (sos != null)
                try {sos.close();} catch (IOException e) {e.printStackTrace();}
        }
    
	 }

	
	
	
	public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }
}
