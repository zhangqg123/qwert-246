package org.jeecg.modules.qwert.zsj.controller;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.QpDevRev;
import org.jeecg.modules.qwert.point.entity.QwertPointDev;
import org.jeecg.modules.qwert.point.entity.QwertPointProtocol;
import org.jeecg.modules.qwert.point.entity.QwertPointTarget;
import org.jeecg.modules.qwert.zsj.entity.ZsjZbA;
import org.jeecg.modules.qwert.zsj.service.IZsjZbAService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.qwert.zsj.utils.JsonUtil;
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
 * @Description: zsj_zb_a
 * @Author: jeecg-boot
 * @Date:   2022-02-22
 * @Version: V1.0
 */
@Api(tags="zsj_zb_a")
@RestController
@RequestMapping("/zsj/zsjZbA")
@Slf4j
public class ZsjZbAController extends JeecgController<ZsjZbA, IZsjZbAService> {
	@Autowired
	private IZsjZbAService zsjZbAService;
	
	/**
	 * 分页列表查询
	 *
	 * @param zsjZbA
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "zsj_zb_a-分页列表查询")
	@ApiOperation(value="zsj_zb_a-分页列表查询", notes="zsj_zb_a-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(ZsjZbA zsjZbA,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ZsjZbA> queryWrapper = QueryGenerator.initQueryWrapper(zsjZbA, req.getParameterMap());
		Page<ZsjZbA> page = new Page<ZsjZbA>(pageNo, pageSize);
		IPage<ZsjZbA> pageList = zsjZbAService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	 @ApiOperation(value = "jst_zc_dev-测试", notes = "jst_zc_dev-测试")
	 @PostMapping(value = "/createData")
	 public Result<?> createData() throws ParseException {
		 String jsonStr = JsonUtil.readJsonFile(new File("d:/response2.json"));
		 Map jsonMap = (Map) JSON.parse(jsonStr);
		 JSONArray rows = (JSONArray) jsonMap.get("rows");
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		 for(int i=0;i<rows.size();i++) {
			 JSONObject jsonObject = rows.getJSONObject(i);
			 ZsjZbA zsjZbA=new ZsjZbA();
			 zsjZbA.setProjectname((String) jsonObject.get("projectname"));
			 zsjZbA.setBiaoduanname((String) jsonObject.get("biaoduanname"));
			 zsjZbA.setBiaoduanno((String) jsonObject.get("biaoduanno"));
			 zsjZbA.setProjectno((String) jsonObject.get("projectno"));
			 String zhongbiaodate = (String) jsonObject.get("zhongbiaodate");
			 if(zhongbiaodate!=null&&!"".equals(zhongbiaodate)){
				 zsjZbA.setZhongbiaodate(sdf.parse(zhongbiaodate));
			 }
			 String entrytime = (String) jsonObject.get("entrytime");
			 if(entrytime!=null && !"".equals(entrytime)){
				 zsjZbA.setEntrytime(sdf.parse(entrytime));
			 }
			 String kaibiaodate = (String) jsonObject.get("kaibiaodate");
			 if(kaibiaodate!=null && !"".equals(kaibiaodate)){
				 zsjZbA.setKaibiaodate(sdf.parse(kaibiaodate));
			 }
			 String zhaobiaodate = (String) jsonObject.get("zhaobiaodate");
			 if(zhaobiaodate!=null && !"".equals(zhaobiaodate)){
				 zsjZbA.setZhaobiaodate(sdf.parse(zhaobiaodate));
			 }
			 String hetongdate = (String) jsonObject.get("hetongdate");
			 if(hetongdate!=null && !"".equals(hetongdate)){
				 zsjZbA.setHetongdate(sdf.parse(hetongdate));
			 }
			 zsjZbAService.save(zsjZbA);
		 }
		 return Result.OK("添加成功！");
	 }

	/**
	 *   添加
	 *
	 * @param zsjZbA
	 * @return
	 */
	@AutoLog(value = "zsj_zb_a-添加")
	@ApiOperation(value="zsj_zb_a-添加", notes="zsj_zb_a-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ZsjZbA zsjZbA) {
		zsjZbAService.save(zsjZbA);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param zsjZbA
	 * @return
	 */
	@AutoLog(value = "zsj_zb_a-编辑")
	@ApiOperation(value="zsj_zb_a-编辑", notes="zsj_zb_a-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ZsjZbA zsjZbA) {
		zsjZbAService.updateById(zsjZbA);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "zsj_zb_a-通过id删除")
	@ApiOperation(value="zsj_zb_a-通过id删除", notes="zsj_zb_a-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		zsjZbAService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "zsj_zb_a-批量删除")
	@ApiOperation(value="zsj_zb_a-批量删除", notes="zsj_zb_a-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.zsjZbAService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "zsj_zb_a-通过id查询")
	@ApiOperation(value="zsj_zb_a-通过id查询", notes="zsj_zb_a-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		ZsjZbA zsjZbA = zsjZbAService.getById(id);
		if(zsjZbA==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(zsjZbA);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param zsjZbA
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ZsjZbA zsjZbA) {
        return super.exportXls(request, zsjZbA, ZsjZbA.class, "zsj_zb_a");
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
        return super.importExcel(request, response, ZsjZbA.class);
    }

}
