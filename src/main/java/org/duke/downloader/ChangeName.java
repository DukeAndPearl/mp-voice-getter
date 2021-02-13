package org.duke.downloader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ChangeName {

	static void add(File file, String str, boolean isPre) throws IOException {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File son : files) {
				add(son, str, isPre);
			}
		} else {
			String path;
			int index = file.getName().lastIndexOf(".");
			if (index == -1) {
				return;
			}
			String ext = file.getName().substring(index);
			if (isPre) {
				path = file.getParent() + "\\" + str + file.getName();
			} else {
				path = file.getParent() + "\\" + file.getName().substring(0, index) + str + ext;
			}
			File newFile = new File(path);
			FileUtils.copyFile(file, newFile, true);
			System.out.println("复制:" + file.getAbsolutePath() + "到" + newFile.getAbsolutePath());
			file.deleteOnExit();
			System.out.println("已删除：" + file.getAbsolutePath());
		}
	}

}
