package utils;

/**
 * 分页工具类
 * 
 * @author
 *
 */
public class Page<A> {
	public Page() {
		pageSize = 15;
		pageNum = 1;
	}
	private int start = 0;
	// 每页的数量
	private int pageSize = 15;// 一页显示的记录数
	// 尾页
	private int last = 0;
	// 当前页
	private int pageNum = 1;
	// 总记录数
	private Long dataCount;
	// 总页数
	private int pageCount;

	// 从第几条查起
	private int beginIndex;
	private String whereStr ="";
	private String orderBy="";

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


	public int getPageCount() {
		if(dataCount != null){
			int countT = (int) (dataCount / pageSize);
			if (dataCount % pageSize != 0)
				countT += 1;
			pageCount = countT;
		}
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getBeginIndex() {
		if (getPageSize() != 0) {
			beginIndex = (getPageNum() - 1) * getPageSize();
		}
		return beginIndex;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getLast() {
		return last;
	}

	public void setLast(int last) {
		this.last = last;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}


	 

	public Long getDataCount() {
		return dataCount;
	}

	public void setDataCount(Long dataCount) {
		this.dataCount = dataCount;
	}

	public void caculateLast(int total) {
		// 假设总数是50，是能够被5整除的，那么最后一页的开始就是45
		if (0 == total % pageSize)
			last = total - pageSize;
		// 假设总数是51，不能够被5整除的，那么最后一页的开始就是50
		else
			last = total - total % pageSize;
	}

	 

}
