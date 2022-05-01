package org.jeecg.modules.qwert.point.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
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
import org.jeecg.modules.qwert.point.mapper.QwertPointDevMapper;
import org.jeecg.modules.qwert.point.service.*;
import org.jeecg.modules.qwert.point.utils.JstConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
@Service
public class QwertPointJobServiceImpl extends ServiceImpl<QwertPointDevMapper, QwertPointDev> implements IQwertPointJobService {
	@Lazy
	@Resource
	private RedisUtil redisUtil;
	@Autowired
	private IQwertPointCatService jstZcCatService;
	@Resource
	private QwertPointDevMapper jstZcDevMapper;
	@Autowired
	private IQwertPointTargetService qwertPointTargetService;
	@Autowired
	private IQwertPointAlarmService jstZcAlarmService;
	@Autowired
	private IQwertPointConfigService jstZcConfigService;
	@Autowired
	private IQwertPointDevService jstZcDevService;
	@Autowired
	private IQwertPointProtocolService qwertPointProtocolService;

    private List<QwertPointCat> jzcList;
    private List<QwertPointDev> jzdList;
    private List<QwertPointTarget> jztList;
    
	public List<QwertPointDev> queryJzdList2(String catNo) {
		List<QwertPointDev> jzdList = this.jstZcDevMapper.queryJzdList2(catNo);
		return jzdList;
	}
	

	/**
	 * 扫描分类
	 * 
	 * @return
	 * @throws JMSException 
	 */
	@Override
	public void readCat(String catOrigin) {
		long start, end;
		start = System.currentTimeMillis();
		List<QwertPointDev> jzdCollect = queryJzdList2(catOrigin);
		for (int i = 0; i < jzdCollect.size(); i++) {
//			if(!JstConstant.runflag) {
//				break;
//			}
			QwertPointDev dev = jzdCollect.get(i);
			readDev(dev.getId());
		}
		end = System.currentTimeMillis();
		System.out.println(catOrigin+" 开始时间:" + start + "; 结束时间:" + end + "; 用时:" + (end - start) + "(ms)");
	}

	@Override
	public void readDev(String devNos) {
		long nstart, nend;
		nstart = System.currentTimeMillis();
		String[] tmpDevNos = devNos.split(",");
		for(int i=0;i<tmpDevNos.length;i++) {
			long start, end;
			start = System.currentTimeMillis();

			String devNo = tmpDevNos[i];
			List resList = new ArrayList();
			//	JstZcDev jzd = getById(devId);
			//	String devNo=jzd.getDevNo();
			QueryWrapper<QwertPointDev> queryWrapper = new QueryWrapper<QwertPointDev>();
			queryWrapper.eq("dev_no", devNo);
			QwertPointDev jzd = jstZcDevService.getOne(queryWrapper);
			if(jzd==null) {
				return;
			}
			String devName = jzd.getDevName();
			String devPos=jzd.getDevPos();
			String catNo = jzd.getDevCat();
			String orgUser = jzd.getOrgUser();
			String modNo = jzd.getModNo();
			if (modNo == null || modNo.equals("")) {
				modNo = "blank";
			}
			String conInfo = jzd.getConInfo();
			JSONObject jsonConInfo = JSON.parseObject(conInfo);
			String type = jsonConInfo.getString("type");
//			String proType = jsonConInfo.getString("proType");
			String stime = jsonConInfo.getString("sleeptime");
			int sleeptime = JstConstant.sleeptime;
			if (stime != null && !stime.equals("")) {
				sleeptime = Integer.parseInt(stime);
			}

			String protocolId=jzd.getType();
			QwertPointProtocol qpp = qwertPointProtocolService.getById(protocolId);
			String proType=qpp.getProtocolNo();

			List<QwertPointTarget> qptList = null;
//			if (orgUser.equals("guangfa")) {
	//			jztCollect = qwertPointTargetService.queryJztList5(devNo);
				qptList = qwertPointTargetService.queryQptList(jzd.getId());
//			}
//			if (orgUser.equals("jinshitan")) {
//				jztCollect = qwertPointTargetService.queryJztList4(catNo);
//			}
			boolean dbflag = false;
			boolean alarmflag=false;
			String alarm=null;
			if (proType.toUpperCase().indexOf("7000D")!=-1) {
				alarm=handleM7000d(jsonConInfo, resList, qptList);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().indexOf("PMBUS")!=-1) {
				alarm=handlePmbus(qptList, resList, jsonConInfo);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().indexOf("KSTAR")!=-1) {
				alarm=handlekStar(qptList, resList, jsonConInfo);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().indexOf("DELTA")!=-1) {
				alarm=handleDelta(qptList, resList, jsonConInfo);
				alarmflag=true;
				dbflag=true;
			}
			if (proType.toUpperCase().indexOf("MODBUS")!=-1) {
				alarm=handleModbus(proType, resList, devNo, devName, catNo, jsonConInfo, sleeptime,
						qptList);
				alarmflag=true;
				dbflag=true;
			}

			if (proType.toUpperCase().indexOf("SNMP")!=-1) {
				alarm=handleSnmp(type, resList, devNo, devName, catNo, jsonConInfo, qptList);
				alarmflag=true;
				dbflag=true;
			}
			if(dbflag||alarmflag) {
				if(alarmflag&&alarm!=null) {
					String alarmNo=null;
					String alarmValue=null;
					boolean saveFlag = false;
					boolean updateFlag =false;
					if(alarm.equals("connfail")){
						String conn = (String) redisUtil.get(devNo + "-conn");
						if(conn==null||conn.equals("connect")){
							alarmNo=devNo;
							alarmValue=devName+"连接中断";
							saveFlag=true;
						}
					}else {
						String rAlarm= (String) redisUtil.get(devNo+"-alarm");
						String[] tmpAlarm = alarm.split("::");
						alarmNo = tmpAlarm[0];
						alarmValue = tmpAlarm[1];
						if(rAlarm==null || !rAlarm.equals(alarm)) {
							saveFlag = true;
						}else{
							updateFlag=true;
						}
					}
					if(saveFlag) {
						QwertPointAlarm jstZcAlarm = new QwertPointAlarm();
						jstZcAlarm.setDevNo(devNo);
						jstZcAlarm.setDevName(devName);
						jstZcAlarm.setDevPos(devPos);
						jstZcAlarm.setCatNo(catNo);
						jstZcAlarm.setTargetNo(alarmNo);
						jstZcAlarm.setAlarmValue(alarmValue);
						jstZcAlarm.setSendTime(new Date());
						jstZcAlarm.setSendType("2");
						jstZcAlarmService.saveSys(jstZcAlarm);
					}
					if(updateFlag){
					/*	List<JstZcAlarm> jzaList = jstZcAlarmService.queryJzaList("2");
						for(int ai=0;ai<jzaList.size();ai++) {
							JstZcAlarm jza = jzaList.get(ai);
							if(jza.getDevNo()!=null&&jza.getTargetNo()!=null) {
								if (jza.getDevNo().equals(devNo) && jza.getTargetNo().equals(alarmNo)) {
									jza.setSendTime(new Date());
									jstZcAlarmService.updateSys(jza);
								}
							}
							break;
						}*/
						QueryWrapper<QwertPointAlarm> qw = new QueryWrapper<QwertPointAlarm>();
						qw.eq("target_no", alarmNo);
						qw.eq("dev_no",devNo);
						qw.orderByDesc("send_time");
						QwertPointAlarm jza = jstZcAlarmService.getOne(qw);
						if(jza!=null){
							jza.setSendTime(new Date());
							jstZcAlarmService.updateSys(jza);
						}
					}
				}
				handleDbMq(resList, devNo, alarm);
			}
			if(JstConstant.debugflag==1) {
				end = System.currentTimeMillis();
				System.out.println(devName + " 用时:" + (end - start) + "(ms)");
			}
		}
		if(JstConstant.debugflag==1) {
			nend = System.currentTimeMillis();
			System.out.println(devNos+" 开始时间:" + nstart + "; 结束时间:" + nend + "; 用时:" + (nend - nstart) + "(ms)");
		}
	}

	private String handlekStar(List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster("TCP",jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		int noConn=0;
		for (int i = 0; i < jztList.size(); i++) {
			QwertPointTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				if(retmessage!=null) {
					String rm = QudongUtils.getKstarString(retmessage, rm2);

					tmpAlarm = getAlarm(jzt, devNo, rm);
					if (tmpAlarm != null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
					}
					resList.add(rm1 + "=" + rm);
				}
				continue;
			}
			try {
				ReadKstarRequest request = new ReadKstarRequest(slaveId, 81,49);
				ReadKstarResponse response = (ReadKstarResponse) master.send(request);
				if(response!=null){
					if (response.isException())
						System.out.println("Exception response: message=" + response.getExceptionMessage());
					else{
						if(noConn>0){
							noConn--;
						}
						System.out.println(response.getMessage());
						retmessage =  response.getMessage();
						String rm1 = jzt.getTargetNo();
						String rm2 = jzt.getAddress();
						String rm = QudongUtils.getKstarString(retmessage, rm2);
						tmpAlarm=getAlarm(jzt, devNo, rm);
						if(tmpAlarm!=null) {
							String[] ta = tmpAlarm.split(":::");
							alarmNo += ta[0];
							alarmValue += ta[1];
						}
						resList.add(rm1+"="+rm);
					}
				}else{
					noConn++;
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		String retAlarm=null;
		if(noConn>0){
			retAlarm="connfail";
		}
		if(alarmNo!=null&&alarmValue!=null){
			retAlarm=alarmNo+"::"+alarmValue;
		}
		return retAlarm;
	}

	private String handlePmbus(List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster("TCP",jsonConInfo);
		String tmpInstruct=null;
		byte[] retmessage = null;
		String alarmNo=null;
		String alarmValue=null;
		int noConn=0;
		for (int i = 0; i < jztList.size(); i++) {
			QwertPointTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				if(retmessage!=null) {
					String rm = QudongUtils.getPmBus(retmessage, rm2);
					tmpAlarm = getAlarm(jzt, devNo, rm);
					if (tmpAlarm != null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
					}
					resList.add(rm1 + "=" + rm);
				}
				continue;
			}
			String[] tmp = instruct.split("/");
			try {
				ReadDianzongRequest request = new ReadDianzongRequest(2.0f,slaveId, Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2]));
				ReadDianzongResponse response = (ReadDianzongResponse) master.send(request);
				if(response!=null) {
					if (response.isException())
						System.out.println("Exception response: message=" + response.getExceptionMessage());
					else{
						noConn--;
						System.out.println(Arrays.toString(response.getShortData()));
						//			resList.add(Arrays.toString(response.getShortData()));
						retmessage =  response.getRetData();
						String rm1 = jzt.getTargetNo();
						String rm2 = jzt.getAddress();
						String rm=null;
				//		String rm = QudongUtils.getPmBus(retmessage, rm2);

						if(rm2.indexOf("_")!=-1){
							short[] rp = response.getShortData();
						//	String[] rm3 = rm2.split("_");
						//	int rm4 = Integer.parseInt(rm3[0]) / 2;
							rm=rp[7]+"";
						}
						if(rm2.indexOf("$")!=-1){
							String[] rm3=rm2.split("\\$");
							rm=retmessage[Integer.parseInt(rm3[0])-1]+"";
						}
						tmpAlarm=getAlarm(jzt, devNo, rm);
						if(tmpAlarm!=null) {
							String[] ta = tmpAlarm.split(":::");
							alarmNo += ta[0];
							alarmValue += ta[1];
						}
						resList.add(rm1+"="+rm);
					}
				}else{
					noConn++;
				}

			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		String retAlarm=null;
		if(noConn>0){
			retAlarm="connfail";
		}
		if(alarmNo!=null&&alarmValue!=null){
			retAlarm=alarmNo+"::"+alarmValue;
		}
		return retAlarm;
	}


	private String handleDelta(List<QwertPointTarget> jztList, List resList, JSONObject jsonConInfo) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"));
		QwertMaster master = QudongUtils.getQwertMaster("TCP",jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		int noConn=0;
		for (int i = 0; i < jztList.size(); i++) {
			QwertPointTarget jzt = jztList.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				if(retmessage!=null) {
					String rm = QudongUtils.getDeltaString(retmessage, rm2);
					tmpAlarm = getAlarm(jzt, devNo, rm);
					if (tmpAlarm != null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
					}
					resList.add(rm1 + "=" + rm);
				}
				continue;
			}
			try {
				ReadDeltaRequest request = new ReadDeltaRequest(slaveId, instruct);
				ReadDeltaResponse response = (ReadDeltaResponse) master.send(request);
				if(response!=null) {
					if (response.isException())
						System.out.println("Exception response: message=" + response.getExceptionMessage());
					else {
						//				System.out.println(response.getMessage());
						if(noConn>0){
							noConn--;
						}
						retmessage = response.getMessage();
						String rm1 = jzt.getTargetNo();
						String rm2 = jzt.getAddress();
						String rm = QudongUtils.getDeltaString(retmessage, rm2);
						tmpAlarm = getAlarm(jzt, devNo, rm);
						if (tmpAlarm != null) {
							String[] ta = tmpAlarm.split(":::");
							alarmNo += ta[0];
							alarmValue += ta[1];
						}
						resList.add(rm1 + "=" + rm);
					}
				}else{
					noConn++;
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
			}
			tmpInstruct = jzt.getInstruct();
		}
		String retAlarm=null;
		if(noConn>0){
			retAlarm="connfail";
		}
		if(alarmNo!=null&&alarmValue!=null){
			retAlarm=alarmNo+"::"+alarmValue;
		}
		return retAlarm;
	}

	private String handleM7000d(JSONObject jsonConInfo, List resList, List<QwertPointTarget> jztCollect) {
		int slaveId = Integer.parseInt(jsonConInfo.getString("slave"),16);
		QwertMaster master = QudongUtils.getQwertMaster("TCP",jsonConInfo);
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		int noConn=0;
		for (int i = 0; i < jztCollect.size(); i++) {
			QwertPointTarget jzt = jztCollect.get(i);
			String instruct = jzt.getInstruct();
			String devNo=jzt.getDevNo();
			String tmpAlarm=null;
			if(instruct.equals(tmpInstruct)){
				String rm1 = jzt.getTargetNo();
				String rm2 = jzt.getAddress();
				if(retmessage!=null) {
					String rm = QudongUtils.getM7000DString(retmessage, rm2);
					tmpAlarm = getAlarm(jzt, devNo, rm);
					if (tmpAlarm != null) {
						String[] ta = tmpAlarm.split(":::");
						alarmNo += ta[0];
						alarmValue += ta[1];
					}
					resList.add(rm1 + "=" + rm);
				}
				continue;
			}
			try {
				ReadM7000Request request = new ReadM7000Request(slaveId, 6);
				ReadM7000Response response = (ReadM7000Response) master.send(request);
				if(response!=null) {
					if (response.isException())
						System.out.println("Exception response: message=" + response.getExceptionMessage());
					else{
						if(noConn>0){
							noConn--;
						}
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
						tmpAlarm=getAlarm(jzt, devNo, rm);
						if(tmpAlarm!=null){
							String[] ta = tmpAlarm.split(":::");
							alarmNo+=ta[0];
							alarmValue+=ta[1];
						}
						resList.add(rm1+"="+rm);
					}
				}else{
					noConn++;
				}
			}
			catch (QudongTransportException e) {
				e.printStackTrace();
				return null;
			}
			tmpInstruct = jzt.getInstruct();
		}
		String retAlarm=null;
		if(noConn>0){
			retAlarm="connfail";
		}
		if(alarmNo!=null&&alarmValue!=null){
			retAlarm=alarmNo+"::"+alarmValue;
		}
		return retAlarm;
	}

	private String getAlarm(QwertPointTarget jzt, String devNo, String rm) {
		String tmpAlarm=null;
		String rkey = devNo + "::" + jzt.getTargetNo();
		String alarmConfig=jzt.getAlarmConfig();
		JSONObject ac = JSON.parseObject(alarmConfig);
		String evt = ac.getString("evt01");
//		String evt = jzt.getEvt01();
		String yinzi = jzt.getYinzi();
		String rvalue = rm;
		Object keyValue = redisUtil.get(rkey);
		if(evt!=null) {
			if (keyValue == null || !keyValue.toString().equals(rvalue)) {
				redisUtil.set(rkey, rvalue);
				if(keyValue!=null) {
					String tmpAlarmNo = jzt.getId() + ",";
					String message = evt;
					if (rvalue.equals("0")) {
		//				message = jzt.getEvt10();
						message=ac.getString("evt10");
					}
					String tmpAlarmValue = jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
					tmpAlarm=tmpAlarmNo+":::"+tmpAlarmValue;
				}
			}
		}else{
			if(rvalue!=null&&!rvalue.equals("")) {
				redisUtil.set(rkey, rvalue);
				if(keyValue!=null){
			//		System.out.println("rvalue::"+rvalue);
					float rv = 0.f;
					if(yinzi!=null&&!yinzi.equals("")) {
						rv=Float.parseFloat((String) rvalue)/Float.parseFloat(yinzi);
					}else {
						rv=Float.parseFloat((String) rvalue);
					}
			//		if(rv>jzt.getValMax()){
					if(rv>Integer.parseInt(ac.getString("val_max"))){
						String tmpAlarmNo = jzt.getId() + ",";
			//			String message = String.format(jzt.getHighInfo(),rvalue);
						String message=jzt.getTargetName()+"过高,等于"+rvalue;
						String tmpAlarmValue = message + "-" + keyValue + "to" + rvalue + ",";
						tmpAlarm=tmpAlarmNo+":::"+tmpAlarmValue;
					}
			//		if(rv<jzt.getValMin()){
					if(rv<Integer.parseInt(ac.getString("val_min"))){
						String tmpAlarmNo = jzt.getId() + ",";
		//				String message = String.format(jzt.getLowInfo(),rvalue);
						String message=jzt.getTargetName()+"过低,等于"+rvalue;
						String tmpAlarmValue = message + "-" + keyValue + "to" + rvalue + ",";
						tmpAlarm=tmpAlarmNo+":::"+tmpAlarmValue;
					}
				}
			}
		}
		return tmpAlarm;
	}


	private String handleSnmp(String type, List resList, String devNo, String devName, String catNo, JSONObject jsonConInfo, List<QwertPointTarget> jztCollect)  {
		String ipAddress = jsonConInfo.getString("ipAddress");
		String version = jsonConInfo.getString("version");
		String timeOut = jsonConInfo.getString("timeOut");
		String community = jsonConInfo.getString("community");
		if(community==null||community.equals("")) {
			community="public";
		}
		jztCollect.stream().sorted(Comparator.comparing(QwertPointTarget::getInstruct));
		List<String> oidList = new ArrayList<String>();
		String tmpInstruct=null;
		String retmessage=null;
		String alarmNo=null;
		String alarmValue=null;
		for (int j = 0; j < jztCollect.size(); j++) {
			QwertPointTarget jzt = jztCollect.get(j);
			String oidval = jzt.getInstruct();
			String rm1 = jzt.getTargetNo();
			String rm2 = jzt.getAddress();
			String tmpAlarm=null;

			if(oidval.equals(tmpInstruct)){
				if(rm2!=null && jzt.getInfoType().equals("digital")){
					String rm = snmpString(retmessage, rm2);
					tmpAlarm=getAlarm(jzt, devNo, rm);
					if(tmpAlarm!=null){
						String[] ta = tmpAlarm.split(":::");
						alarmNo+=ta[0];
						alarmValue+=ta[1];
					}
					resList.add(rm1+"="+rm);
				};
				continue;
			}
			List snmpList = null ;
			snmpList = SnmpData.snmpGet(ipAddress, community, oidval,null);
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
						tmpAlarm=getAlarm(jzt, devNo, rm);
						if(tmpAlarm!=null){
							String[] ta = tmpAlarm.split(":::");
							alarmNo+=ta[0];
							alarmValue+=ta[1];
						}
						resList.add(rm1+"="+rm);
					}
				}

				if(catNo.equals("MicroHtm")) {
					String tmpSnmp=(String) snmpList.get(0);
					if(tmpSnmp!=null) {
					//	tmpSnmp=tmpSnmp.replaceAll(" ", "");
						String[] tmps = tmpSnmp.split("=");
						String t1 = tmps[1].replaceAll(" ", "");
	//					if(t1.equals("Null")) {
	//						System.out.println("Null");
	//					}
						if(t1!=null&&!t1.equals("Null")) {
							if(Float.parseFloat(t1)>35||Float.parseFloat(t1)<10) {
								List<QwertPointAlarm> jzaList = jstZcAlarmService.queryJzaList("2");
								int dealflag=0; //初始状态
								for(int ai=0;ai<jzaList.size();ai++) {
									QwertPointAlarm jza = jzaList.get(ai);
									if(jza.getDevNo().equals(devNo)&&jza.getTargetNo().equals(jzt.getTargetNo())) {
										if(jza.getDealType()=="1") {  //已处理
											QwertPointAlarm jstZcAlarm = new QwertPointAlarm();
											jstZcAlarm.setDevNo(devNo);
											jstZcAlarm.setDevName(devName);
											jstZcAlarm.setCatNo(catNo);
											jstZcAlarm.setTargetNo(jzt.getTargetNo());
											jstZcAlarm.setAlarmValue(jzt.getTargetName());
											jstZcAlarm.setSendTime(new Date());
											jstZcAlarm.setSendType("2");
											jstZcAlarmService.saveSys(jstZcAlarm);
											dealflag=2; //已处理
									//		break;
										}else {
											dealflag=1; //未处理
											jza.setSendTime(new Date());
											jstZcAlarmService.updateSys(jza);
										}
										break;
									}
								}
								if(dealflag==0 || dealflag==2) {
									QwertPointAlarm jstZcAlarm = new QwertPointAlarm();
									jstZcAlarm.setDevNo(devNo);
									jstZcAlarm.setDevName(devName);
									jstZcAlarm.setCatNo(catNo);
									jstZcAlarm.setTargetNo(jzt.getTargetNo());
									jstZcAlarm.setAlarmValue(jzt.getTargetName());
									jstZcAlarm.setSendTime(new Date());
									jstZcAlarm.setSendType("2");
									jstZcAlarmService.saveSys(jstZcAlarm);
								}
							}
						}
					}
//								System.out.println(snmpList.get(n));
				}
			}
			tmpInstruct=jzt.getInstruct();
		}
		String retAlarm=null;
		if(alarmNo!=null&&alarmValue!=null){
			retAlarm=alarmNo+"::"+alarmValue;
		}
		return retAlarm;
	}
	
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

	private void handleDbMq(List resList, String devNo,String alarm) {
		String resValue = org.apache.commons.lang.StringUtils.join(resList.toArray(),";");
		if(alarm!=null){
			if(alarm.equals("connfail")){
				redisUtil.set(devNo+"-conn", "interupt");
			}else{
				redisUtil.set(devNo+"-conn", "connect");
				redisUtil.set(devNo+"-alarm",alarm);
			}
		}else{
			redisUtil.set(devNo+"-conn", "connect");
		}
		redisUtil.expire(devNo+"-conn", 7200);

		redisUtil.set(devNo,resValue);
		redisUtil.set(devNo+"::"+ DateUtils.formatTime(),resValue);
		redisUtil.expire(devNo+"::"+ DateUtils.formatTime(), 7200);
	}

	private String handleModbus(String proType, List resList, String devNo,
                                String devName, String catNo, JSONObject jsonConInfo, int sleeptime, List<QwertPointTarget> jztCollect)  {
		String alarmResult=null;
		BatchResults<String> results;
		String slave = jsonConInfo.getString("slave");
		String packageBit = jsonConInfo.getString("packageBit");
		String bitNumber = jsonConInfo.getString("bitNumber");
		int pb=64;
		if(packageBit!=null&&!packageBit.equals("")) {
			pb=Integer.parseInt(packageBit);
		}
		int bn=10;
		if(bitNumber!=null&&!bitNumber.equals("")) {
			bn=Integer.parseInt(bitNumber);
		}
		
		String timeOut = jsonConInfo.getString("timeOut");
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
		boolean flag = false;
		try {
			master.init();
			int slaveId = 0;
			slaveId = Integer.parseInt(slave,16);
			BatchRead<String> batch = new BatchRead<String>();
			String targetNos="";
			String tmpInstruct = null;
			int pointNumber = 0;
			int tmp2Offset=0;
			boolean batchSend = false;
			if(jztCollect.size()>0) {
				boolean alarmFlag = false;
				for (int j = 0; j < jztCollect.size(); j++) {

					QwertPointTarget jzt = jztCollect.get(j);
					String di = jzt.getInstruct().substring(0, 2);

			//		int	offset = Integer.parseInt(jzt.getAddress());
					int offset=0;
					String ta = jzt.getAddress();
					String[] tas=null;
					if(ta!=null&&ta.indexOf(".")!=-1){
						tas = ta.split("\\.");
						ta=tas[0];
					}
					if(jzt.getAddressType()!=null && jzt.getAddressType().equals("HEX")){
			//			System.out.println(jzt.getDevNo()+"::"+jzt.getTargetNo());
						if(ta.length()>4) {
							ta=ta.substring(0,4);
						}
						offset = Integer.parseInt(ta,16);
					}else{
						offset = Integer.parseInt(ta);
					}

					if (pointNumber>0 && offset==tmp2Offset) {
						continue;
					}
					if (pointNumber>0 && (pointNumber > pb || (offset - tmp2Offset > bn))) {
						flag = true;
					}
					
					tmp2Offset=offset;
					pointNumber++;
					if (flag == true) {
						
						results = master.send(batch);
						Thread.sleep(sleeptime);
						if(results.toString().equals("{}")) {
							alarmResult="connfail";
							if(JstConstant.debugflag==1) {
								System.out.println("{}:"+targetNos);
							}
						}else {
							resList.add(results.toString());
						}
						if(JstConstant.debugflag==1) {
							System.out.println(devNo+"::"+results);
						}
						batch = new BatchRead<String>();
						targetNos="";
						flag = false;
						pointNumber = 0;
					}

					String res = null;
					Map<String, String> resMap = new HashMap<String, String>();
					String dataType = "2";
					if(jzt.getDataType()!=null){
						dataType=jzt.getDataType();
					}

					if (di.equals("04")) {
						targetNos=targetNos+jzt.getId()+",";
//						batch.addLocator(jzt.getId(), BaseLocator.inputRegister(slaveId, offset,
//								Integer.parseInt(dataType)));
//						batchSend = true;
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
						targetNos=targetNos+jzt.getId()+",";
						batch.addLocator(jzt.getId(), BaseLocator.holdingRegister(slaveId, offset,
								Integer.parseInt(dataType)));
						batchSend = true;
					}
					if (di.equals("02")) {
						batch.addLocator(jzt.getId(), BaseLocator.inputStatus(slaveId, offset));
						batchSend = true;
					}
					Thread.sleep(sleeptime/2);
				}
				if (batchSend == true && alarmFlag==false) {
					results = master.send(batch);
					Thread.sleep(sleeptime);

					if(results.toString().equals("{}")) {
						alarmResult="connfail";

						if(JstConstant.debugflag==1) {
							System.out.println("{}:"+targetNos);
						}
					}else {
						if(JstConstant.debugflag==1) {
							System.out.println(devNo+"::"+results);
						}
						resList.add(results.toString());
					}
					if(JstConstant.debugflag==1) {
						System.out.println(devNo+"::"+resList.size());
					}
				}
			//	resList.add("{gf10001x0010=12345}");

				String alarm=null;
				if(resList.size()>0) {
					alarm=trackAlarm(devNo,resList, jztCollect);
					alarmResult=alarm;
				}
				Thread.sleep(sleeptime/2);
		    }
		} catch (ModbusInitException e) {
			e.printStackTrace();
		} catch (ModbusTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e)	            {
			e.printStackTrace();
		} finally {
			master.destroy();
		}
//		handleDbMq(type, resList, devNo);
		return alarmResult;
	}

	private String trackAlarm(String devNo, List resList, List<QwertPointTarget> jztCollect) {
		String alarmValue="";
		String alarmNo="";

		for(int ri=0;ri<resList.size();ri++) {
			String r1=(String) resList.get(ri);
			r1=r1.replaceAll(" ", "");
			r1=r1.substring(1, r1.length()-1);
			String[] r2 = r1.split(",");
			for(int rj=0;rj<r2.length;rj++) {
				String[] r3 = r2[rj].split("=");
				boolean jztfind = false;
				QwertPointTarget jzt = qwertPointTargetService.getById(r3[0]);
				String alarmConfig=jzt.getAlarmConfig();
				JSONObject ac = JSON.parseObject(alarmConfig);
				String evt = ac.getString("evt01");

				if(jzt.getAlarmPoint().equals("1")) {
					jztfind=true;
				}
				if(jzt.getAddress().indexOf('.')!=-1){
					jztfind=true;
				}
				String rkey=null;
				String rvalue=null;
				if(!jztfind){
					rkey = devNo + "::" + jzt.getTargetNo();
					rvalue=r3[1];
					Object keyValue = redisUtil.get(rkey);
					if(keyValue==null || !keyValue.toString().equals(rvalue)){
						redisUtil.set(rkey, rvalue);
					}
				}else {
					if(jzt.getInfoType().equals("digital")){
						if(jzt.getAddress().indexOf(".")!=-1) {
				//			String tmpinstruct=jzt.getInstruct();
							String tmpaddress=jzt.getAddress();
							List<QwertPointTarget> jztc=null;
		//					if(tmpaddress.indexOf('.')!=-1){
								String[] tas = tmpaddress.split("\\.");
								String ta = tas[0];
								jztc = jztCollect.stream().filter(u -> ta.equals((u.getAddress().split("\\."))[0])).collect(Collectors.toList());
	//						}else{
	//							String ta = jzt.getAddress();
	//							jztc = jztCollect.stream().filter(u -> ta.equals(u.getAddress())).collect(Collectors.toList());
	//						}

							for(int n=0;n<jztc.size();n++){
								QwertPointTarget item = jztc.get(n);
								if(item.getAlarmPoint()==null||item.getAlarmPoint().equals("")||item.getAlarmPoint().equals("0")) {
									continue;
								}
								if(!item.getAlarmPoint().equals("1")) {
									continue;
								}
								String str1=r3[1];
								if(str1.equals("true")) {
									str1="1";
								}
								if(str1.equals("false")) {
									str1="0";
								}

								String binaryStr = Integer.toBinaryString(Integer.parseInt(str1));
								while(binaryStr.length() < 16){
									binaryStr = "0"+binaryStr;
								}
								String a6=null;
								String a1=item.getAddress();
								String[] a2 = a1.split("\\.");
								int a4 = Integer.parseInt(a2[1]);
								int a5 = 15 - a4;
								a6 = binaryStr.substring(a5, a5 + 1);
								rkey = devNo + "::" + item.getTargetNo();
								if(evt!=null) {
									rvalue = a6;
									Object keyValue = redisUtil.get(rkey);
									if (keyValue == null || !keyValue.toString().equals(rvalue)) {
										redisUtil.set(rkey, rvalue);
										alarmNo += jzt.getId() + ",";
										String message = evt;
										if (rvalue.equals("0")) {
									//		message = jzt.getEvt10();
											message=ac.getString("evt10");
										}
										alarmValue += jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
									}
								}
							}
						}
						else {
							rkey = devNo + "::" + jzt.getTargetNo();
						//	String evt = jzt.getEvt01();
							if(evt!=null) {
								rvalue = r3[1];
								Object keyValue = redisUtil.get(rkey);
								if (keyValue == null || !keyValue.toString().equals(rvalue)) {
									redisUtil.set(rkey, rvalue);
									alarmNo += jzt.getId() + ",";
									String message = evt;
									if (rvalue.equals("0")) {
							//			message = jzt.getEvt10();
										message=ac.getString("evt10");
									}
									alarmValue += jzt.getTargetName() + "-" + message + "-" + keyValue + "to" + rvalue + ",";
								}
							}
						}
					}else {
						rkey = devNo + "::" + jzt.getTargetNo();
						rvalue=r3[1];
						String yinzi=jzt.getYinzi();
						float r4=0f;
						if(rvalue.contains(".")) {
							int indexOf = rvalue.indexOf(".");
							rvalue = rvalue.substring(0, indexOf);
						}
						if(yinzi!=null) {
							r4=Integer.parseInt(rvalue)/Integer.parseInt(yinzi);
						}else {
							r4=Integer.parseInt(rvalue);
						}
						if(ac.getString("val_max")==null||ac.getString("val_max").equals("")){
							return null;
						}
					//	if(jzt.getValMax()!=null && jzt.getValMax()>0) {
						if(ac.getString("val_max")!=null && Integer.parseInt(ac.getString("val_max"))>0) {
							Object keyValue = redisUtil.get(rkey);
							String flag = null;
							String message=null;
							int r5=0;
							if (r4 >= Integer.parseInt(ac.getString("val_max"))) {
								flag = "1";
								message=jzt.getTargetName()+"过高,等于"+r4;
			//					redisUtil.set(rkey, flag);
							}
							if (r4 <= Integer.parseInt(ac.getString("val_min"))) {
								flag = "-1";
								message=jzt.getTargetName()+"过低,等于"+r4;
			//					redisUtil.set(rkey, flag);
							}
							if (r4 > Integer.parseInt(ac.getString("val_min")) && r4 < Integer.parseInt(ac.getString("val_max"))) {
								flag = "0";
			//					redisUtil.set(rkey, flag);
							}
							if (flag!=null && (keyValue == null || !flag.equals(keyValue))) {
								redisUtil.set(rkey, flag);
								if(message!=null) {
									alarmNo += jzt.getId() + ",";
		//							alarmValue += jzt.getTargetName() + "-" + message + "-报警值-" + r4 + "-" + keyValue + "to" + flag + ",";
									alarmValue += message + "-报警值-" + r4 + "-" + keyValue + "to" + flag + ",";
								}
							}
						}
					}
				}
			}
		}
		String alarm=null;
		if(alarmValue.length()>0) {
			alarm=alarmNo+"::"+alarmValue;
		}
		return alarm;
	}
}
