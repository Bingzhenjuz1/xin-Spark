package com.xin.spark.service.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * MD5加密
 * 单向加密算法
 * 特点：加密速度快，不需要秘钥，但是安全性不高，需要搭配随机盐值使用
 */
public class MD5Util {

	/**
	 * 加密
	 * @param content
	 * @param salt
	 * @param charset
	 * @return
	 */
	public static String sign(String content, String salt, String charset) {
		content = content + salt;
		return DigestUtils.md5Hex(getContentBytes(content, charset));
	}

	/**
	 * 证明
	 * @param content
	 * @param sign
	 * @param salt
	 * @param charset
	 * @return
	 */
	public static boolean verify(String content, String sign, String salt, String charset) {
		content = content + salt;
		String mysign = DigestUtils.md5Hex(getContentBytes(content, charset));
		return mysign.equals(sign);
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (!"".equals(charset)) {
			try {
				return content.getBytes(charset);
			} catch (UnsupportedEncodingException var3) {
				throw new RuntimeException("MD5签名过程中出现错误,指定的编码集错误");
			}
		} else {
			return content.getBytes();
		}
	}
}