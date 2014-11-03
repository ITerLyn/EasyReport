package org.easyframework.report.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.easyframework.report.engine.exception.NotFoundLayoutColumnException;
import org.easyframework.report.engine.exception.SQLQueryException;
import org.easyframework.report.engine.exception.TemplatePraseException;
import org.easyframework.report.exception.QueryParamsException;
import org.easyframework.report.view.BootstrapQueryFormView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

/**
 * 报表生成控制器
 */
@Controller
@RequestMapping(value = "/report")
public class ReportingController extends AbstractController {
	@RequestMapping(value = "/home")
	public String home() {
		return "/home";
	}

	@RequestMapping(value = { "", "/", "/index" })
	public String index(HttpServletRequest request) {
		return "/designer";
	}

	@RequestMapping(value = { "/preview/{uid}" })
	public ModelAndView preview(@PathVariable String uid, String viewName, HttpServletRequest request) {
		if (StringUtils.isBlank(viewName)) {
			return this.preview(uid, request);
		}

		ModelAndView modelAndView = new ModelAndView(viewName);
		try {
			ReportingContollerUtils.previewByFormMap(uid, modelAndView, request);
		} catch (QueryParamsException | TemplatePraseException ex) {
			modelAndView.addObject("formHtmlText", ex.getMessage());
			this.logException("查询参数生成失败", ex);
		} catch (Exception ex) {
			modelAndView.addObject("formHtmlText", "查询参数生成失败！请联系管理员.");
			this.logException("查询参数生成失败", ex);
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/uid/{uid}" })
	public ModelAndView preview(@PathVariable String uid, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("/template");
		try {
			ReportingContollerUtils.previewByTemplate(uid, modelAndView, new BootstrapQueryFormView(), request);
		} catch (QueryParamsException | TemplatePraseException ex) {
			modelAndView.addObject("formHtmlText", ex.getMessage());
			this.logException("查询参数生成失败", ex);
		} catch (Exception ex) {
			modelAndView.addObject("formHtmlText", "查询参数生成失败！请联系管理员.");
			this.logException("查询参数生成失败", ex);
		}

		return modelAndView;
	}

	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject generate(String uid, HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		try {
			ReportingContollerUtils.generate(uid, jsonObject, request);
		} catch (QueryParamsException | NotFoundLayoutColumnException | SQLQueryException | TemplatePraseException ex) {
			jsonObject.put("htmlTable", ex.getMessage());
			this.logException("报表生成失败", ex);
		} catch (Exception ex) {
			jsonObject.put("htmlTable", "报表生成失败！请联系管理员.");
			this.logException("报表生成失败", ex);
		}

		return jsonObject;
	}

	@RequestMapping(value = "/exportexcel", method = RequestMethod.POST)
	public void exportToExcel(String uid, String name, String htmlText, HttpServletRequest request, HttpServletResponse response) {
		try {
			ReportingContollerUtils.exportToExcel(uid, name, htmlText, request, response);
		} catch (Exception ex) {
			this.logException("导出Excel失败", ex);
		}
	}
}