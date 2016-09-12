package code.test;

import nl.flotsam.xeger.Xeger;

/**
 * 根据正则表达式生成随机数
 * @author Administrator
 *
 */
public class RandomNewTest {
	public static void main(String[] args) {
		String regex = "[J][0-9]{11}"; 
		Xeger generator = new Xeger(regex); 
		for(int i=0;i<100;i++){
			String result = generator.generate(); 
			System.out.println(result);
		}
	}
}
