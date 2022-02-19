package org.me.todoservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ToolUtil {

	private static final Logger log = LoggerFactory.getLogger(ToolUtil.class);

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		return false;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static void writeToFile(String content, String fileName) throws IOException {
		//在JDK7优化后的try-with-resource语句，该语句确保了每个资源,在语句结束时关闭。
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
			bufferedWriter.write(content);
		}
	}
}
