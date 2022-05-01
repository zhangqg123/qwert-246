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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.QwertPointProtocolItem;
import org.jeecg.modules.qwert.point.entity.QwertPointTargetTypeItem;
import org.jeecg.modules.qwert.point.service.IQwertPointTargetTypeItemService;

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
 * @Description: 点类型项目表
 * @Author: jeecg-boot
 * @Date:   2021-11-29
 * @Version: V1.0
 */
@Api(tags="点类型项目表")
@RestController
@RequestMapping("/point/qwertPointTargetTypeItem")
@Slf4j
public class QwertPointTargetTypeItemController extends JeecgController<QwertPointTargetTypeItem, IQwertPointTargetTypeItemService> {
	@Autowired
	private IQwertPointTargetTypeItemService qwertPointTargetTypeItemService;
	
	/**
	 * 分页列表查询
	 *
	 * @param qwertPointTargetTypeItem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "点类型项目表-分页列表查询")
	@ApiOperation(value="点类型项目表-分页列表查询", notes="点类型项目表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(QwertPointTargetTypeItem qwertPointTargetTypeItem,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<QwertPointTargetTypeItem> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointTargetTypeItem, req.getParameterMap());
		Page<QwertPointTargetTypeItem> page = new Page<QwertPointTargetTypeItem>(pageNo, pageSize);
		IPage<QwertPointTargetTypeItem> pageList = qwertPointTargetTypeItemService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param qwertPointTargetTypeItem
	 * @return
	 */
	@AutoLog(value = "点类型项目表-添加")
	@ApiOperation(value="点类型项目表-添加", notes="点类型项目表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertPointTargetTypeItem qwertPointTargetTypeItem) {
		qwertPointTargetTypeItemService.save(qwertPointTargetTypeItem);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qwertPointTargetTypeItem
	 * @return
	 */
	@AutoLog(value = "点类型项目表-编辑")
	@ApiOperation(value="点类型项目表-编辑", notes="点类型项目表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertPointTargetTypeItem qwertPointTargetTypeItem) {
		qwertPointTargetTypeItemService.updateById(qwertPointTargetTypeItem);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "点类型项目表-通过id删除")
	@ApiOperation(value="点类型项目表-通过id删除", notes="点类型项目表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertPointTargetTypeItemService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "点类型项目表-批量删除")
	@ApiOperation(value="点类型项目表-批量删除", notes="点类型项目表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertPointTargetTypeItemService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "点类型项目表-通过id查询")
	@ApiOperation(value="点类型项目表-通过id查询", notes="点类型项目表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertPointTargetTypeItem qwertPointTargetTypeItem = qwertPointTargetTypeItemService.getById(id);
		if(qwertPointTargetTypeItem==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qwertPointTargetTypeItem);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qwertPointTargetTypeItem
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertPointTargetTypeItem qwertPointTargetTypeItem) {
        return super.exportXls(request, qwertPointTargetTypeItem, QwertPointTargetTypeItem.class, "点类型项目表");
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
        return super.importExcel(request, response, QwertPointTargetTypeItem.class);
    }

	 @RequestMapping(value = "/queryTargetTypeList", method = RequestMethod.GET)
	 public Result<List<QwertPointTargetTypeItem>> queryTargetTypeList() {
		 Result<List<QwertPointTargetTypeItem>> result = new Result<>();
		 LambdaQueryWrapper<QwertPointTargetTypeItem> query = new LambdaQueryWrapper<QwertPointTargetTypeItem>();
		 try {
			 List<QwertPointTargetTypeItem> list = qwertPointTargetTypeItemService.list(query);
			 result.setResult(list);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 log.error(e.getMessage(),e);
		 }
		 return result;
	 }

}
