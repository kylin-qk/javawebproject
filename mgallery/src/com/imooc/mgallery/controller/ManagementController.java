package com.imooc.mgallery.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.imooc.mgallery.entity.Painting;
import com.imooc.mgallery.service.PaintingService;
import com.imooc.mgallery.utils.PageModel;

/**
 * 后台管理功能Controller
 */
@WebServlet("/management")
public class ManagementController extends HttpServlet {
	private PaintingService paintingService = new PaintingService();
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManagementController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		//通过method参数区分不同的操作
		String method = request.getParameter("method");
		if(method.equals("list")) {//分页查询列表
			this.list(request,response);
		}else if(method.equals("delete")) {
			this.delete(request,response);
		}else if(method.equals("show_create")) {//显示新增页面
			this.showCreatePage(request,response);
		}else if(method.equals("create")) {
			this.create(request, response);
		}else if(method.equals("show_update")) {
			this.showUpdatePage(request,response);
		}else if(method.equals("update")) {
			this.update(request,response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	//分页查询列表
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String p = request.getParameter("p");
		String r = request.getParameter("r");
		if(p==null) {
			p = "1";
		}
		if(r==null) {
			r = "6";
		}
		PageModel pageModel = paintingService.pagination(Integer.parseInt(p),Integer.parseInt(r));
		request.setAttribute("pageModel", pageModel);
		request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
	}
	//显示新增页面
	private void showCreatePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/jsp/create.jsp").forward(request, response);		
	}
	//新增油画方法
	private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//文件上传时的数据处理与标准表单完全不同
		/*
		String pname = request.getParameter("pname");
		System.out.println(pname);
		*/
		//1. 初始化FileUpload组件
		FileItemFactory factory = new DiskFileItemFactory();
		/**
		 * FileItemFactory 用于将前端表单的数据转换为一个个FileItem对象
		 * ServletFileUpload 则是为FileUpload组件提供Java web的Http请求解析
		 */
		ServletFileUpload sf = new ServletFileUpload(factory);
		//2. 遍历所有FileItem
		try {
			List<FileItem> formData = sf.parseRequest(request);
			Painting painting = new Painting();
			for(FileItem fi:formData) {
				if(fi.isFormField()) {
					System.out.println("普通输入项:" + fi.getFieldName() + ":" + fi.getString("UTF-8"));
					switch (fi.getFieldName()) {
					case "pname":
						painting.setPname(fi.getString("UTF-8"));
						break;
					case "category":
						painting.setCategory(Integer.parseInt(fi.getString("UTF-8")));
						break;
					case "price":
						painting.setPrice(Integer.parseInt(fi.getString("UTF-8")));
						break;
					case "description":
						painting.setDescription(fi.getString("UTF-8"));
						break;
					default:
						break;
					}
				}else {
					System.out.println("文件上传项:" + fi.getFieldName());
					//3.文件保存到服务器目录
					String path = request.getServletContext().getRealPath("/upload");
					System.out.println("上传文件目录:" + path);
					//String fileName = "test.jpg";
					String fileName = UUID.randomUUID().toString();
					//fi.getName()得到原始文件名,截取最后一个.后所有字符串,例如:wxml.jpg->.jpg
					String suffix = fi.getName().substring(fi.getName().lastIndexOf("."));
					//fi.write()写入目标文件
					fi.write(new File(path,fileName + suffix));
					painting.setPreview("/upload/" + fileName + suffix);
				}
			}
			paintingService.create(painting);//新增功能
			response.sendRedirect("/management?method=list");//返回列表页
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示更新页面
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void showUpdatePage(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		Painting painting = paintingService.findById(Integer.parseInt(id));
		request.setAttribute("painting", painting);
		request.getRequestDispatcher("/WEB-INF/jsp/update.jsp").forward(request, response);
	}
	/**
	 * 实现油画更新
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void update(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		int isPreviewModified = 0;
		//文件上传时的数据处理与标准表单完全不同
		/*
		String pname = request.getParameter("pname");
		System.out.println(pname);
		*/
		//1. 初始化FileUpload组件
		FileItemFactory factory = new DiskFileItemFactory();
		/**
		 * FileItemFactory 用于将前端表单的数据转换为一个个FileItem对象
		 * ServletFileUpload 则是为FileUpload组件提供Java web的Http请求解析
		 */
		ServletFileUpload sf = new ServletFileUpload(factory);
		//2. 遍历所有FileItem
		try {
			List<FileItem> formData = sf.parseRequest(request);
			Painting painting = new Painting();
			for(FileItem fi:formData) {
				if(fi.isFormField()) {
					System.out.println("普通输入项:" + fi.getFieldName() + ":" + fi.getString("UTF-8"));
					switch (fi.getFieldName()) {
					case "pname":
						painting.setPname(fi.getString("UTF-8"));
						break;
					case "category":
						painting.setCategory(Integer.parseInt(fi.getString("UTF-8")));
						break;
					case "price":
						painting.setPrice(Integer.parseInt(fi.getString("UTF-8")));
						break;
					case "description":
						painting.setDescription(fi.getString("UTF-8"));
						break;
					case "id":
						painting.setId(Integer.parseInt(fi.getString("UTF-8")));
						break;
					case "isPreviewModified":
						isPreviewModified = Integer.parseInt(fi.getString("UTF-8"));
						break;
					default:
						break;
					}
				}else {
					if(isPreviewModified == 1) { 
						System.out.println("文件上传项:" + fi.getFieldName());
						//3.文件保存到服务器目录
						String path = request.getServletContext().getRealPath("/upload");
						System.out.println("上传文件目录:" + path);
						//String fileName = "test.jpg";
						String fileName = UUID.randomUUID().toString();
						//fi.getName()得到原始文件名,截取最后一个.后所有字符串,例如:wxml.jpg->.jpg
						String suffix = fi.getName().substring(fi.getName().lastIndexOf("."));
						//fi.write()写入目标文件
						fi.write(new File(path,fileName + suffix));
						painting.setPreview("/upload/" + fileName + suffix);
					}
				}
			}
			//更新数据的核心方法
			paintingService.update(painting, isPreviewModified);
			response.sendRedirect("/management?method=list");//返回列表页
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 客户端采用Ajax方式提交Http请求
	 * Controller方法处理后不再跳转任何jsp,而是通过响应输出JSON格式字符串
	 * Tips:作为Ajax与服务器交互后,得到的不是整页HTML,而是服务器处理后的数据
	 * @throws IOException 
	 */
	
	public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");
		PrintWriter out = response.getWriter();
		try {
			paintingService.delete(Integer.parseInt(id));
			//{"result":"ok"}
			out.println("{\"result\":\"ok\"}");
		}catch(Exception e) {
			e.printStackTrace();
			out.println("{\"result\":\"" + e.getMessage() + "\"}");
		}
		
	}
	
}
