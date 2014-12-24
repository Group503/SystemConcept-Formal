package com.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.filesystem.InputFileFrame.InputFileCallBack;

class Disk {

	public static final int DISKLENGTH = 128 * 64; // bytes

	byte[] bytes = new byte[DISKLENGTH]; // 8192Byte //磁盘数据
	File diskFile = new File("disk.dat"); // 模拟磁盘的文件
	
	
	public Disk() throws IOException {

		// System.out.print(diskFile.exists());
		if (!diskFile.exists()) { // disk.dat不存在

			for (int i = 0; i < 128; i++) {
				if (i > 2) {
					bytes[i] = 0; // 未使用
				} else {
					bytes[i] = -1; // 已使用
				}

			}
			// 初始化根目录目录项
			// setDirectoryItem(128, "$C:nn$" , (byte) 0,
			// (byte)-1);//起始盘号-1表示没有包含盘块
			addDirectoryItem(128, "$C:nn$", (byte) 0, (byte) -1);
			//addDirectoryItem(192, "$ttnn$", (byte) 1, (byte) 4);
			//addDirectoryItem(200, "konnn$", (byte) 0, (byte) -1);
			//addDirectoryItem(256, "aaaee$", (byte) 0, (byte) -1);// 文件

			addDirectoryItem(136, "$D:nn$", (byte) 0, (byte) -1);
			// /*test*/setDirectoryItem(startIndex, content, fileLength,
			// startBlock);

			addDirectoryItem(144, "$E:nn$", (byte) 0, (byte) -1);
			// 初始化结束

		} else { // disk.dat存在 读取到bytes[]
			FileInputStream inputStream = new FileInputStream(diskFile);
			inputStream.read(bytes);
			inputStream.close();

		}
			
	}

	// 新建目录项
	private void addDirectoryItem(int startIndex, String content,
			byte fileLength, byte startBlock) throws IOException {

		/*
		 * 文件名：3B （'$'为占位符） 
		 * 扩展名：1B （n：无 ， e：可执行文件） 
		 * 属性：1B （n：目录 ， e：文件） 
		 * 文件长度：2B（'$'为占位符） 
		 * 起始盘块号：1B （start）
		 */
		if (content.length() != 6) {
			System.out.println("the content.length must be 6");
			return ;
		}
		for (int i = 0; i < 6; i++) { // 设置目录项前6字节 (总共8字节)
			bytes[startIndex + i] = (byte) content.charAt(i);

		}
		bytes[startIndex + 6] = fileLength;
		bytes[startIndex + 7] = startBlock;
		if (startBlock > 2) { // 设置FAT表，占用
			bytes[startBlock] = -1;
			System.out.println("in addDirec, Block :"+startBlock+" = -1");
		}
		
		//写回磁盘
		FileOutputStream outputStream = new FileOutputStream(diskFile);
		outputStream.write(bytes);
		outputStream.close();
		
		if(usageCallback!=null){
			
			usageCallback.flushUsage();
		}
	}

	// 显示目录项
	public void getDirectoryItem(int startIndex) {
		String filename, extension, attribute, fileLength, startblock;
	
		filename = getFileName(startIndex, true, false);// 文件名
		extension = getExtension(startIndex);// 拓展名
		attribute = getAttribute(startIndex);// 属性
		fileLength = String.valueOf(getFileLength(startIndex));// 文件长度
		startblock = String.valueOf(getStartBlock(startIndex));// 起始盘号
		
		String[] args = {filename,extension,attribute,fileLength,startblock};
		
		ShowAttributeFrame showAttributeFrame = new ShowAttributeFrame(args);
		
		System.out.println("文件名" + filename);
		System.out.println("拓展名" + extension);
		System.out.println("属性" + attribute);
		System.out.println("文件长度" + fileLength);
		System.out.println("起始盘号" + startblock);
	}

	// 解析执行命令
	public boolean execute(String order) throws IOException {
		if (order.contains("$")) {
			JOptionPane.showMessageDialog(null , "The input has keyword '$'.");
			System.out.println("the order has'$'");
			return false;
		}
		String[] option;
		order = order.trim();
		option = order.split("\\s+");// 以若干个空格作为间隔
	
		
		if (option.length == 1) {//格式化磁盘、执行文件
			if(option[0].equalsIgnoreCase("format")){
				format();
				treeCallback.flushFileTree("我的电脑", -1);
				usageCallback.flushUsage();
				return true;
			}
			else if ( option[0].endsWith(".e") && isPath(option[0])) {
				int	index = getDirectoryItemAddress(option[0]);
				if(index != -1){
					//OutputFileFrame outputFileFrame = new OutputFileFrame(getFileContent(index)) ;
					exeContentCallback.executeFile(getFileContent(index));
					return true;
				}
			}
		} else if (option.length == 2) {// 创建文件、删除文件、显示文件、建立目录、 删除空目录
			if (option[0].equalsIgnoreCase("create")) {// 新创建的文件的父目录必须存在
				if (option[1].endsWith(".e") && isPath(option[1])) {// 合法路径输入
					int i = option[1].lastIndexOf('\\');
					String parentPath = option[1].substring(0, i);
					final int parentIndex = getDirectoryItemAddress(parentPath);
					if (parentIndex != -1
							&& getDirectoryItemAddress(option[1]) == -1) {
						// 父目录存在，新创建的文件不存在
						int fileLength = getFileLength(parentIndex);
						
						if (fileLength >= 8) {// 子目录项已满
							System.out.println("子目录项已满。");
							return false;
						}
						final String path = option[1];
						InputFileFrame inputFileFrame =  new InputFileFrame();
						inputFileFrame.registerInputFileCallback(new InputFileCallBack() {
							
							@Override
							public void returnInputFile(String str) {
								try {
									handleCreate(str , parentIndex ,path);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						return true;
					}

				}
			} else if (option[0].equalsIgnoreCase("delete")) {
				if (option[1].endsWith(".e") && isPath(option[1])) {// 合法路径输入
					int index = getDirectoryItemAddress(option[1]);
					System.out.println("nenene:" + index);
					if(index >= 192){//在系统盘块之外
						int i = option[1].lastIndexOf('\\');
						String parentPath = option[1].substring(0 , i);
						handleRmdirAndDelete(index , parentPath);//兄弟目录项前移，parent.fileLength--
						
						return true;
					}
				
				}

			} else if (option[0].equalsIgnoreCase("type")) {
				if (isPath(option[1])) { // 合法路径输入
					// getDirectoryItem(option[1]);
					int startIndex = getDirectoryItemAddress(option[1]);
					if (startIndex != -1) {
						getDirectoryItem(startIndex);
						
						return true;
					}
				}
			} else if (option[0].equalsIgnoreCase("mkdir")) {// 建立目录
				if (!option[1].endsWith(".e") && isPath(option[1])) {// 合法路径输入
					int i = option[1].lastIndexOf('\\');
					String parentPath = option[1].substring(0, i);//父目录路径
					int parentIndex = getDirectoryItemAddress(parentPath);//父目录目录项地址
					if (parentIndex != -1
							&& getDirectoryItemAddress(option[1]) == -1) {
						// 父目录存在，新创建的文件不存在
						int startBlock = getStartBlock(parentIndex);//父目录起始盘号
						int fileLength = getFileLength(parentIndex);//父目录下的文件长度

						if (fileLength >= 8) {// 子目录项已满
							System.out.println("子目录项已满。");
							return false;
						}

						if (startBlock == -1) {// 未占用物理块
							startBlock = findNextEmptyBlock();
						}
						
						updateDirectoryItem(parentIndex, fileLength + 1,
								startBlock);//更新父目录文件长度和起始盘块号
						String newFileName = option[1].substring(i+1);
						System.out.println("the new filename:" + newFileName);
						if (newFileName.length() < 3) {//需要添加占位符
							if(newFileName.length() == 1){
								newFileName = newFileName.concat("$$");
							}
							else if(newFileName.length() == 2){ 
								newFileName = newFileName.concat("$");
							}
						}
						addDirectoryItem(startBlock * 64 + 8 * fileLength,
								newFileName + "nn$", (byte) 0, (byte) -1);
						
						treeCallback.flushFileTree(parentPath ,getDirectoryItemAddress(parentPath));//通知刷新文件树
						usageCallback.flushUsage();
						
						return true;
					}

				}

			} else if (option[0].equalsIgnoreCase("rmdir")) {// 删除空目录
				if (!option[1].endsWith(".e") && isPath(option[1])) {//目录合法输入
					//System.out.println("nicaine:  "+option[1].endsWith(".e"));
					int startIndex = getDirectoryItemAddress(option[1]);
					System.out.println("enenen:"+startIndex);
					if (startIndex >= 192
							&& getFileLength(startIndex) == 0 ) {
						// 存在该空目录 && 不是c: d: e:
						int i = option[1].lastIndexOf('\\');
						String parentPath = option[1].substring(0, i);
						handleRmdirAndDelete(startIndex , parentPath);//兄弟目录项前移，parent.fileLength--
						
						return true;
					}		
					
				}		
			}
		} else if (option.length == 3) {  //复制文件
			if (option[0].equalsIgnoreCase("copy") && option[1].endsWith(".e")
					&& option[2].endsWith(".e") && isPath(option[1])
					&& isPath(option[2])) {
				int i = option[2].lastIndexOf('\\');
				String parentPath = option[2].substring(0, i);
				int parentIndex = getDirectoryItemAddress(parentPath);
				int srcIndex = getDirectoryItemAddress(option[1]);
				if (srcIndex != -1 && getDirectoryItemAddress(parentPath) != -1
						&& getDirectoryItemAddress(option[2]) == -1) {
					
					int fileLength = getFileLength(parentIndex);
					
					if (fileLength >= 8) {// 子目录项已满
						System.out.println("子目录项已满。");
						return false;
					}
					
					String content = getFileContent(srcIndex);
					handleCreate(content, parentIndex, option[2]);	
					
					return true;
				}
					
			}
		}
		
		JOptionPane.showMessageDialog(null , "The input is illegal.");
		return false;
	}
	
	private void format() throws IOException {
		for (int i = 0; i < 128; i++) {
			if (i > 2) {
				bytes[i] = 0; // 未使用
			} else {
				bytes[i] = -1; // 已使用
			}
		}
		
		addDirectoryItem(128, "$C:nn$", (byte) 0, (byte) -1);
		addDirectoryItem(136, "$D:nn$", (byte) 0, (byte) -1);
		addDirectoryItem(144, "$E:nn$", (byte) 0, (byte) -1);
		
		
	}

	private String getFileContent(int index) {
		
		int startBlock = getStartBlock(index);
		String content = "";
		while( startBlock > 2 ){
			for (int i = 0; i < 64; i++) {	
				content = content + String.valueOf( (char)bytes[startBlock * 64 + i] );
			}
			startBlock = bytes[startBlock];
		}
		System.out.println(content);
		return content;
	}

	private void handleCreate(String str, int parentIndex , String path) throws IOException {
		
		int blockLength = (str.length() +63 )/ 64 ;
		/*if(str.length() == 0){
			blockLength = 0 ;
		}*/
		System.out.println("in handleCreat  strlength : "+ str.length());
		System.out.println("in handleCreat  blockLength : "+blockLength);
	
		boolean isEnough = false;
		int count = 0;
		for(int i = 3 ; i < 128 ; i++){  //磁盘是否有足够空间
			if(bytes[i] == 0){
				count++;
				if( count > blockLength){
					isEnough = true;
				}
			}
		}
		
		if( !isEnough){
			System.out.println("the disk is not enough");
			return;
		}
		
		int startBlock = getStartBlock(parentIndex);//父目录起始盘号
		int fileLength = getFileLength(parentIndex);//父目录下的文件长度
		if (startBlock == -1) {// 未占用物理块
			startBlock = findNextEmptyBlock();
		}
		
		updateDirectoryItem(parentIndex, fileLength + 1,
				startBlock);//更新父目录文件长度和起始盘块号
		
		int i = path.lastIndexOf('\\');
		int j = path.lastIndexOf('.');
		String parentPath = path.substring(0, i);
		System.out.println("in handleCreate parentPath : "+ parentPath);
		String fileName = path.substring(i+1 , j);
		System.out.println("fileName : "+fileName);
		
		if (fileName.length() < 3) {//需要添加占位符
			if(fileName.length() == 1){
				fileName = fileName.concat("$$");
			}
			else if(fileName.length() == 2){ 
				fileName = fileName.concat("$");
			}
		}
		
		byte firstEmptyBlock ;
		if( blockLength == 0){
			firstEmptyBlock = -1;
		}
		else{
			firstEmptyBlock = (byte) findNextEmptyBlock();
		}
		 
		
		addDirectoryItem(startBlock * 64 + 8 * fileLength,
				fileName + "ee$", (byte) blockLength, firstEmptyBlock);
		
		// 对磁盘赋值
		int start = 0;
		int emptyBlock = firstEmptyBlock;
		for(i  = 0 ;  i< blockLength  ;i++){
			if( i != 0 ){ 
				emptyBlock = findNextEmptyBlock();
			}
			 System.out.println("emptyBlock  :"+emptyBlock);
			for(j = 0 ; j < 64 ; j++){
				if(start + j < str.length()){
					bytes[emptyBlock *64 +j] = (byte)str.charAt(start + j);
				}
				else {
					bytes[emptyBlock *64 +j] = (byte)' ';
				}
			}
			start += 64;
			bytes[emptyBlock] = -1;
			bytes[emptyBlock] = (byte)findNextEmptyBlock();//修改FAT表
			System.out.println("next block : "+bytes[emptyBlock]);
		
		}
		if( blockLength > 0){
			bytes[emptyBlock] = -1;//修改FAT表 ，链式最后一块
		}
			System.out.println("in handleCreate emptyblock :"+emptyBlock);
		
		// 写回磁盘
		FileOutputStream outputStream = new FileOutputStream(diskFile);
		outputStream.write(bytes);
		outputStream.close();
		
		treeCallback.flushFileTree(parentPath ,getDirectoryItemAddress(parentPath));//通知刷新文件树
		usageCallback.flushUsage();
	}

	//兄弟目录项前移，parent.fileLength--
	private void handleRmdirAndDelete( int startIndex , String parentPath ) throws IOException {
		int parentIndex = getParentAddress(startIndex);
		if( parentIndex == -1){
			System.out.println("parentIndex not found , in handle method");
			return;
		}
		int parentFileLength = getFileLength(parentIndex);
		
		//针对删除文件，设置关联FAT为空闲
		if( getStartBlock(startIndex) != -1 ){
			
			int block = getStartBlock(startIndex);
			System.out.println("bbbblock :: "+block);
			int next;
			while ( bytes[block] != -1 ) {//链式查询FAT表
					next = bytes[block];
					bytes[block] = 0;
					System.out.println("block :"+block+" set to 0.");
					block = next;
			}
			bytes[block] = 0;
			System.out.println("block :"+block+" set to 0.");
		}
		
		
		int count = (startIndex % 64) / 8;//第count项子目录
		int firstIndex = (startIndex / 64) *64;
		if( count  < parentFileLength - 1  ){//不是最后一项，把最后一项提前
			System.out.println("count : "+count+" parentFileLength : "+parentFileLength);
			System.out.println("firstIndex : "+firstIndex);
			for(int i  = 0 ; i < 8 ; i++){
				bytes[firstIndex + count * 8 + i] = bytes[firstIndex
						+ (parentFileLength - 1) * 8 + i];
				System.out.println((char)bytes[firstIndex
						+ (parentFileLength - 1) * 8 + i]);
			}
		}
		
		bytes[parentIndex + 6]--;//parent.fileLength--
		if (bytes[parentIndex + 6] <= 0) {//变为空目录，设置FAT表，取消物理块占用
			bytes[getStartBlock(parentIndex)] = 0;
			//int tem = getStartBlock(parentIndex);
			bytes[parentIndex + 7] = -1;
			
			//System.out.println("startblock :"+getStartBlock(parentIndex));
			//System.out.println("FAT :"+bytes[tem]);
		}
		
		//写回磁盘
		FileOutputStream outputStream = new FileOutputStream(diskFile);
		outputStream.write(bytes);
		outputStream.close();
		
		treeCallback.flushFileTree(parentPath , getDirectoryItemAddress(parentPath));//通知刷新文件树
		usageCallback.flushUsage();
	}
	
	
	//根据目录项地址找到父目录项地址
	private int getParentAddress( int childAddress ){
		if( childAddress < 192 ){
			System.out.println("the childAddress is forbidden.");
			return -1;
		}
			
		int  count = 3; //c: d: e: 3次遍历
		int parentAddress;
		int childBlock = childAddress / 64;//目录项地址所在盘块号
		int find = -1;
		for (int i = 0; i < count; i++) {
			parentAddress = 128 + i * 8;
			find = getParentAddress(parentAddress, childBlock);
			if( find > 0){
				break;
			}
		}
		
		System.out.println("find :"+find);
		return find;
		
	}
	
	//根据目录项地址所在盘块号找到父目录项地址
	private int  getParentAddress(int address , int childBlock) {
		System.out.println("address :"+address+" block :"+getStartBlock(address));
		if (getStartBlock(address) == childBlock) {
			return address;
		}	
			
		int fileLength  = getFileLength(address);
		if( fileLength == 0 || getAttribute(address).equals("e")){
			//空目录或者文件不再递归
			return -1;
		}
		
		int find = -1;
		int childAddress = getStartBlock(address) * 64;
		for (int i = 0; i < fileLength; i++) {
			find = getParentAddress(childAddress + i * 8, childBlock);
			if (find > 0) {
				break;
			}
		}
		return find;
	}
	
	
	//更新父目录文件长度和起始盘块号
	private void updateDirectoryItem(int startIndex, int fileLength, int startBlock) throws IOException {
		// TODO Auto-generated method stub
		bytes[startIndex + 6] = (byte) fileLength;
		bytes[startIndex + 7] = (byte) startBlock;
		//更新FAT分配表
		if( startBlock > 0){
			bytes[startBlock] = -1;
		}
		else {
			bytes[startBlock] = 0;
		}
		
		
		//写回磁盘
		FileOutputStream outputStream = new FileOutputStream(diskFile);
		outputStream.write(bytes);
		outputStream.close();
	}

	// 判断是否为合法路径名
	public boolean isPath(String path) {
		path = path.trim();
		// if (path.contains(" ")) {
		// return false;
		// }
		if (path.contains(".")) {
			if (path.indexOf('.') != path.lastIndexOf('.')
					|| path.indexOf('.') != path.length() - 2) {
				System.out.println("here0");
				return false;
			}
		}

		String[] subPath = path.split("\\\\");

		if (subPath.length < 1) {// 最短 C:或 C:\

			System.out.println("here1");
			// System.out.println(subPath.length);
			return false;
		}

		String plate = subPath[0].toLowerCase();
		if (!plate.equals("c:") && !plate.equals("d:") && !plate.equals("e:")) {
			System.out.println("here2");
			// System.out.println(plate);
			return false;
		}
		// System.out.println("subPath.length"+subPath.length);
		for (int i = 1; i < subPath.length; i++) { // 防止 c:\a\\b\ccc两种情况
			System.out.println("isPath judge" + i + "**" + subPath[i]);
			if (subPath[i].length() == 0 || subPath[i].length() > 3) {
				if (i == subPath.length - 1) { // c:\a\bbb.e 文件名可以大于三 最大长度为5
					if (subPath[i].length() <= 5
							&& subPath[i].toLowerCase().endsWith(".e")) {
						continue;
					}
				}
				System.out.println("here3");
				return false;
			}

		}

		return true;
	}

	// 根据合法路径查找目录项地址
	protected int getDirectoryItemAddress(String path) {
		// byte[] item = new byte[8];
		path = path.trim();
		String[] subPath = path.split("\\\\");
		String fileName;
		int startIndex = 128;
		int find;
		int itemCount = 3; // 初始为3，c: d: e:
		boolean hasFound;

		subPath[0] = subPath[0].toUpperCase(); // 合法路径开头一定是盘符

		for (find = 0; find < subPath.length; find++) {
			hasFound = false;
			for (int i = 0; i < itemCount; i++) {
				if (bytes[startIndex + 8 * i + 3] == 'e') {
					fileName = getFileName(startIndex + 8 * i, true, true);
				} else {
					fileName = getFileName(startIndex + 8 * i, true, false);
				}
				System.out.println(fileName + "**" + subPath[find]);
				if (fileName.equals(subPath[find])) { // 路径匹配
					if (find != subPath.length - 1) {// 不是路径最后一项
						if (bytes[startIndex + 8 * i + 7] != -1
								&& bytes[startIndex + 8 * i + 3] == 'n') {
							// 起始盘号不等于-1 && 是目录
							itemCount = bytes[startIndex + 8 * i + 6];
							startIndex = bytes[startIndex + 8 * i + 7] * 64;
							hasFound = true;
							break;
						}
					} else { // 路径最后一项
						startIndex += 8 * i;
						hasFound = true;
						break;
					}

				}

			}
			if (!hasFound) {
				break;
			}
		}
		if (find == subPath.length) {
			return startIndex;// 返回目录项地址
		} else {
			System.out.println("not found");
			return -1;
		}

	}

	private int findNextEmptyBlock() {//顺序查找FAT表中第一个空闲物理块
		for ( int i = 3; i < 128; i++) {
			if(bytes[i] == 0){  //-1表示占用 ，0表示空闲
				System.out.println("find emptyBlock = "+i);
				return i;
			}
		}
		System.out.println("there is no empty block.");
		return -1;//没找到 ，磁盘已满
	}

	/********************************************************************************/
	/** 以bytes[startIndex]起始的8字节作为目录项 ，返回文件名，拓展名，属性，文件长度，起始盘号.. **/
	private String getFileName(int startIndex, boolean removeKey,
			boolean encludeExtension) {// removeKey表示是否去除关键字'$'
		if (startIndex < 128) {
			System.out.println("error:startIndex < 128!");
			return null;
		}
		String fileName;
		char[] tem = { (char) bytes[startIndex], (char) bytes[startIndex + 1],
				(char) bytes[startIndex + 2] };
		fileName = String.valueOf(tem);// 文件名
		if (removeKey) {
			fileName = fileName.replaceAll("\\$", "");
			// System.out.println("here"+ fileName);
		}
		if (encludeExtension) {
			fileName = fileName.concat("."
					+ String.valueOf((char) bytes[startIndex + 3]));
		}
		return fileName;
	}

	private String getExtension(int startIndex) {
		if (startIndex < 128) {
			System.out.println("error:startIndex < 128!");
			return null;
		}
		String extension;
		extension = String.valueOf((char) bytes[startIndex + 3]);// 拓展名
		return extension;
	}

	private String getAttribute(int startIndex) {
		if (startIndex < 128) {
			System.out.println("error:startIndex < 128!");
			return null;
		}
		String attribute;
		attribute = String.valueOf((char) bytes[startIndex + 4]);// 属性
		return attribute;
	}

	private byte getFileLength(int startIndex) {
		if (startIndex < 128) {
			System.out.println("error:startIndex < 128!");
			return (Byte) null;
		}
		return bytes[startIndex + 6]; // 文件长度
	}

	private byte getStartBlock(int startIndex) {
		if (startIndex < 128) {
			System.out.println("error:startIndex < 128!");
			return (Byte) null;
		}
		return bytes[startIndex + 7]; // 起始盘号
	}
	
	/**************************回调通知刷新文件数的接口******************************/
	public interface FlushFileTreeCallback{
		public void flushFileTree(String nodePath , int index);
	}
	
	private	FlushFileTreeCallback treeCallback;
	
	public void registerFlushFileTreeCallback(FlushFileTreeCallback callback){
		this.treeCallback = callback;
	}
	
	/**************************回调通知刷新磁盘使用情况的接口******************************/
	public interface FlushUsageCallback{
		public void flushUsage();
	}
	
	private	FlushUsageCallback usageCallback;
	
	public void registerFlushUsageCallback(FlushUsageCallback callback){
		this.usageCallback = callback;
	}
	
	/**************************回调返回exe文件执行的内容******************************/
	interface ExeContentCallback{
		public void executeFile(String content);
	}
	
	private	ExeContentCallback exeContentCallback;
	
	public void registerExeContentCallback(ExeContentCallback callback){
		this.exeContentCallback = callback;
	}
	
}
