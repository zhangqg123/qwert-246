package org.jeecg.modules.qwert.zsj.work;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.qwert.zsj.entity.ZsjZbA;
import org.jeecg.modules.qwert.zsj.service.IZsjZbAService;
import org.jeecg.modules.qwert.zsj.utils.JsonUtil;
import org.jeecg.modules.qwert.zsj.utils.WorkResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

/**
* @Description: zsj_zb_a
* @Author: jeecg-boot
* @Date:   2022-02-22
* @Version: V1.0
*/

@RestController
@RequestMapping("/work/zsj/zsjZbA")
@Slf4j
public class WorkZsjZbAController extends JeecgController<ZsjZbA, IZsjZbAService> {
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
   @PostMapping(value = "/list")
   public WorkResult<?> queryPageList(ZsjZbA zsjZbA,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
       String tId = req.getParameter("traceId");
       QueryWrapper<ZsjZbA> queryWrapper = QueryGenerator.initQueryWrapper(zsjZbA, req.getParameterMap());
       Page<ZsjZbA> page = new Page<ZsjZbA>(pageNo, pageSize);
       IPage<ZsjZbA> pageList = zsjZbAService.page(page, queryWrapper);
       if(pageList.getRecords().size()>0){
           return WorkResult.OK(pageList.getRecords());
       }
       return WorkResult.error("查询失败");
   }

    @PostMapping(value = "/queryBy")
    public WorkResult<?> queryBy(ZsjZbA zsjZbA,
                                       @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                       HttpServletRequest req) {
        String tId = req.getParameter("traceId");
        QueryWrapper<ZsjZbA> queryWrapper = QueryGenerator.initQueryWrapper(zsjZbA, req.getParameterMap());
        Page<ZsjZbA> page = new Page<ZsjZbA>(pageNo, pageSize);
        IPage<ZsjZbA> pageList = zsjZbAService.page(page, queryWrapper);

        if(pageList.getRecords().size()>0){
            return WorkResult.OK(pageList.getRecords().get(0));
        }
        return WorkResult.error("查询失败");
    }

    @PostMapping(value = "/queryById")
    public WorkResult<?> queryById(@RequestParam(name="id",required=true) String id) {
        ZsjZbA zsjZbA = zsjZbAService.getById(id);
        if(zsjZbA==null) {
            return WorkResult.error("未找到对应数据");
        }
        return WorkResult.OK(zsjZbA);
    }

}
