package code.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * 根据数据库表动态生成Bean文件
 * @author ss
 *
 */
public class DynamicGenerateBeanUtil {
	private String authorName = "ss";
	private String[] colnames; // 列名数组
	private String[] colTypes; // 列名类型数组
	private int[] colSizes; // 列名大小数组
	private boolean f_util = false; // 是否需要导入包java.util.*
	private boolean f_sql = false; // 是否小导入包java.sql.*
	private String packageOutPath;//包名
	private String tablename;

	/**
	 * 构造函数
	 * @param packageOutPath 包名
	 * @param tablename 表明
	 */
	public DynamicGenerateBeanUtil(String packageOutPath,String tablename) {
		this.packageOutPath=packageOutPath;
		this.tablename=tablename;
		Connection con = null;
		String sql = "select * from " + tablename;
		Statement pStemt = null;
		try {
			con=BaseDBOperatorUtil.getConn();
			pStemt = (Statement) con.createStatement();
			ResultSet rs = pStemt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int size = rsmd.getColumnCount(); // 统计列
			colnames = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			for (int i = 0; i < size; i++) {
				colnames[i] = rsmd.getColumnName(i + 1);
				colTypes[i] = rsmd.getColumnTypeName(i + 1);

				if (colTypes[i].equalsIgnoreCase("date") || colTypes[i].equalsIgnoreCase("timestamp")) {
					f_util = true;
				}
				if (colTypes[i].equalsIgnoreCase("blob") || colTypes[i].equalsIgnoreCase("char")) {
					f_sql = true;
				}
				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
			}

			String content = parse(colnames, colTypes, colSizes);

			try {
				File directory = new File("");
				// System.out.println("绝对路径"+directory.getAbsolutePath());
				// System.out.println("相对路径"+directory.getCanonicalPath());
				String path = this.getClass().getResource("").getPath();
				System.out.println(".java Path==="+path);
				String outputPath = directory.getAbsolutePath() + "/src/" + packageOutPath.replace(".", "/") + "/"
						+ initcap(tablename) + ".java";
				String bagPath = directory.getAbsolutePath() + "/src/" + packageOutPath.replace(".", "/") + "/";
				File file2 = new File(bagPath);
				if (!file2.isDirectory()){
					try {
						file2.mkdirs();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				FileWriter fw = new FileWriter(outputPath, false);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(content);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生成实体类主代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parse(String[] colnames, String[] colTypes, int[] colSizes) {
		StringBuffer sb = new StringBuffer();
		sb.append("package " + this.packageOutPath + ";\r\n");
		// 判断是否导入工具包
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
		}
		if (f_sql) {
			sb.append("import java.sql.*;\r\n");
		}
		sb.append("\r\n");
		// 注释部分
		sb.append("   /**\r\n");
		sb.append("    * " + initcap(tablename) + " 实体类\r\n");
		sb.append("    * " + new Date() + " " + this.authorName + "\r\n");
		sb.append("    */ \r\n");
		// 实体部分
		sb.append("\r\n\r\npublic class " + initcap(tablename) + "{\r\n");
		processAllAttrs(sb);// 属性
		processAllMethod(sb);// get set方法
		sb.append("}\r\n");
		return sb.toString();
	}

	/**
	 * 生成所有属性
	 * 
	 * @param sb
	 */
	private void processAllAttrs(StringBuffer sb) {

		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + defineVar(colnames[i]) + ";\r\n");
		}

	}

	/**
	 * 生成所有方法
	 * 
	 * @param sb
	 */
	private void processAllMethod(StringBuffer sb) {

		for (int i = 0; i < colnames.length; i++) {
			sb.append("\n\tpublic void set" + transVar(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " "
					+ defineVar(colnames[i]) + "){\r\n");
			sb.append("\t\tthis." + this.defineVar(colnames[i]) + "=" + this.defineVar(colnames[i]) + ";\r\n");
			sb.append("\t}\r\n");
			sb.append("\n\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + transVar(colnames[i]) + "(){\r\n");
			sb.append("\t\treturn " + defineVar(colnames[i]) + ";\r\n");
			sb.append("\t}\r\n");
		}

	}

	/**
	 * 将输入字符串的首字母改为大写EclipseEESpace
	 * 
	 * @param str
	 * @return
	 */
	private String initcap(String str) {

		char[] ch = str.toLowerCase().toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}

		return new String(ch);
	}

	/**
	 * 用于生成get/set方法时 功能：先将变量字母全变为小写，将第一个字母变为大写，将紧跟“_”后面一个字母大写，并去掉“_”.
	 * 
	 * @param str
	 * @return
	 */
	private String transVar(String str) {
		int index = 0;
		if (str.indexOf("_") != -1) {
			index = str.indexOf("_");
			str = str.replace("_", "");
		}
		char[] ch = str.toLowerCase().toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
			if (index != 0) {
				ch[index] = (char) (ch[index] - 32);
			}
		}
		str = new String(ch);
		return str;
	}

	/**
	 * 用于定义变量名 功能：先将变量字母全变为小写，将紧跟“_”后面一个字母大写，并去掉“_”.
	 * 
	 * @param str
	 * @return
	 */
	private String defineVar(String str) {
		int index = 0;
		if (str.indexOf("_") != -1) {
			index = str.indexOf("_");
			str = str.replace("_", "");
		}
		char[] ch = str.toLowerCase().toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z' && index != 0) {
			ch[index] = (char) (ch[index] - 32);
		}
		str = new String(ch);
		return str;
	}

	/**
	 * 获得列的数据类型
	 * 
	 * @param sqlType
	 * @return
	 */
	private String sqlType2JavaType(String sqlType) {
		if (sqlType.equalsIgnoreCase("binary_double")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("binary_float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("blob")) {
			return "byte[]";
		} else if (sqlType.equalsIgnoreCase("blob")) {
			return "byte[]";
		} else if (sqlType.equalsIgnoreCase("char") || sqlType.equalsIgnoreCase("nvarchar2")
				|| sqlType.equalsIgnoreCase("varchar2")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")
				|| sqlType.equalsIgnoreCase("timestamp with local time zone")
				|| sqlType.equalsIgnoreCase("timestamp with time zone")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("number")) {
			return "Long";
		}
		return "String";
	}

}
