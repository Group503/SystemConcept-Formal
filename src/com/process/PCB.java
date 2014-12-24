package com.process;

import java.util.Arrays;


/**
 *  
 * 进程的PCB
 * 
 * @author Au_java
 * 
 * 标识符 ID
 * 状态 五态 -- 新建，就绪，运行，阻塞，完成 
 * 优先级、程序计数器、内存指针、上下文数据、I/O状态信息、记账信息
 * 
 */

public class PCB {
	public static int staticID = 1;
	private int id;//标识符
	private int status;//状态
	private long arriveTime;//进程到达时间, 同时为 pcb 的ID
	private long serivceTime;//进程所需的时间片
	private long leafTime;//进程运行完所需的时间片
	private int menorySize; // 内存占用
	private String blockReason;//进程在继续执行前等待的事件标识
	private boolean isApplyResource;  // 是否申请资源
	private int[] applyResource;  // 原始申请资源数组
	private int[] usingResoutce;  // 已占用资源数组
	private int[] reg;		// 寄存器组
	private String[] codes; // 代码
	private int codeIndex;  // 下一条执行的代码
	public final static int STATUS_NEW = 1;
	public final static int STATUS_READY = 2;
	public final static int STATUS_RUNNING = 3;
	public final static int STATUS_BLOCK = 4;
	public final static int STATUS_DONE = 5;
	
	public final static int WANDER_PCB_ID = -1;
	
	private boolean isOnceApply = true;
	
	
	public boolean isOnceApply() {
		return isOnceApply;
	}

	public void setOnceApply(boolean isOnceApply) {
		this.isOnceApply = isOnceApply;
	}

	PCB(String code){
		long tmp = System.currentTimeMillis();
		this.id = PCB.staticID++;
		this.status = PCB.STATUS_READY;
		this.arriveTime = tmp;
		this.reg = new int[]{0,0,0,0};
		this.applyResource = new int[]{0,0,0};
		this.usingResoutce = new int[]{0,0,0};
		this.codes = code.split(";");
		this.serivceTime = this.codes.length;
		this.leafTime = this.serivceTime; 
		this.menorySize = (int) (this.codes.length * ((Math.random() * 5)+1));
		this.blockReason = "";
		this.isApplyResource = false;
		this.codeIndex = 0;
	}
	
	PCB(boolean isWanderPCB){
		//long tmp = System.currentTimeMillis();
		if(isWanderPCB){
			this.id = PCB.WANDER_PCB_ID;
			this.status = PCB.STATUS_READY;
			this.serivceTime = -1;
			this.leafTime = -1; 
			this.blockReason = "";
			this.isApplyResource = false;
		}
	}
	
	PCB( int[] applyResource,String code){
		this(code);
		
		if(applyResource[0]==0&&applyResource[1]==0&&applyResource[2]==0){
			isApplyResource = false;
		}else{
			this.isApplyResource = true;
		}
		this.applyResource = applyResource;
	}
	
	public String getBlockReason() {
		return blockReason;
	}

	public void setBlockReason(String blockReason) {
		this.blockReason = blockReason;
	}

	public boolean isApplyResource() {
		return isApplyResource;
	}

	public void setApplyResource(boolean isApplyResource) {
		this.isApplyResource = isApplyResource;
	}

	public int[] getApplyResource() {
		return applyResource;
	}

	public void setApplyResource(int[] applyResource) {
		this.applyResource = applyResource;
	}

	public int[] getUsingResoutce() {
		return usingResoutce;
	}

	public void setUsingResoutce(int[] usingResoutce) {
		this.usingResoutce = usingResoutce;
	}



	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getSerivceTime() {
		return serivceTime;
	}

	public void setSerivceTime(long serivceTime) {
		this.serivceTime = serivceTime;
	}
	
	

	public long getLeafTime() {
		return leafTime;
	}

	public void setLeafTime(long leafTime) {
		this.leafTime = leafTime;
	}

	


	public int getMenorySize() {
		return menorySize;
	}

	public void setMenorySize(int menorySize) {
		this.menorySize = menorySize;
	}

	public String[] getCodes() {
		return codes;
	}

	public void setCodes(String[] codes) {
		this.codes = codes;
	}

	public int getCodeIndex() {
		return codeIndex;
	}

	public void setCodeIndex(int codeIndex) {
		this.codeIndex = codeIndex;
	}
	
	

	public int[] getReg() {
		return reg;
	}

	public void setReg(int[] reg) {
		this.reg = reg;
	}

	public long getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(long arriveTime) {
		this.arriveTime = arriveTime;
	}

	@Override
	public String toString() {
		return "PCB [id=" + id + ", serivceTime=" + serivceTime + ", leafTime="
				+ leafTime + ", reg=" + Arrays.toString(reg) + ", codeIndex="
				+ codeIndex + "]\n";
	}

	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof PCB){
			PCB tmp = (PCB)arg0;
			if(this.id == tmp.getId()){
				return true;
			}
		}
		return false;
	}
	
	

}
