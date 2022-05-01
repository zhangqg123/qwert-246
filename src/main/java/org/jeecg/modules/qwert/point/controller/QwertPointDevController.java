package org.jeecg.modules.qwert.point.controller;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchRead;
import org.jeecg.modules.qwert.conn.modbus4j.source.BatchResults;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusFactory;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ErrorResponseException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.IpParameters;
import org.jeecg.modules.qwert.conn.modbus4j.source.locator.BaseLocator;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ReadInputRegistersResponse;
import org.jeecg.modules.qwert.conn.modbus4j.test.TestSerialPortWrapper;
import org.jeecg.modules.qwert.conn.qudong.QwertMaster;
import org.jeecg.modules.qwert.conn.qudong.base.QudongUtils;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadDianzongRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadDianzongResponse;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Request;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Response;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaResponse;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarResponse;
import org.jeecg.modules.qwert.conn.snmp.SnmpData;
import org.jeecg.modules.qwert.point.entity.*;
import org.jeecg.modules.qwert.point.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.qwert.point.utils.JstConstant;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: qwert_point_dev
 * @Author: jeecg-boot
 * @Date:   2021-11-16
 * @Version: V1.0
 */
@Api(tags="qwert_point_dev")
@RestController
@RequestMapping("/point/qwertPointDev")
@Slf4j
public class QwertPointDevController extends JeecgController<QwertPointDev, IQwertPointDevService> {
 	@Autowired
 	private IQwertPointDevService qwertPointDevService;
	@Autowired
	private IQwertPointTargetService qwertPointTargetService;
	@Autowired
	private IQwertPointProtocolService qwertPointProtocolService;
	 @Autowired
	 private IQwertPointProtocolClassService qwertPointProtocolClassService;
	 @Lazy
	 @Resource
	 private RedisUtil redisUtil;

	/**
	 * 分页列表查询
	 *
	 * @param qwertPointDev
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "qwert_point_dev-分页列表查询")
	@ApiOperation(value="qwert_point_dev-分页列表查询", notes="qwert_point_dev-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(QwertPointDev qwertPointDev,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<QwertPointDev> queryWrapper = QueryGenerator.initQueryWrapper(qwertPointDev, req.getParameterMap());
		Page<QwertPointDev> page = new Page<QwertPointDev>(pageNo, pageSize);
		IPage<QwertPointDev> pageList = qwertPointDevService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @AutoLog(value = "qwert_point_target-target列表查询")
	 @ApiOperation(value="qwert_point_target-点列表查询", notes="qwert_point_target-点列表查询")
	 @GetMapping(value = "/qptList")
	 public Result<?> queryTargetList(HttpServletRequest req) {
		 String dev_no=req.getParameter("code");
		 List<QwertPointTarget> qptList = qwertPointTargetService.queryQptList(dev_no);
		 return Result.OK(qptList);
	 }

	/**
	 *   添加
	 *
	 * @param qwertPointDev
	 * @return
	 */
	@AutoLog(value = "qwert_point_dev-添加")
	@ApiOperation(value="qwert_point_dev-添加", notes="qwert_point_dev-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody QwertPointDev qwertPointDev) {
		qwertPointDevService.save(qwertPointDev);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param qwertPointDev
	 * @return
	 */
	@AutoLog(value = "qwert_point_dev-编辑")
	@ApiOperation(value="qwert_point_dev-编辑", notes="qwert_point_dev-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody QwertPointDev qwertPointDev) {
		qwertPointDevService.updateById(qwertPointDev);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_dev-通过id删除")
	@ApiOperation(value="qwert_point_dev-通过id删除", notes="qwert_point_dev-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		qwertPointDevService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "qwert_point_dev-批量删除")
	@ApiOperation(value="qwert_point_dev-批量删除", notes="qwert_point_dev-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.qwertPointDevService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "qwert_point_dev-通过id查询")
	@ApiOperation(value="qwert_point_dev-通过id查询", notes="qwert_point_dev-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		QwertPointDev qwertPointDev = qwertPointDevService.getById(id);
		if(qwertPointDev==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(qwertPointDev);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param qwertPointDev
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, QwertPointDev qwertPointDev) {
        return super.exportXls(request, qwertPointDev, QwertPointDev.class, "qwert_point_dev");
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
        return super.importExcel(request, response, QwertPointDev.class);
    }

	 @GetMapping(value = "/devTarget")
	 public Result<?> devTarget(HttpServletRequest request) throws InterruptedException {
		 long start, end;
		 start = System.currentTimeMillis();

		 String devNo=request.getParameter("code");
		 QueryWrapper<QwertPointDev> queryWrapper = new QueryWrapper<QwertPointDev>();
		 // target表dev_no 对应于 dev表id字段
		 queryWrapper.eq("id", devNo);
		 QwertPointDev qpd = qwertPointDevService.getOne(queryWrapper);
		 if(qpd==null) {
			 return null;
		 }
//		String devName = jzd.getDevName();
		 String catNo = qpd.getDevCat();
		 String protocolId=qpd.getType();
		 QwertPointProtocol qpp = qwertPointProtocolService.getById(protocolId);
		 String conInfo = qpd.getConInfo();
	//	 String revList = qpDevRev.getRevList();
		 List<QwertPointTarget> qptList = null;
		 List resList = new ArrayList();
		 qptList = qwertPointTargetService.queryQptList(devNo);

		 JSONObject jsonConInfo = JSON.parseObject(conInfo);
		 String type = jsonConInfo.getString("type");
//		 String proType = jsonConInfo.getString("proType");
		 String proType=qpp.getProtocolNo();

		 if (proType.toUpperCase().indexOf("MODBUS")!=-1) {
			 handleModbus(proType,devNo,catNo, qptList, resList, jsonConInfo);
		 }

		 if (proType.toUpperCase().indexOf("SNMP")!=-1) {
			 handleSnmp(qptList, resList, jsonConInfo);
		 }

		 if (proType.toUpperCase().indexOf("DELTA")!=-1) {
			 handleDelta(proType, qptList, resList, jsonConInfo);
		 }

		 if (proType.toUpperCase().indexOf("KSTAR")!=-1) {
			 handlekStar(proType, qptList, resList, jsonConInfo);
		 }
		 if (proType.toUpperCase().indexOf("7000D")!=-1) {
			 handleM7000D(proType, qptList, resList, jsonConInfo);
		 }
		 if (proType.toUpperCase().indexOf("PMBUS")!=-1) {
			 handlePmbus(proType, qptList, resList, jsonConInfo);
		 }

		 end = System.currentTimeMillis();
		 System.out.println((resList.toString()));
		 System.out.println("开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
		 List<QwertPointTarget2> qpt2List=new ArrayList<QwertPointTarget2>();
		 for(int i=0;i<resList.size();i++){
			 String res = (String) resList.get(i);
			 res=res.substring(1,res.length()-1);
		//	 if(res.indexOf(",")!=-1){
				 String[] tres = res.split(",");
				 for(int j=0;j<tres.length;j++){
					 String[] tres1 = tres[j].split("=");
					 String targetNo = tres1[0].trim();
					 String targetValue = tres1[1].trim();
					 for(int h=0;h<qptList.size();h++){
						 QwertPointTarget qpt = qptList.get(h);
						 String ta = (qpt.getAddress().split("\\."))[0];
						 if(targetNo.equals(qpt.getId())){
							 if(qpt.getAddress().indexOf(".")==-1){
								 QwertPointTarget2 qpt2=new QwertPointTarget2();
								 qpt2.setTargetNo(targetNo);
								 qpt2.setTargetName(qpt.getTargetName());
							 	 qpt2.setTargetValue(targetValue);
								 qpt2List.add(qpt2);
							 }else{
								 String binaryStr = Integer.toBinaryString(Integer.parseInt(targetValue));
								 while(binaryStr.length() < 16){
									 binaryStr = "0"+binaryStr;
								 }
								 List<QwertPointTarget> qpt3List = qptList.stream().filter(u -> ta.equals((u.getAddress().split("\\."))[0])).collect(Collectors.toList());
								 for(int k=0;k<qpt3List.size();k++){
									 QwertPointTarget qpt3 = qpt3List.get(k);
									 QwertPointTarget2 qpt2=new QwertPointTarget2();
									 String a1=qpt3.getAddress();
									 String[] a2 = a1.split("\\.");
									 String a6=null;
									 int a4 = Integer.parseInt(a2[1]);
									 int a5 = 15 - a4;
									 a6 = binaryStr.substring(a5, a5 + 1);
									 qpt2.setTargetNo(qpt3.getTargetNo());
									 qpt2.setTargetName(qpt3.getTargetName());
									 qpt2.setTargetValue(a6);
									 qpt2List.add(qpt2);
								 }
							 }
							 break;
						 }
					 }
		//		 }
			 }
		 }
		 return Result.ok(resList);
	 }

	 //	@AutoLog(value = "jst_zc_dev-测试")
	 @ApiOperation(value = "jst_zc_dev-测试", notes = "jst_zc_dev-测试")
	 @PostMapping(value = "/conntest")
	 public Result<?> conntest(@RequestBody QpDevRev qpDevRev) throws InterruptedException {
    	/*		 List<JstZcConfig> jzConList = jstZcConfigService.list();

		 for (int i=0;i<jzConList.size();i++) {
			 JstZcConfig jc = jzConList.get(i);
			 if(jc.getConfigNo().equals("debugflag")) {
				 JstConstant.debugflag=Integer.parseInt(jc.getConfigValue());
			 }
			 if(jc.getConfigNo().equals("sleeptime")) {
				 JstConstant.sleeptime=Integer.parseInt(jc.getConfigValue());
			 }
		 } */
		 long start, end;
		 start = System.currentTimeMillis();

		 String devNo=qpDevRev.getDevNo();
		 QueryWrapper<QwertPointDev> queryWrapper = new QueryWrapper<QwertPointDev>();
		 // target表dev_no 对应于 dev表id字段
		 queryWrapper.eq("id", devNo);
		 QwertPointDev qpd = qwertPointDevService.getOne(queryWrapper);
		 if(qpd==null) {
			 return null;
		 }
//		String devName = jzd.getDevName();
		 String catNo = qpd.getDevCat();
		 String protocolId=qpd.getType();
		 QwertPointProtocol qpp = qwertPointProtocolService.getById(protocolId);
		 String conInfo = qpDevRev.getConnInfo();
		 String revList = qpDevRev.getRevList();
		 List<QwertPointTarget> qptList = null;
		 List resList = new ArrayList();
		 if (qpDevRev.getDevCat() != null) {
			 qptList = qwertPointTargetService.queryQptList(devNo);
		 } else {
			 qptList = JSONArray.parseArray(revList, QwertPointTarget.class);
		 }

		 JSONObject jsonConInfo = JSON.parseObject(conInfo);
		 String type = jsonConInfo.getString("type");
//		 String proType = jsonConInfo.getString("proType");
		 String proType=qpp.getProtocolNo();

		 if (proType.toUpperCase().indexOf("MODBUS")!=-1) {
			 handleModbus(proType,devNo,catNo, qptList, resList, jsonConInfo);
		 }

		 if (proType.toUpperCase().indexOf("SNMP")!=-1) {
			 handleSnmp(qptList, resList, jsonConInfo);
		 }

		 if (proType.toUpperCase().indexOf("DELTA")!=-1) {
			 handleDelta(proType, qptList, resList, jsonConInfo);
		 }

		 if (proType.toUpperCase().indexOf("KSTAR")!=-1) {
			 handlekStar(proType, qptList, resList, jsonConInfo);
		 }
		 if (proType.toUpperCase().indexOf("7000D")!=-1) {
			 handleM7000D(proType, qptList, resList, jsonConInfo);
		 }
		 if (proType.toUpperCase().indexOf("PMBUS")!=-1) {
			 handlePmbus(proType, qptList, resList, jsonConInfo);
		 }

		 end = System.currentTimeMillis();
		 System.out.println((resList.toString()));
		 System.out.println("开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
		 //resList 开始循环

		 return Result.ok(resList);
	 }

	 public void handleModbus(String proType,String devNo,String catNo, List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo)
			 throws InterruptedException {
		 TestSerialPortWrapper wrapper = null;
		 String commPortId = jsonConInfo.getString("com");
		 String ipAddress = jsonConInfo.getString("ipAddress");
		 if(commPortId!=null) {
			 int baudRate = Integer.parseInt(jsonConInfo.getString("baudRate"));
			 int flowControlIn = 0;
			 int flowControlOut = 0;
			 int dataBits = Integer.parseInt(jsonConInfo.getString("dataBits"));
			 int stopBits = Integer.parseInt(jsonConInfo.getString("stopBits"));
			 int parity = Integer.parseInt(jsonConInfo.getString("parity"));

			 wrapper = new TestSerialPortWrapper(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);
		 }
		 IpParameters ipParameters = null;
		 if(ipAddress!=null) {
			 String port = jsonConInfo.getString("port");
			 ipParameters = new IpParameters();
			 ipParameters.setHost(ipAddress);
			 ipParameters.setPort(Integer.parseInt(port));
			 ipParameters.setEncapsulated(true);
		 }
		 ModbusFactory modbusFactory = new ModbusFactory();
		 ModbusMaster master=null;
		 if(proType.toUpperCase().indexOf("RTU")!=-1) {
			 master = modbusFactory.createRtuMaster(wrapper);
		 }
		 if(proType.toUpperCase().indexOf("ASCII")!=-1) {
			 master = modbusFactory.createAsciiMaster(wrapper);
		 }
		 if(proType.toUpperCase().indexOf("TCP")!=-1) {
			 master = modbusFactory.createTcpMaster(ipParameters, false);
		 }
		 String slave;
		 String packageBit;
		 String timeOut;
		 BatchResults<String> results;
		 slave = jsonConInfo.getString("slave");
		 packageBit = jsonConInfo.getString("packageBit");
		 String bitNumber = jsonConInfo.getString("bitNumber");
		 int pb=64;
		 if(packageBit!=null&&!packageBit.equals("")) {
			 pb=Integer.parseInt(packageBit);
		 }
		 int bn=10;
		 if(bitNumber!=null&&!bitNumber.equals("")) {
			 bn=Integer.parseInt(bitNumber);
		 }
		 timeOut = jsonConInfo.getString("timeOut");
		 String stime = jsonConInfo.getString("sleeptime");
		 int sleeptime= JstConstant.sleeptime;
		 if(stime!=null && !stime.equals("")) {
			 sleeptime=Integer.parseInt(stime);
		 }
		 boolean flag = false;

		 try {
			 master.init();
			 int slaveId = 0;
			 slaveId = Integer.parseInt(slave,16);
			 BatchRead<String> batch = new BatchRead<String>();
			 String tmpInstruct = null;
			 int pointNumber = 0;
			 int tmp2Offset = 0;
			 boolean batchSend = false;
			 for (int i = 0; i < jztList.size(); i++) {
				 QwertPointTarget jzt = jztList.get(i);
				 String di = jzt.getInstruct().substring(0,2);
				 int offset=0;
				 String ta = jzt.getAddress();
				 String[] tas=null;
				 if(ta!=null&&ta.indexOf(".")!=-1){
					 tas = ta.split("\\.");
					 ta=tas[0];
				 }
				 if(ta!=null&&ta.indexOf("(")!=-1){
					 tas = ta.split("\\(");
					 ta=tas[0];
				 }
				 if(jzt.getAddressType()!=null && jzt.getAddressType().equals("HEX")){
					 offset = Integer.parseInt(ta,16);
				 }else{
					 offset = Integer.parseInt(ta);
				 }

				 if (offset==tmp2Offset&&pointNumber>0) {
					 continue;
				 }
				 if (pointNumber>0 && (pointNumber > pb || (offset - tmp2Offset >= bn))) {
					 flag = true;
				 }
				 pointNumber++;
				 tmp2Offset=offset;
				 if (flag == true) {
					 //		System.out.println(i + "::" + offset);
					 results = master.send(batch);
					 Thread.sleep(sleeptime);
					 if(!results.toString().equals("{}")) {
						 resList.add(results.toString());
					 }
					 if(JstConstant.debugflag==1) {
						 System.out.println(results);
					 }
					 batch = new BatchRead<String>();
					 flag = false;
					 pointNumber = 0;
				 }

				 String dataType = "2";
				 if(jzt.getDataType()!=null){
					 dataType=jzt.getDataType();
				 }
				 if (di.equals("04")) {
					 if(catNo.trim().equals("D86")){
						 ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, offset, 2);
						 ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);
						 if (response.isException()){
							 System.out.println("Exception response: message=" + response.getExceptionMessage());
						 }else{
							 short[] retMessage = response.getShortData();
							 String rm1 = jzt.getId();
							 //					resList.add(rm1+"="+retMessage[0]);
							 resList.add("{"+rm1+"="+retMessage[0]+"}");
						 }
					 }else{
						 batch.addLocator(jzt.getId(),
								 BaseLocator.inputRegister(slaveId, offset, Integer.parseInt(dataType)));
						 batchSend = true;
					 }
				 }
				 if (di.equals("03")) {
					 batch.addLocator(jzt.getId(),
							 BaseLocator.holdingRegister(slaveId, offset, Integer.parseInt(dataType)));
					 batchSend = true;
				 }
				 if (di.equals("02")) {
					 batch.addLocator(jzt.getId(), BaseLocator.inputStatus(slaveId, offset));
					 batchSend = true;
				 }
				 Thread.sleep(sleeptime/2);
			 }
			 if (batchSend == true) {
				 results = master.send(batch);
				 Thread.sleep(sleeptime);
				 if(JstConstant.debugflag==1) {
					 System.out.println(results);
				 }
				 resList.add(results.toString());
//				resList.add("{gf10001c027=22354}");
			 }
			 if(JstConstant.debugflag==1) {
				 System.out.println(devNo+"::"+resList.size());
			 }
		 } catch (ModbusInitException e) {
			 e.printStackTrace();
		 } catch (ModbusTransportException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } catch (ErrorResponseException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } finally {
			 master.destroy();
		 }
	 }
	 private void handlePmbus(String proType, List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		 int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		 QwertMaster master = QudongUtils.getQwertMaster(proType, jsonConInfo);
		 String tmpInstruct=null;
		 byte[] retmessage = null;
		 for (int i = 0; i < jztList.size(); i++) {
			 QwertPointTarget jzt = jztList.get(i);
			 String instruct = jzt.getInstruct();
			 if(instruct.equals(tmpInstruct)){
				 String rm1 = jzt.getTargetNo();
				 String rm2 = jzt.getAddress();
				 String rm = null;
				 if(rm2.indexOf("$")!=-1){
					 String[] rm3=rm2.split("\\$");
					 rm=retmessage[Integer.parseInt(rm3[0])-1]+"";
				 }
				 resList.add(rm1+"="+rm);
				 continue;
			 }
			 String[] tmp = instruct.split("/");
			 try {
				 ReadDianzongRequest request = new ReadDianzongRequest(2.0f,slaveId, Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2]));
				 ReadDianzongResponse response = (ReadDianzongResponse) master.send(request);

				 if (response.isException())
					 System.out.println("Exception response: message=" + response.getExceptionMessage());
				 else{
					 System.out.println(Arrays.toString(response.getShortData()));
					 //			resList.add(Arrays.toString(response.getShortData()));
					 retmessage =  response.getRetData();
					 String rm1 = jzt.getTargetNo();
					 String rm2 = jzt.getAddress();
					 int rm = 0;
					 if(rm2.indexOf("_")!=-1){
						 short[] rp = response.getShortData();
						 //		String[] rm3=rm2.split("_");
						 //		byte trm = retmessage[Integer.parseInt(rm3[0])] ;
						 //		rm=trm & 0xff;
						 rm=rp[7];
					 }
					 if(rm2.indexOf("$")!=-1){
						 String[] rm3=rm2.split("\\$");
						 rm=retmessage[Integer.parseInt(rm3[0])-1];
					 }
					 resList.add(rm1+"="+rm);
				 }
			 }
			 catch (QudongTransportException e) {
				 e.printStackTrace();
			 }
			 tmpInstruct = jzt.getInstruct();
		 }
	 }

	 private void handleM7000D(String proType, List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		 int slaveId = Integer.parseInt(jsonConInfo.getString("slave"),16);
		 QwertMaster master = QudongUtils.getQwertMaster(proType,jsonConInfo);
		 String tmpInstruct=null;
		 String retmessage=null;
		 for (int i = 0; i < jztList.size(); i++) {
			 QwertPointTarget jzt = jztList.get(i);
			 String instruct = jzt.getInstruct();
			 if(instruct.equals(tmpInstruct)){
				 String rm1 = jzt.getTargetNo();
				 String rm2 = jzt.getAddress();
				 String rm = QudongUtils.getM7000DString(retmessage, rm2);
				 resList.add(rm1+"="+rm);
				 continue;
			 }
			 try {

				 ReadM7000Request request = new ReadM7000Request(slaveId, 6);
				 ReadM7000Response response = (ReadM7000Response) master.send(request);
				 if (response==null){
					 System.out.println("Exception response: message=null");
				 }
				 if (response.isException())
					 System.out.println("Exception response: message=" + response.getExceptionMessage());
				 else{
					 String rbd=null;
					 if(response.getBinData().length()<8) {
						 rbd=response.getBinData()+"00";
					 }else {
						 rbd=response.getBinData();
					 }
					 retmessage =  rbd.substring(0,8);
					 String rm1 = jzt.getTargetNo();
					 String rm2 = jzt.getAddress();
					 // 不清楚应该是低位在前，高位在前
					 String rm = QudongUtils.getM7000DString(retmessage, rm2);
					 resList.add(rm1+"="+rm);
				 }
			 }
			 catch (QudongTransportException e) {
				 e.printStackTrace();
			 }
			 tmpInstruct = jzt.getInstruct();
		 }
	 }


	 private void handlekStar(String proType, List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		 int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		 QwertMaster master = QudongUtils.getQwertMaster(proType,jsonConInfo);
		 String tmpInstruct=null;
		 String retmessage=null;
		 for (int i = 0; i < jztList.size(); i++) {
			 QwertPointTarget jzt = jztList.get(i);
			 String instruct = jzt.getInstruct();
			 if(instruct.equals(tmpInstruct)){
				 String rm1 = jzt.getTargetNo();
				 String rm2 = jzt.getAddress();
				 String rm = getKstarString(retmessage, rm2);

				 resList.add(rm1+"="+rm);
				 continue;
			 }
			 try {
				 ReadKstarRequest request = new ReadKstarRequest(slaveId, 81,49);
				 ReadKstarResponse response = (ReadKstarResponse) master.send(request);

				 if (response.isException())
					 System.out.println("Exception response: message=" + response.getExceptionMessage());
				 else{
					 System.out.println(response.getMessage());
					 retmessage =  response.getMessage();
					 String rm1 = jzt.getTargetNo();
					 String rm2 = jzt.getAddress();
					 String rm = getKstarString(retmessage, rm2);
					 resList.add(rm1+"="+rm);
				 }
			 }
			 catch (QudongTransportException e) {
				 e.printStackTrace();
			 }
			 tmpInstruct = jzt.getInstruct();
		 }
	 }

	 @Nullable
	 private String getKstarString(String retmessage, String rm2) {
		 String rm = null;
		 if(rm2.indexOf("_")!=-1) {
			 String[] rm3 = rm2.split("_");
			 rm = retmessage.substring(Integer.parseInt(rm3[0]) - 1, Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1]) -1);
		 }
		 if(rm2.indexOf("$")!=-1) {
			 String[] rm3 = rm2.split("\\$");
			 int rm4 = Integer.parseInt(rm3[0]) - 1;
			 int rm5 = Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1]);
			 rm = retmessage.substring(Integer.parseInt(rm3[0]) - 1, Integer.parseInt(rm3[0]) + Integer.parseInt(rm3[1])-1);
		 }
		 return rm;
	 }

	 private void handleDelta(String proType, List<QwertPointTarget> qptList, List resList, JSONObject jsonConInfo) {
		 int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		 QwertMaster master = QudongUtils.getQwertMaster(proType, jsonConInfo);
		 String tmpInstruct=null;
		 String retmessage=null;
		 for (int i = 0; i < qptList.size(); i++) {
			 QwertPointTarget jzt = qptList.get(i);
			 String instruct = jzt.getInstruct();
			 if(instruct.equals(tmpInstruct)){
				 String rm1 = jzt.getTargetNo();
				 String rm2 = jzt.getAddress();
				 String rm = retDeltaString(retmessage, rm2);
				 resList.add(rm1+"="+rm);
				 continue;
			 }
			 try {
				 ReadDeltaRequest request = new ReadDeltaRequest(slaveId, instruct);
				 ReadDeltaResponse response = (ReadDeltaResponse) master.send(request);

				 if (response.isException())
					 System.out.println("Exception response: message=" + response.getExceptionMessage());
				 else{
					 //				System.out.println(response.getMessage());
					 retmessage =  response.getMessage();
					 String rm1 = jzt.getTargetNo();
					 String rm2 = jzt.getAddress();
					 String rm = retDeltaString(retmessage, rm2);
					 resList.add(rm1+"="+rm);
				 }
			 }
			 catch (QudongTransportException e) {
				 e.printStackTrace();
			 }
			 tmpInstruct = jzt.getInstruct();
		 }
		 int rr=0;
	 }

	 private String retDeltaString(String retmessage, String rm2) {
		 String retValue="--";
		 int rn = rm2.lastIndexOf(",");
		 String rm3 = rm2.substring(rn + 1);
		 String rm4[] = rm3.split("\\)");
		 String rm5 = rm4[0];
		 String[] rm6 = retmessage.split(";");
		 String r7 = rm6[Integer.parseInt(rm5)];
		 if (rm2.indexOf("=") == -1) {
			 retValue= r7;
		 }else{
			 String[] r8 = rm2.split("=");
			 if(r7.equals(r8[1])){
				 retValue= r7;
			 }
		 }
		 return retValue;
	 }

	 private void handleSnmp(List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		 String version;
		 String timeOut;
		 String community;
		 String ipAddress=jsonConInfo.getString("ipAddress");
		 version = jsonConInfo.getString("version");
		 timeOut = jsonConInfo.getString("timeOut");
		 community = jsonConInfo.getString("community");
		 if(community==null){
			 community="public";
		 }
		 jztList.stream().sorted(Comparator.comparing(QwertPointTarget::getInstruct));
		 List<String> oidList = new ArrayList<String>();
		 String tmpInstruct=null;
		 String retmessage=null;
		 for (int i = 0; i < jztList.size(); i++) {
			 QwertPointTarget jzt = jztList.get(i);
			 String oidval = jzt.getInstruct();
			 String rm1 = jzt.getTargetNo();
			 String rm2 = jzt.getAddress();

			 if(oidval.equals(tmpInstruct)){
				 if(rm2!=null && jzt.getInfoType().equals("digital")){
					 String rm = snmpString(retmessage, rm2);
					 resList.add(rm1+"="+rm);
				 };
				 continue;
			 }
			 List snmpList = SnmpData.snmpGet(ipAddress, community, oidval,null);

			 if(snmpList.size()>0) {
				 String tmpRet = (String) snmpList.get(0);
				 String rm=null;
				 if(tmpRet!=null&&!tmpRet.equals("")){
					 retmessage = tmpRet.split("=")[1];
				 }
				 if(rm2==null||rm2.equals("")){
					 resList.add(rm1+"="+retmessage);
				 }else{
					 if(jzt.getInfoType().equals("digital")){
						 rm = snmpString(retmessage, rm2);
						 resList.add(rm1+"="+rm);
					 }
				 }

//				for(int j=0;j<snmpList.size();j++) {
//					resList.add(snmpList.get(j));
//				}
			 }
			 tmpInstruct=jzt.getInstruct();
		 }
	 }

	 @Nullable
	 private String snmpString(String retmessage, String rm2) {
		 String rm = null;
		 retmessage=retmessage.trim();
		 if(rm2.indexOf("$")!=-1) {
			 String[] rm3 = rm2.split("\\$");
			 int rm4 = Integer.parseInt(rm3[0]);
			 rm = retmessage.substring(rm4, rm4+1);
		 }else{
			 String binaryStr = Integer.toBinaryString(Integer.valueOf(retmessage.trim()));
			 while(binaryStr.length() < 16){
				 binaryStr = "0"+binaryStr;
			 }
			 int pos = 15 - Integer.valueOf(rm2);
			 rm = binaryStr.substring(pos,pos+1);
		 }
		 return rm;
	 }

}
