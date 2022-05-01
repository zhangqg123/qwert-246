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
import org.jeecg.modules.qwert.point.entity.ProtocolTreeModel;
import org.jeecg.modules.qwert.point.entity.QwertPointProtocol;
import org.jeecg.modules.qwert.point.service.IQwertPointProtocolService;

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
 * @Description: qwert_point_protocol
 * @Author: jeecg-boot
 * @Date:   2021-11-18
 * @Version: V1.0
 */
@Api(tags="qwert_point_protocol")
@RestController
@RequestMapping("/point/qwertPointProtocol")
@Slf4j
public class QwertPointProtocolController extends JeecgController<QwertPointProtocol, IQwertPointProtocolService>{
	@Autowired
	private IQwertPointProtocolService qwertPointProtocolService;
	
	/**
	 * 分页列表查询
	 *
	 * @param qwertPointProtocol
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "qwert_point_protocol-分页列表查询")
	@ApiOperation(value="qwert_point_protocol-分页列表查询", notes="qwert_point_protocol-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<?> queryPageList(QwertPointProtocol qwertPointProtocol,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
        if(hasQuery != null && "true".equals(hasQuery)){
            QueryWrapper<QwertPointProtocol> queryWrapper =  QueryGenerator.initQueryWrapper(qwertPointProtocol, req.getParameterMap());
            List<QwertPointProtocol> list = qwertPointProtocolService.queryTreeListNoPage(queryWrapper);
            IPage<QwertPointProtocol> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        }else{
            String parentId = qwertPointProtocol.getPid();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            qwertPointProtocol.setPid(null);
            QueryWrapper<QwertPointProtocol> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointProtocol, req.getParameterMap());
            // 使用 eq 防止模糊查询
            queryWrapper.eq("pid", parentId);
            Page<QwertPointProtocol> page = new Page<QwertPointProtocol>(pageNo, pageSize);
            IPage<QwertPointProtocol> pageList = qwertPointProtocolService.page(page, queryWrapper);
            return Result.OK(pageList);
        }
	}

	 /**
      * 获取子数据
      * @param qwertPointProtocol
      * @param req
      * @return
      */
	@AutoLog(value = "qwert_point_protocol-获取子数据")
	@ApiOperation(value="qwert_point_protocol-获取子数据", notes="qwert_point_protocol-获取子数据")
	@GetMapping(value = "/childList")
	public Result<?> queryPageList(QwertPointProtocol qwertPointProtocol,HttpServletRequest req) {
		QueryWrapper<QwertPointProtocol> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointProtocol, req.getParameterMap());
		List<QwertPointProtocol> list = qwertPointProtocolService.list(queryWrapper);
		IPage<QwertPointProtocol> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
		return Result.OK(pageList);
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
	@AutoLog(value = "qwert_point_protocol-批量获取子数据")
    @ApiOperation(value="qwert_point_protocol-批量获取子数据", notes="qwert_point_protocol-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<QwertPointProtocol> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<QwertPointProtocol> list = qwertPointProtocolService.list(queryWrapper);
            IPage<QwertPointProtocol> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }
	
	/**
	 *   添加
	 *
	 * @param qwertPointProtocol
	 * @return
	 */
	@AutoLog(value = "qwert_point_protocol-添加")
	@ApiOperation(value="qwert_point_protocol-添加", notes="qwert_point_protocol-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertPointProtocol qwertPointProtocol) {
		qwertPointProtocolService.addQwertPointProtocol(qwertPointProtocol);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qwertPointProtocol
	 * @return
	 */
	@AutoLog(value = "qwert_point_protocol-编辑")
	@ApiOperation(value="qwert_point_protocol-编辑", notes="qwert_point_protocol-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertPointProtocol qwertPointProtocol) {
		qwertPointProtocolService.updateQwertPointProtocol(qwertPointProtocol);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_protocol-通过id删除")
	@ApiOperation(value="qwert_point_protocol-通过id删除", notes="qwert_point_protocol-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertPointProtocolService.deleteQwertPointProtocol(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "qwert_point_protocol-批量删除")
	@ApiOperation(value="qwert_point_protocol-批量删除", notes="qwert_point_protocol-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertPointProtocolService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_protocol-通过id查询")
	@ApiOperation(value="qwert_point_protocol-通过id查询", notes="qwert_point_protocol-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertPointProtocol qwertPointProtocol = qwertPointProtocolService.getById(id);
		if(qwertPointProtocol==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qwertPointProtocol);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qwertPointProtocol
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertPointProtocol qwertPointProtocol) {
		return super.exportXls(request, qwertPointProtocol, QwertPointProtocol.class, "qwert_point_protocol");
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
		return super.importExcel(request, response, QwertPointProtocol.class);
    }

	 @RequestMapping(value = "/queryProtocolTreeList", method = RequestMethod.GET)
	 public Result<List<ProtocolTreeModel>> queryProtocolTreeList() {
		 Result<List<ProtocolTreeModel>> result = new Result<>();
		 try {
			 List<ProtocolTreeModel> list = qwertPointProtocolService.queryProtocolTreeList();
			 result.setResult(list);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 log.error(e.getMessage(),e);
		 }
		 return result;
	 }

}
