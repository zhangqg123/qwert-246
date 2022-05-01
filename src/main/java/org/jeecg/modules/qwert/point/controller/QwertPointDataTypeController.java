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
import org.jeecg.modules.qwert.point.entity.QwertPointDataType;
import org.jeecg.modules.qwert.point.service.IQwertPointDataTypeService;

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
 * @Description: qwert_point_data_type
 * @Author: jeecg-boot
 * @Date:   2021-12-12
 * @Version: V1.0
 */
@Api(tags="qwert_point_data_type")
@RestController
@RequestMapping("/point/qwertPointDataType")
@Slf4j
public class QwertPointDataTypeController extends JeecgController<QwertPointDataType, IQwertPointDataTypeService> {
	@Autowired
	private IQwertPointDataTypeService qwertPointDataTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param qwertPointDataType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "qwert_point_data_type-分页列表查询")
	@ApiOperation(value="qwert_point_data_type-分页列表查询", notes="qwert_point_data_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(QwertPointDataType qwertPointDataType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<QwertPointDataType> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointDataType, req.getParameterMap());
		Page<QwertPointDataType> page = new Page<QwertPointDataType>(pageNo, pageSize);
		IPage<QwertPointDataType> pageList = qwertPointDataTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param qwertPointDataType
	 * @return
	 */
	@AutoLog(value = "qwert_point_data_type-添加")
	@ApiOperation(value="qwert_point_data_type-添加", notes="qwert_point_data_type-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertPointDataType qwertPointDataType) {
		qwertPointDataTypeService.save(qwertPointDataType);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qwertPointDataType
	 * @return
	 */
	@AutoLog(value = "qwert_point_data_type-编辑")
	@ApiOperation(value="qwert_point_data_type-编辑", notes="qwert_point_data_type-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertPointDataType qwertPointDataType) {
		qwertPointDataTypeService.updateById(qwertPointDataType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_data_type-通过id删除")
	@ApiOperation(value="qwert_point_data_type-通过id删除", notes="qwert_point_data_type-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertPointDataTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "qwert_point_data_type-批量删除")
	@ApiOperation(value="qwert_point_data_type-批量删除", notes="qwert_point_data_type-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertPointDataTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_data_type-通过id查询")
	@ApiOperation(value="qwert_point_data_type-通过id查询", notes="qwert_point_data_type-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertPointDataType qwertPointDataType = qwertPointDataTypeService.getById(id);
		if(qwertPointDataType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qwertPointDataType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qwertPointDataType
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertPointDataType qwertPointDataType) {
        return super.exportXls(request, qwertPointDataType, QwertPointDataType.class, "qwert_point_data_type");
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
        return super.importExcel(request, response, QwertPointDataType.class);
    }

}
