package com.g.filter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.g.model.support.XDAOSupport;

@Aspect
public class LogService extends XDAOSupport {
	private static Log log = LogFactory.getLog(LogService.class);
	@Around("execution(* com.g.controller.*.*(..))")
	public Object validateLogin(ProceedingJoinPoint point) throws Throwable {
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		//设置不过滤的链接
		String[] notFilter = new String[]{"login","test"};
		String uri = request.getRequestURL().toString();
		boolean doFilter = true;
		for (String s : notFilter) {
			if (uri.indexOf(s) != -1) {
				// 如果uri中包含不过滤的uri，则不进行过滤
				doFilter = false;
				break;
			}
		}
		if (doFilter) {
			Object temp = request.getSession().getAttribute("loginUser");
			if (temp == null) {
				return "login";
			}
		}
		return point.proceed();
	}

	
	@AfterThrowing(pointcut = "execution(* com.g.controller.*.*(..))", throwing = "ex")
	public void afterThrowing(Exception ex) {
		try {
			log.error(ex.getMessage(), ex);
		} catch (Exception ee) {
			System.out.println("记录错误：" + ee.getMessage());
		}
	}
	
	/**
	 * 截图字符串前面字节的方法
	 *
	 * @param b
	 * @param charsetName
	 * @return
	 */
	public static String decode(byte[] b, String charsetName) {
		ByteBuffer in = ByteBuffer.wrap(b);
		Charset charset = Charset.forName(charsetName);
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer out = CharBuffer.allocate(b.length);
		out.clear();
		decoder.decode(in, out, false);
		out.flip();
		return out.toString();
		
	}
}
