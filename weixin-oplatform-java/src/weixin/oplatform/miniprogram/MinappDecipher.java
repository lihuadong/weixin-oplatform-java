/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * MinappDecipher.java
 * Ver0.0.1
 * 2017年5月8日-上午8:53:59
 *  2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform.miniprogram;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 
 * MinappDecipher
 * 
 * 李华栋
 * 李华栋
 * 2017年5月8日 上午8:53:59
 * 
 * @version 0.0.1
 * 
 */
public class MinappDecipher {

    public static boolean initialized = false;  
	
	/**
	 * AES解密
	 * @param content 密文
	 * @return
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 */
	public static byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws InvalidAlgorithmParameterException {
		initialize();
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			Key sKeySpec = new SecretKeySpec(keyByte, "AES");
			
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化 
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();  
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();  
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}  
	
	public static void initialize(){  
        if (initialized) return;  
        Security.addProvider(new BouncyCastleProvider());  
        initialized = true;  
    }
	
	//生成iv  
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception{  
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");  
        params.init(new IvParameterSpec(iv));  
        return params;  
    }  

    public static String getUserInfo(String encryptedData,String iv,String sessionKey){
    	
    	    String userInfo = null; 
		try {
			
	        byte[] resultByte = decrypt(Base64.getDecoder().decode(encryptedData), Base64.getDecoder().decode(sessionKey), Base64.getDecoder().decode(iv));
	        if(null != resultByte && resultByte.length > 0){
	            userInfo = new String(resultByte, "UTF-8");
	        }
	    } catch (InvalidAlgorithmParameterException e) {
	        e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
		return userInfo;
    }
	
	/**
	 * main(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param args 
	 *void
	 * @exception 
	 * @since  0.0.1
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//{"openId":"oDWH50ET2p9WQvV7XWu6qJk1D3Lc","nickName":"悟空来 |  Arthur李华栋","gender":1,"language":"zh_CN","city":"Haidian","province":"Beijing","country":"CN","avatarUrl":"http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83er0kFrHHQHHHF6V4OGkACFnBibYVqT2wmjvUZO4gXHYWmsfe8abKZUby7iaurmptS4zU7arYn5B5iajw/0","unionId":"oEoEjwSkoftwsyyicEAJAU4vKMiE","watermark":{"timestamp":1494078442,"appid":"wx86dfbc2c131a5b8f"}}

		String encryptedData ="9hhqw5ESzt6yYEiMw50OUzYFYguJNb7aX2DxomNfm2aBNXDLIchLVShqj3kIbrj/0owXkRRRamFW4keO6fbr4bVAIrgnAYPUquGx0mNxFLbQtX0MUp/qnUmVMPqYYPo8BkQB1Q1XRDkRjvq+hZou8A==";
		String iv ="SWKScOnePGvuKkFkyuD/4A==";
		String sessionKey="Vko98f1Ojc9P9pXcI3k2JA==";
		
		String str   = getUserInfo(encryptedData,iv,sessionKey);
		System.out.println(str);
	}

}
