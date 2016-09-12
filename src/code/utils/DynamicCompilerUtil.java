package code.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang3.StringUtils;

/**
 * 动态编译java文件
 * @author ss
 *
 */
public class DynamicCompilerUtil {
	/**
	 * 编译java文件
	 * 
	 * @param filePath
	 *            文件或目录  若为目录 递归编译
	 * @param sourceDir
	 *            java源文件存放位置
	 * @param targetDir 编译时候自动会创建包相关路径
	 *            编译后class文件存放目录
	 * @param diagnostics
	 *            存放编译过程中的错误信息
	 * @return
	 * @throws Exception
	 */
	public static boolean compiler(String filePath, String sourceDir, String targetDir,
			DiagnosticCollector<JavaFileObject> diagnostics) throws Exception {
		// 获取编译器实例
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler==null){
			System.out.println("false");
			return false;
		}
		// 获取标准文件管理器实例
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		try {
			if (StringUtils.isEmpty(filePath) && StringUtils.isEmpty(sourceDir) && StringUtils.isEmpty(targetDir)) {
				return false;
			}
			// 得到filepath目录下所有文件
			File sourceFile = new File(filePath);
			List<File> sourceFileList = new ArrayList<File>();
			getSourceFiles(sourceFile, sourceFileList);
			if (sourceFileList.size() == 0) {
				System.out.println(filePath + "目录下查找不到任何java文件");
				return false;
			}
			// 获取要编译的编译单
			Iterable<? extends JavaFileObject> compilationUnits = fileManager
					.getJavaFileObjectsFromFiles(sourceFileList);
			/**
			 * 编译选项，在编译java文件时，编译程序会自动的去寻找java文件引用的其他的java源文件或者class。
			 * -sourcepath选项就是定义java源文件的查找目录， -classpath选项就是定义class文件的查找目录。
			 */
			System.out.println(">>>>"+targetDir);
			Iterable<String> options = Arrays.asList("-d", targetDir, "-sourcepath", sourceDir);
			CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, options, null,
					compilationUnits);
			// 运行编译任务
			return compilationTask.call();
		} finally {
			fileManager.close();
		}
	}

	/**
	 * 查找该目录下所有java文件
	 * 
	 * @param sourceFile
	 * @param sourceFileList
	 * @throws Exception
	 */
	private static void getSourceFiles(File sourceFile, List<File> sourceFileList) throws Exception {
		if (sourceFile.exists() && sourceFileList != null) {
			if (sourceFile.isDirectory()) {
				// 得到该目录下以.java结尾的文件或目录
				File[] childrenFiles = sourceFile.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							return true;
						} else {
							String name = pathname.getName();
							return name.endsWith(".java") ? true : false;
						}
					}
				});
				// 递归调用
				for (File childFile : childrenFiles) {
					getSourceFiles(childFile, sourceFileList);
				}
			} else {// 若file对象为文件
				sourceFileList.add(sourceFile);
			}
		}
	}
	
	/**
	 * 编译制定pack下的java文件
	 * @param packFilePath
	 * @param packsourceDir
	 * @param packtargetDir
	 */
	public static void compiler(String packFilePath,String packsourceDir,String packtargetDir){
		try {
			File directory = new File("");
			String filePath = directory.getAbsolutePath() + "/src/" + packFilePath.replace(".", "/");
			String sourceDir = directory.getAbsolutePath() + "/src/" + packsourceDir.replace(".", "/");
			String targetDir = directory.getAbsolutePath() + "/bin/" + packtargetDir.replace(".", "/");
			File targetDirFile = new File(targetDir);
			if(!targetDirFile.isDirectory()){
				targetDirFile.mkdir();
			}
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			boolean compilerResult = compiler(filePath, sourceDir, targetDir, diagnostics);
			if (compilerResult) {
				System.out.println("编译成功");
			} else {
				System.out.println("编译失败");
				for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
					System.out.println(diagnostic.getMessage(null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
