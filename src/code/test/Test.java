package code.test;

import java.lang.reflect.Method;

import code.utils.DynamicCompilerUtil;
import code.utils.DynamicGenerateBeanUtil;

public class Test {
	public static void main(String[] args) {
		String packageOutPath="code.bean.test";
		String tablename="Student";
		new DynamicGenerateBeanUtil(packageOutPath,tablename);
		DynamicCompilerUtil.compiler(packageOutPath, packageOutPath, "");
		try {
			Class<?> forName = Class.forName(packageOutPath+".Student");
			Object tu = forName.newInstance();
			Method sUser = forName.getMethod("setName", String.class);
			Method gUser = forName.getMethod("getName");
			sUser.invoke(tu, "susan");
			String user = (String) gUser.invoke(tu);
			System.out.println(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String aString = "你好";
		System.err.println(String.valueOf(aString.length()));
	}
}
