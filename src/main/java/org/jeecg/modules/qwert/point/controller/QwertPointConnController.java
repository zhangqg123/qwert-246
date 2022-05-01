package org.jeecg.modules.qwert.point.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.QwertPointConn;
import org.jeecg.modules.qwert.point.service.IQwertPointConnService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: qwert_point_conn
 * @Author: jeecg-boot
 * @Date:   2021-12-09
 * @Version: V1.0
 */
@Api(tags="qwert_point_conn")
@RestController
@RequestMapping("/point/qwertPointConn")
@Slf4j
public class QwertPointConnController extends JeecgController<QwertPointConn, IQwertPointConnService> {
	@Autowired
	private IQwertPointConnService qwertPointConnService;
	
	/**
	 * 分页列表查询
	 *
	 * @param qwertPointConn
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "qwert_point_conn-分页列表查询")
	@ApiOperation(value="qwert_point_conn-分页列表查询", notes="qwert_point_conn-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(QwertPointConn qwertPointConn,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<QwertPointConn> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointConn, req.getParameterMap());
		Page<QwertPointConn> page = new Page<QwertPointConn>(pageNo, pageSize);
		IPage<QwertPointConn> pageList = qwertPointConnService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param qwertPointConn
	 * @return
	 */
	@AutoLog(value = "qwert_point_conn-添加")
	@ApiOperation(value="qwert_point_conn-添加", notes="qwert_point_conn-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertPointConn qwertPointConn) {
		qwertPointConnService.save(qwertPointConn);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qwertPointConn
	 * @return
	 */
	@AutoLog(value = "qwert_point_conn-编辑")
	@ApiOperation(value="qwert_point_conn-编辑", notes="qwert_point_conn-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertPointConn qwertPointConn) {
		qwertPointConnService.updateById(qwertPointConn);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_conn-通过id删除")
	@ApiOperation(value="qwert_point_conn-通过id删除", notes="qwert_point_conn-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertPointConnService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "qwert_point_conn-批量删除")
	@ApiOperation(value="qwert_point_conn-批量删除", notes="qwert_point_conn-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertPointConnService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_conn-通过id查询")
	@ApiOperation(value="qwert_point_conn-通过id查询", notes="qwert_point_conn-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertPointConn qwertPointConn = qwertPointConnService.getById(id);
		if(qwertPointConn==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qwertPointConn);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qwertPointConn
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertPointConn qwertPointConn) {
        return super.exportXls(request, qwertPointConn, QwertPointConn.class, "qwert_point_conn");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, QwertPointConn.class);
    }

}
