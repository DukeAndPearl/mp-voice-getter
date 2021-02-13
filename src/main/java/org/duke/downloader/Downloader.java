package org.duke.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class Downloader {

	/**
	 * FileUtils下载网络文件
	 *
	 * @param serverUrl       ：网络文件地址
	 * @param savePath：本地保存路径
	 * @param filePath        ：压缩文件保存路径
	 * @return
	 */
	public static Boolean download(String serverUrl, String dir, String filePath) throws Exception {
		System.out.println("正在下载：" + serverUrl);
		boolean result = false;
		File f = new File(dir);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new Exception("makdirs: '" + dir + "'fail");
			}
		}
		URL url = new URL(serverUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3 * 1000);
		// 防止屏蔽程序抓取而放回403错误
		conn.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 5.0;Windows NT;DigExt)");
		Long totalSize = Long.parseLong(conn.getHeaderField("Content-Length"));
		if (totalSize > 0) {
			try {
				FileUtils.copyURLToFile(url, new File(filePath));
			} catch (IOException e) {
				System.out.println("下载失败.");
				return false;
			}
			result = true;
		}
		System.out.println("下载成功：" + filePath);
		return result;
	}

	/**
	 * 字节流下载压缩文件
	 * 
	 * @param serverUrl :网络地址
	 * @param dir       ：保持路径
	 * @param filePath  ：压缩文件保持路径
	 * @return ：下载结果
	 * @throws Exception ：异常
	 */
	public static Boolean downloadBySaedine(String serverUrl, String dir, String filePath) throws Exception {
		boolean result = false;
		File f = new File(dir);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new Exception("makdirs: '" + dir + "'fail");
			}
		}
		// Sardine是WebDAV的工具包
		Sardine sardine = SardineFactory.begin("", "");
		if (sardine.exists(serverUrl)) {
			URL url = new URL(serverUrl);
			URLConnection conn = url.openConnection();
			int length = conn.getContentLength();
			conn.setConnectTimeout(60 * 60 * 24 * 1000);
			// 防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			InputStream is = sardine.get(serverUrl);
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = new FileOutputStream(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int len;
			byte[] bytes = new byte[length / 5];
			while ((len = bis.read(bytes)) != -1) {
				bos.write(bytes, 0, len);
			}
			// 清除缓存
			bos.flush();
			// 关闭流
			fos.close();
			is.close();
			bis.close();
			bos.close();
			result = true;
		}
		return result;
	}

}
