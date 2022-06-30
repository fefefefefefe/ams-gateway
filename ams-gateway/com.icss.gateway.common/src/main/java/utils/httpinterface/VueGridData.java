package utils.httpinterface;

import com.github.pagehelper.Page;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * vue消息封装类
 * @param <T>
 */
public class VueGridData<T> {
	/**
	 * 消息头
	 */
	public class Head{
		/**
		 * 状态
		 */
		@Expose
		@SerializedName("status")
		public int status;
		/**
		 * 信息
		 */
		@Expose
		@SerializedName("message")
		public String message;
	
		public Head() {
			status = 20;
			message = "操作成功";
		}
	};

	/**
	 * 消息体
	 */
	public class Body{
		public Body() {
			this.pagination = new Pagination();
//			this.pageSQLserver=new utils.Page();
		}
		/**
		 * 结果列表
		 */
		@Expose
		@SerializedName("result")
		public List<T> result;

		/**
		 * 结果对象
		 */
		@Expose
		@SerializedName("resultObj")
		public T resultObj;
		/**
		 * 分页对象
		 */
		@Expose
		@SerializedName("pagination")
		public Pagination pagination;
//		@Expose
//		@SerializedName("pageSQLserver")
//		public utils.Page  pageSQLserver;
		/**
		 * 字段
		 */
		@Expose
		@SerializedName("columns")
		public List<String> columns;
		/**
		 * 字段
		 */
		@Expose
		@SerializedName("column")
		public String column;
		/**
		 * 数据量大小
		 */
		@Expose
		@SerializedName("resultCount")
		public Integer resultCount;
		/**
		 * 最小结果
		 */
		@Expose
		@SerializedName("mixResult")
		public Map<String, Object> mixResult;
	};
	/**
	 * 消息头
	 */
	@Expose
	@SerializedName("head")
	public  Head head;
	/**
	 * 消息体
	 */
	@Expose
	@SerializedName("body")
	public  Body body;

	/**
	 * VueGridData 构造函数
	 */
	public VueGridData() {
		this.head = new Head();
		this.body = new Body();
		
	}

	/**
	 * VueGridData 构造函数
	 *
	 * @result true成功 false 失败
	 * @type 类型
	 *
	 */
	public VueGridData(boolean result, String type) {
		this.head = new Head();
		this.body = new Body();
		String text = "成功！";
		int responseCode = 200;
		if(!result) {
			text = "失败！";
			responseCode = 50;
		}
		head.message = type + text;
		head.status = responseCode;
	}

	/**
	 * VueGridData 构造函数
	 *
	 * @page Page<T> 对象
	 *
	 */
	public VueGridData(Page<T> page) {
		this.head = new Head();
		this.body = new Body();
		this.body.pagination.dataCount = page.getTotal();
		this.body.pagination.pageCount = page.getPages();
		this.body.pagination.pageNum = page.getPageNum();
		this.body.pagination.pageSize = page.getPageSize();
		this.body.result = page.getResult();
	}

}


