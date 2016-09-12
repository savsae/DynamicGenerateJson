package code.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.google.gson.Gson;

public class SaveFileUtil {
	/**
	 * @param
	 * @return 返回文件
	 */
	public static File saveToFile(List<?> exportDatas, String outPutPath, String fileName,String suffix) {
		if(!suffix.startsWith("."))  return null;
		File tmpFile = null;
		BufferedWriter tmpFileOutputStream = null;
		try {
			File file = new File(outPutPath);
			if (!file.exists()) {
				file.mkdir();
			}
			// 定义文件格式并创建
			tmpFile = File.createTempFile(fileName, suffix, new File(outPutPath));
			System.out.println("tmpFile：" + tmpFile);
			// UTF-8使正确读取分隔符","
			tmpFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8"),
					1024);
			// 写入文件内容
			for (Object billInfor : exportDatas) {
				tmpFileOutputStream.write(new Gson().toJson(billInfor));
				tmpFileOutputStream.newLine();
			}
			tmpFileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				tmpFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tmpFile;
	}
}
