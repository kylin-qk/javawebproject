package com.imooc.mgallery.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imooc.mgallery.service.PaintingService;
import com.imooc.mgallery.utils.PageModel;

/**
 * Servlet implementation class PaintingController
 */
@WebServlet("/page")
public class PaintingController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private PaintingService paintingService = new PaintingService();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaintingController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//1.接收Http数据
		String page = request.getParameter("p");//页号
		String rows = request.getParameter("r");//每页记录数
		String category = request.getParameter("c");
		if(page == null) { //page设置默认值
			page = "1";
		}
		if(rows == null) { //rows设置默认值
			rows = "6";
		}
		//2.调用Service方法,得到处理结果
		PageModel pageModel = paintingService.pagination(Integer.parseInt(page), Integer.parseInt(rows),category);
		request.setAttribute("pageModel", pageModel);
		//3.请求转发至对应JSP(view)进行数据展现
		request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
	}

}
