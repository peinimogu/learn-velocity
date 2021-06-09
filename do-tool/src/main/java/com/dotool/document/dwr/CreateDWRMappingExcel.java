package com.dotool.document.dwr;

import com.dotool.document.DocumentMain;
import com.dotool.document.tool.Field;
import com.dotool.document.tool.ReadFQExcel;
import com.dotool.document.tool.Table;
import com.dotool.util.PublicTools;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateDWRMappingExcel {



	public static void main(String[] args) throws Exception {
			List<Table> tableList = ReadFQExcel.read(DocumentMain.fileName);
			System.out.println(tableList.size());

		String[] col = new String[] {"主题域","实体名称","实体属性字段","实体属性","字段类型","字段长度","是否主键","分布键","是否时间戳","行/列存储","属性所属域组","系统简称","Schema标识","表名","表中文名","字段名","字段中文名","字段类型","字段长度","所属值域","映射代码","映射备注"};

//		XSSFWorkbook wbnew = XSSFWorkbookFactory.createWorkbook(new FileInputStream("d:\\test.xlsx"));
		XSSFWorkbook wbnew = XSSFWorkbookFactory.createWorkbook();

		CellStyle style = wbnew.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);// 左右居中

		//边框下
		style.setBorderBottom(BorderStyle.THIN);
		//边框左
		style.setBorderLeft(BorderStyle.THIN);
		//边框右
		style.setBorderRight(BorderStyle.THIN);
		//边框上
		style.setBorderTop(BorderStyle.THIN);

		CellStyle titleStyle = wbnew.createCellStyle();
		titleStyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中

		//边框下
		titleStyle.setBorderBottom(BorderStyle.THIN);
		//边框左
		titleStyle.setBorderLeft(BorderStyle.THIN);
		//边框右
		titleStyle.setBorderRight(BorderStyle.THIN);
		//边框上
		titleStyle.setBorderTop(BorderStyle.THIN);

		titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

//		titleStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
		titleStyle.setFillForegroundColor((short) 27);

		try {

			String needTableString = DocumentMain.needTableString;
			String[] needTableArray = needTableString.split(",");
			Map<String, String> needMap = new HashMap<>();
			for (String s : needTableArray) {
				if(PublicTools.isNotNull(s)) {
					needMap.put(s, s);
				}
			}

			for (Table table : tableList) {
				String name = table.getName();
				if (needMap.isEmpty() == false && needMap.containsKey(name) == false) {
					continue;
				}

				String physicsTableName = DocumentMain.dbPre + "_" + name + "_f";
				String dwiPhysicsTableName = DocumentMain.dwiDBPre + "_" + name + "";


				Sheet  st  = wbnew.createSheet(table.getDes());

				int sc = 1;
				Row row = st.createRow(0);

				//row.createCell(0).setCellValue("");
				row.createCell(0).setCellFormula("HYPERLINK(\"" + "#数据资源!A1" + "\",\"" + "返回" + "\")");



				row = st.createRow(1);

				int a = 0;
				for(String c : col) {
					row.createCell(a++).setCellValue(c);

					row.getCell(0).setCellStyle(style);
					for (int j = 0; j < col.length; j++) {
						if (row.getCell(j) == null) {
							row.createCell(j).setCellValue("");
						}

						row.getCell(j).setCellStyle(titleStyle);

					}
				}



				Row idRow = st.createRow(++sc);
				idRow.createCell(0).setCellValue(DocumentMain.domainName);
				idRow.createCell(1).setCellValue(table.getDes());

				idRow.createCell(2).setCellValue("id");
				idRow.createCell(3).setCellValue("系统id");

				idRow.createCell(4).setCellValue("serial");
				idRow.createCell(5).setCellValue("50");


				idRow.createCell(6).setCellValue("N");
				idRow.createCell(7).setCellValue("N");
				idRow.createCell(9).setCellValue("row");

				for (int j = 0; j < col.length; j++) {
					if (idRow.getCell(j) == null) {
						idRow.createCell(j).setCellValue("");
					}
					{
						idRow.getCell(j).setCellStyle(style);
					}

				}




				int index = -1;
			    for (Field field : table.getFieldList()) {

					if(field.getInDBThemeFlag().equalsIgnoreCase("n")) {
						continue;
					}

			    	index++;
					String cnName = field.getCnName();
					String enName = field.getName();

					String type = field.getType();
					String length = field.getLength();
					String decimal = field.getDecimal();
					if (decimal.equalsIgnoreCase("0")) {
						decimal = "";
					}
					String des = field.getDes();


					String pk = "";
					String isNull = "Y";
					String isPk = "N";
					String isDistribution = "N";

					if(index == 0) {
						pk = "PRI";

						isNull = "N";
						isPk = "Y";
						isDistribution = "Y";
					}

					Row rr = st.createRow(++sc);
					rr.createCell(0).setCellValue(DocumentMain.domainName);
					rr.createCell(1).setCellValue(table.getDes());

					String physicsName = field.getPhysicsName().toLowerCase();
					if (physicsName.equals("id")) {
						physicsName = "record_id";
					}

					rr.createCell(2).setCellValue(physicsName);
					rr.createCell(3).setCellValue(cnName);

					String newType = changeType(type);
					if (newType.equals("timestamp")) {
						length = "6";
					} else if (newType.toLowerCase().equals("character varying")) {
						try {
							length = (Integer.parseInt(length) * 3) + "";
						} catch (Exception e) {
							length = "255";
						}
					}

					rr.createCell(4).setCellValue(newType);
					rr.createCell(5).setCellValue(length);


					rr.createCell(6).setCellValue(isPk);
					rr.createCell(7).setCellValue(isDistribution);
					rr.createCell(9).setCellValue("row");

					rr.createCell(11).setCellValue(DocumentMain.sourceSystemName);

					rr.createCell(12).setCellValue(DocumentMain.dwiDBPre);
					rr.createCell(13).setCellValue(dwiPhysicsTableName);
					rr.createCell(14).setCellValue(table.getDes());

					rr.createCell(15).setCellValue(field.getName());
					rr.createCell(16).setCellValue(cnName);

					rr.createCell(17).setCellValue(newType);
					rr.createCell(18).setCellValue(length);



					//rr.getCell(0).getArrayFormulaRange().
					for (int j = 0; j < col.length; j++) {
						if (rr.getCell(j) == null) {
							rr.createCell(j).setCellValue("");
						}
						{
							rr.getCell(j).setCellStyle(style);
						}

					}


				}

				List<String> auditDataList = new ArrayList<>();

				auditDataList.add("数据创建者,dw_creation_by,character varying,100");
				auditDataList.add("数据创建时间,dw_creation_date,timestamp,6");
				auditDataList.add("数据最后更新者,dw_last_update_by,character varying,100");
				auditDataList.add("数据最后更新时间,dw_last_update_date,timestamp,6");
				auditDataList.add("数据批次号,dw_batch_number,bigint,");

				for (String data : auditDataList) {
					String[] strArray = data.split(",");

					Row rr = st.createRow(++sc);
					rr.createCell(0).setCellValue(DocumentMain.domainName);
					rr.createCell(1).setCellValue(table.getDes());

					rr.createCell(2).setCellValue(strArray[1].toLowerCase());
					rr.createCell(3).setCellValue(strArray[0]);

					rr.createCell(4).setCellValue(strArray[2]);
					String s = "";

					if (strArray.length == 4) {
						s = strArray[3];
					}
					rr.createCell(5).setCellValue(s);

					rr.createCell(6).setCellValue("N");
					rr.createCell(7).setCellValue("N");
					rr.createCell(9).setCellValue("row");

					for (int j = 0; j < col.length; j++) {
						if (rr.getCell(j) == null) {
							rr.createCell(j).setCellValue("");
						}
						{
							rr.getCell(j).setCellStyle(style);
						}

					}

				}


				for (int j = 0; j < col.length; j++) {
					st.setColumnWidth(j, 5000);

				}
				
			}


		}


		catch (Exception e) {
			e.printStackTrace();
		}
		wbnew.write(new FileOutputStream("d:\\test_fq_physics_mapping.xlsx"));
		wbnew.close();

	}
	
	public static String getString(Cell cell) {
		if(cell != null) {
			String str=cell.toString().replace(String.valueOf((char)160)," ").replaceAll("^\\s+", "").replaceAll("\\s+$", "");
			str=str.trim();
			return str;
		}
		return "";
	}
	
	public static String getString(String cell) {
		if(cell != null) {
			String str=cell.toString().replace(String.valueOf((char)160)," ").replaceAll("^\\s+", "").replaceAll("\\s+$", "");
			str=str.trim();
			return str;
		}
		return "";
	}
	
	public static String changeType(String type , String length) {
		type = type.toLowerCase();
		switch(type) {
			case "varchar2":
				return "character varying("+length+")";
		   case "varchar":
			   return "character varying("+length+")";
		   case "date":
			   return "timestamp(6)";
		   case "datetime":
			   return "timestamp(6)";
		   case "smallint":
			   return "smallint";
		   case "int":
			   return "integer";
		   case "char":
			   return "char("+length+")";
		   case "number":
			   return "number";
		   case "double":
			   return "number"+ ((length !=null&& !"".equals(length) && !"0".equals(length))?"("+length+")":"") ;
		   case "decimal":
			   return "number"+ ((length !=null&& !"".equals(length) && !"0".equals(length))?"("+length+")":"") ;
		   case "float":
			   return "number"+ ((length !=null&& !"".equals(length) && !"0".equals(length))?"("+length+")":"") ;
		   case "timestamp":
			   return "timestamp(6)";
		   case "bigint":
			   return "bigint";
		   case "tinyint":
			   return "tinyint";
		   case "mediumtext":
			   return "text";
		   case "longtext":
			   return "text";
		   case "text":
			   return "text";
		}
		return "character varying(100)";
	}

	public static String changeType(String cell) {
		cell = cell.replace("（", "(").replace("）", ")");
		cell = getString(cell);
		if (cell.toUpperCase().startsWith("mediumtext".toUpperCase())) {
			return "text";
		}
		else if (cell.toUpperCase().startsWith("smallint".toUpperCase())) {
			return "int";
		}
		else if (cell.toUpperCase().startsWith("VARCHAR2")) {
			return cell.toUpperCase().replace("VARCHAR2", " character varying");
		}
		else if (cell.toUpperCase().startsWith("VARCHAR")) {
			return cell.toUpperCase().replace("VARCHAR", " character varying");
		} else if (cell.toUpperCase().startsWith("TIMESTAMP")) {
			return cell.toLowerCase() ;
		} else if (cell.toUpperCase().startsWith("DATETIME") || cell.toUpperCase().startsWith("DATE")) {
			return "timestamp" ;
		} else if (cell.toUpperCase().startsWith("DOUBLE")) {
			return cell.toUpperCase().replace("DOUBLE", "number");
		} else if (cell.toUpperCase().startsWith("INT")) {
			return "integer";
		} else if (cell.toUpperCase().startsWith("BIGINT")) {
			return "bigint";
		} else if (cell.toUpperCase().startsWith("TINYINT")) {
			return "tinyint";
		} else if (cell.toUpperCase().startsWith("FLOAT")) {
			return cell.toUpperCase().replace("FLOAT", "number");
		}

		return cell.toLowerCase();
	}


	
	public static String getTypeString1(String type,String length) {
		return type + ((length !=null&& !"".equals(length) && !"0".equals(length))?"("+length+")":"") ;
	}



}
