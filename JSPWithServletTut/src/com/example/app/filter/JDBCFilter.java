package com.example.app.filter;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.example.app.conn.ConnectionUtils;
import com.example.app.utlis.MyUtlis;

@WebFilter(filterName = "/JDBCFilter", urlPatterns = "/*")
public class JDBCFilter implements Filter {

	public JDBCFilter() {

	}

	public void destroy() {
	}

	private boolean needJDBC(HttpServletRequest request) {
		System.out.println("JDBC Filter");

		String servletPath = request.getServletPath();
		String pathInfo = request.getPathInfo();
		String urlPattern = servletPath;
		if (pathInfo != null) {
			urlPattern = servletPath + "/*";

		}

		Map<String, ? extends ServletRegistration> servletRegistrations = request.getServletContext()
				.getServletRegistrations();

		Collection<? extends ServletRegistration> values = servletRegistrations.values();
		for (ServletRegistration sr : values) {
			Collection<String> mappings = sr.getMappings();

			if (mappings.contains(urlPattern))
				return true;
		}

		return false;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		if (this.needJDBC(req)) {
			System.out.println("Open Connection for :" + req.getServletPath());
			Connection conn = null;

			try {

				conn = ConnectionUtils.getConnection();
				conn.setAutoCommit(false);
				MyUtlis.storeConnection(request, conn);

				chain.doFilter(request, response);

				conn.commit();

			} catch (Exception e) {
				e.printStackTrace();
				ConnectionUtils.rollBackQuietly(conn);
				throw new ServletException();

			} finally {
				ConnectionUtils.closeQuietly(conn);
			}

		}
		else {
			chain.doFilter(request, response);
		}

	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
