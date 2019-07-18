package com.example.app.filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.app.bean.UserAccount;
import com.example.app.utlis.DBUtlis;
import com.example.app.utlis.MyUtlis;

@WebFilter(filterName = "/CookieFilter", urlPatterns = "/*")
public class CookieFilter implements Filter {

	public CookieFilter() {

	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		UserAccount userInSession = MyUtlis.getLoginedUser(session);
		if (userInSession != null) {
			session.setAttribute("COOKIE_CHECKED", "CHECKED");
			chain.doFilter(request, response);
			return;
		}

		Connection conn = MyUtlis.getStoredConnection(request);

		String checked = (String) session.getAttribute("COOKIE_CHECKED");
		if (checked == null && conn != null) {
			String userName = MyUtlis.getUserNameInCookie(req);
			try {

				//
				UserAccount user = DBUtlis.findUser(conn, userName);
				MyUtlis.storeLoginedUser(session, user);

			} catch (SQLException e) {
				e.printStackTrace();
			}
			session.setAttribute("COOKIE_CHECKED", "CHECKED");
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}

}
