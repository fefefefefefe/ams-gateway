package utils.httpinterface;


import utils.ConstantUtil;

public class Pagination {
	int pageNum = 1;
	int pageSize = ConstantUtil.DEFAULT_PAGE_SIZE;
	long dataCount = 0;
	int pageCount = 0;
	private String whereStr = "";
	private String orderBy = "";

	public Pagination() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getWhereStr() {
		return whereStr;
	}

	public void setWhereStr(String whereStr) {
		this.whereStr = whereStr;
	}

	public String getOrderBy() {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = " CURRENT_TIMESTAMP ";
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getDataCount() {
		return dataCount;
	}

	public void setDataCount(long dataCount) {
		this.dataCount = dataCount;
	}

	public int getPageCount() {
		int ys=(int) (this.dataCount%this.pageSize) ;
		this.pageCount =  (int) Math.floor(this.dataCount/this.pageSize) ;
		if(ys>0) {
			this.pageCount++;
		}
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

}