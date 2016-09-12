package code.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * jdbc封装
 * 
 * @author Administrator
 *
 */
public class BaseDBOperatorUtil {
	private static Connection conn = null;
	private static Properties props = null;

	public static Connection getConn() {
		try {
			props = new Properties();
			InputStream iStream = BaseDBOperatorUtil.class.getResourceAsStream("/resources/dbconf.properties");
			if (null == iStream) {
				System.out.println("配置文件路径错误!");
			}
			props.load(iStream);
			Class.forName(props.getProperty("driverClass"));
			conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"),
					props.getProperty("password"));
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConn() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
