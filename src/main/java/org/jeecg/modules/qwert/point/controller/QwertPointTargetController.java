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
import org.jeecg.modules.qwert.point.entity.QwertPointTarget;
import org.jeecg.modules.qwert.point.service.IQwertPointTargetService;

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
 * @Description: qwert_point_target
 * @Author: jeecg-boot
 * @Date:   2021-11-29
 * @Version: V1.0
 */
@Api(tags="qwert_point_target")
@RestController
@RequestMapping("/point/qwertPointTarget")
@Slf4j
public class QwertPointTargetController extends JeecgController<QwertPointTarget, IQwertPointTargetService> {
	@Autowired
	private IQwertPointTargetService qwertPointTargetService;
	
	/**
	 * ??????????????????
	 *
	 * @param qwertPointTarget
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "qwert_point_target-??????????????????")
	@ApiOperation(value="qwert_point_target-??????????????????", notes="qwert_point_target-??????????????????")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(QwertPointTarget qwertPointTarget,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<QwertPointTarget> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointTarget, req.getParameterMap());
		Page<QwertPointTarget> page = new Page<QwertPointTarget>(pageNo, pageSize);
		IPage<QwertPointTarget> pageList = qwertPointTargetService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   ??????
	 *
	 * @param qwertPointTarget
	 * @return
	 */
	@AutoLog(value = "qwert_point_target-??????")
	@ApiOperation(value="qwert_point_target-??????", notes="qwert_point_target-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertPointTarget qwertPointTarget) {
		qwertPointTargetService.save(qwertPointTarget);
		return Result.OK("???????????????");
	}
	
	/**
	 *  ??????
	 *
	 * @param qwertPointTarget
	 * @return
	 */
	@AutoLog(value = "qwert_point_target-??????")
	@ApiOperation(value="qwert_point_target-??????", notes="qwert_point_target-??????")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertPointTarget qwertPointTarget) {
		qwertPointTargetService.updateById(qwertPointTarget);
		return Result.OK("????????????!");
	}
	
	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_target-??????id??????")
	@ApiOperation(value="qwert_point_target-??????id??????", notes="qwert_point_target-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertPointTargetService.removeById(id);
		return Result.OK("????????????!");
	}
	
	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "qwert_point_target-????????????")
	@ApiOperation(value="qwert_point_target-????????????", notes="qwert_point_target-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertPointTargetService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("??????????????????!");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_target-??????id??????")
	@ApiOperation(value="qwert_point_target-??????id??????", notes="qwert_point_target-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertPointTarget qwertPointTarget = qwertPointTargetService.getById(id);
		if(qwertPointTarget==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(qwertPointTarget);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param qwertPointTarget
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertPointTarget qwertPointTarget) {
        return super.exportXls(request, qwertPointTarget, QwertPointTarget.class, "qwert_point_target");
    }

    /**
      * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, QwertPointTarget.class);
    }

}
