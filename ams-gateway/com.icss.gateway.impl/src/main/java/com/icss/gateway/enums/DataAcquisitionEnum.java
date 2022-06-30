package com.icss.gateway.enums;

/**
 * <p>
 * 三目数据 枚举类
 * </p>
 *
 * @author cuigf
 * @since 2022-03-02
 */
public enum DataAcquisitionEnum {



	MEDICAL_INSURANCE_DRUG_CATALOG(
			1,
			"医保药品目录",
			"医保药品编码,医保药品名称,剂型,规格,用法,用量,支付单位,支付类别,备注,最高限价,三级医院最高价格,二级医院最高价格,一级医院最高价格,离休价格,门诊住院用药标识,门诊自付比例,住院自付比例,工伤自付比例,生育自付比例,生效日期,终止日期,国家医保贯标目录编码,医疗机构编码,医疗机构名称",
			"",
			new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}
	),

	MEDICAL_INSURANCE_DIAGNOSIS_PROJECT_CATALOG(
			2,
			"医保诊疗项目目录",
			"医保药品编码,医保药品名称,医保项目内涵,医保目录除外内容,支付类别,支付单位,规格,用法,备注,最高限价,三级医院最高价格,二级医院最高价格,一级医院最高价格,离休价格,门诊自付比例,住院自付比例,工伤自付比例,生育自付比例,生效日期,终止日期,国家医保贯标目录编码,医疗机构编码,医疗机构名称",
			"",
			new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22}
	),

	MEDICAL_INSURANCE_CONSUME_MATERIAL_CATALOG(
			3,
			"医保耗材目录",
			"医保项目编码,医保项目名称,规格,支付类别,支付单位,医保项目内涵,医保目录除外内容,备注,最高限价,三级医院最高价格,二级医院最高价格,一级医院最高价格,门诊自付比例,住院自付比例,工伤自付比例,生育自付比例,生效日期,终止日期,国家医保贯标目录编码,医疗机构编码,医疗机构名称",
			"",
			new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}
	),


	HOSPITAL_BASIC_INFO(
			10,
			"医院基本信息",
			"医疗机构编码,医疗机构名称,医保结算等级,行政区名称,性质,临床科室数量,实有人数,编制床数量,开放床数量,是否支持互联网医院,年度住院人数,年度门诊人数,统计年,医疗机构性质,医疗机构类别,年度医保基金支付总额",
			"",
			new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}
	);


	/**
	 * 三目数据类型：1医保药品目录2医保诊疗项目目录3医保耗材目录4病种目录5服务价格目录
	 */
	private int catalogType;


	/**
	 * 数据库表名
	 */
	private String tableName;


	/**
	 * 表所有字段
	 */
	private String tableColumns;

	/**
	 * 表所有字段的类型
	 */
	private String tableColumnTypes;

	/**
	 * excel每行row记录的cell下标和表字段的下标的映射关系
	 * 比如：int[] array = {2,0,1}
	 * 意思是：下标0的cell对应表字段的下标是2，下标1的cell对应表字段的下标是0，下标2的cell对应表字段的下标是1，
	 */
	private int[] cellAndColumnIndexMappings;


	/**
	 * 构造函数
	 * @param catalogType 三目数据类型：1医保药品目录2医保诊疗项目目录3医保耗材目录4病种目录5服务价格目录
	 * @param tableName 数据库表名
	 * @param tableColumns 表所有字段
	 * @param tableColumnTypes 表所有字段的类型
	 * @param cellAndColumnIndexMappings excel每行row记录的cell下标和表字段的下标的映射关系
	 */
	private DataAcquisitionEnum(int catalogType, String tableName, String tableColumns, String tableColumnTypes, int[] cellAndColumnIndexMappings) {
		this.setCatalogType(catalogType);
		this.setTableName(tableName);
		this.setTableColumns(tableColumns);
		this.setTableColumnTypes(tableColumnTypes);
		this.setCellAndColumnIndexMappings(cellAndColumnIndexMappings);
	}

	public int getCatalogType() {
		return catalogType;
	}

	public void setCatalogType(int catalogType) {
		this.catalogType = catalogType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableColumns() {
		return tableColumns;
	}

	public void setTableColumns(String tableColumns) {
		this.tableColumns = tableColumns;
	}

	public String getTableColumnTypes() {
		return tableColumnTypes;
	}

	public void setTableColumnTypes(String tableColumnTypes) {
		this.tableColumnTypes = tableColumnTypes;
	}

	public int[] getCellAndColumnIndexMappings() {
		return cellAndColumnIndexMappings;
	}

	public void setCellAndColumnIndexMappings(int[] cellAndColumnIndexMappings) {
		this.cellAndColumnIndexMappings = cellAndColumnIndexMappings;
	}

	/**
	 * 根据catalogType获取单个DataAcquisitionEnum
	 *
	 * @catalogType 1医保药品目录2医保诊疗项目目录3医保耗材目录4病种目录5服务价格目录
	 * @author cuigf 2022-03-03
	 */
	public static DataAcquisitionEnum getDataAcquisitionEnumByCatalogType(int catalogType){
		for(DataAcquisitionEnum item : DataAcquisitionEnum.values()){
			if(item.getCatalogType() == catalogType){
				return item;
			}
		}
		return null;
	}

	/**
	 * 根据单个DataAcquisitionEnum组装insert预编译sql语句
	 *
	 * @threeCatalogEnum DataAcquisitionEnum枚举
	 * @author cuigf 2022-03-03
	 */
	public static String composePreparedInsertSql(DataAcquisitionEnum threeCatalogEnum) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" INSERT INTO ");
		stringBuilder.append(threeCatalogEnum.getTableName());
		stringBuilder.append(" ( ");
		stringBuilder.append(threeCatalogEnum.getTableColumns());
		stringBuilder.append(" ) VALUES ( ");

		String[] tableColumns = threeCatalogEnum.getTableColumns().split(",");
		int index = 0;
		for (String column : tableColumns) {
			stringBuilder.append("?");
			if (index < tableColumns.length - 1) {
				stringBuilder.append(",");
			}
			index++;
		}
		stringBuilder.append(" )");
		return stringBuilder.toString();
	}


	public static void main(String[] args) {

	}



}
