package utils;

import java.util.*;

/**
 * 该类用来管理并行线程的并发数。
 * 该并发数必须限制在一个固定的上限值，即时刻保持着指定的线程活动数量。
 * 背景：
 * 由于系统中能开启的数据库连接池数量是配置好的，而需要并行请求数据库的线程是不固定的，
 * 这样以来就必须限制启动的线程数，以使开启的线程都能得到数据库连接。
 * @author gcy
 * @DATE 2022/03/16
 *
 */
public class ThreadPoolUtils
{
	private List<Thread> threads=null;
	private int allCnt=0;
	private int activeLimit=0;

	/**
	 *
	 * @param threads：线程list，存放所有要操作的线程对象。
	 * @param activeLimit： 最大活动线程数，实时监控并开启新的线程以满足满载运行。
	 * @author gcy
	 * @DATE 2022/03/16
	 */
	public ThreadPoolUtils(List<Thread> threads, int activeLimit)
	{
		if(threads!=null && threads.size()!=0 && activeLimit>0){
			this.threads=threads;
			this.allCnt=threads.size();
			this.activeLimit=activeLimit;
		}
	}

	/**
	 * 开启该类的服务功能
	 * @author gcy
	 * @DATE 2022/03/16
	 */
	public void doService()
	{
		if(threads==null || threads.size()==0){
			return;
		}
		this.doStart(activeLimit);
		boolean flag=true;
		while(flag)
		{
			try
			{	//每隔10秒监测一下是否需要启动新的线程
				Thread.sleep(1*2*1000);
				int cnt=this.getDiffValue();
				this.doStart(cnt);
				flag=this.doWhile();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param newCnt
	 * @author gcy
	 * @DATE 2022/03/16
	 */
	private void doStart(int newCnt)
	{
		if(newCnt==0)
		{
			return;
		}
		int cnt=0;
		//初始化启动指定的线程数量
		int len=threads.size();
		for(int i=0;i<len;i++)
		{
			if(this.getThreadState(threads.get(i)).equals("NEW"))
			{
				Thread t=threads.get(i);
				t.start();
				cnt=cnt+1;
				if(cnt>=newCnt)
				{
					break;
				}
			}
		}
	}

	/**
	 * 得到可以继续添加多少个线程去运行。
	 * @return int
	 * @author gcy
	 * @DATE 2022/03/16
	 */
	private int getDiffValue()
	{
		int cnt=0;
		int len=threads.size();
		for(int i=0;i<len;i++)
		{
			if(!this.getThreadState(threads.get(i)).equals("NEW") && !this.getThreadState(threads.get(i)).equals("TERMINATED"))
			{//得到正在运行中的线程的数量
				cnt=cnt+1;
			}
		}
		//用上限值-正在运行线程数，得到可以继续添加运行的线程数量。
		return this.activeLimit-cnt;
	}
	/**
	 * 验证何时关闭构造函数中的while死循环。
	 * @return  true：继续  false：关闭死循环

	 */
	private boolean doWhile()
	{
		int cnt=0;
		for(int i=0;i<allCnt;i++)
		{
			if(this.getThreadState(threads.get(i)).equals("TERMINATED"))
			{
				cnt=cnt+1;
			}
		}
		if(cnt==allCnt)
		{//如果所有线程全部执行完毕，且运行完毕的线程数达到了要求运行的线程数，那么
			return false;
		}
		else
		{
			return true;
		}
	}
	/**
	 * 得到线程状态的字符串表示
	 * @param t
	 * @return
	 * @author gcy
	 * @DATE 2022/03/16
	 */
	private String getThreadState(Thread t)
	{
		return t.getState().name();
	}

	public static void main(String[] args){
		List<Thread> threads=null;
		threads=new ArrayList<Thread>();
		for(int i=0;i<2;i++){
			Thread t=new Thread(new Runnable(){
				public void run() {
					try {
						Thread.sleep(1*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName()+"运行中...");
				}
			});
			threads.add(t);
		}
		ThreadPoolUtils pool=new ThreadPoolUtils(threads,10000);
		pool.doService();
		System.out.println("pool结束");
	}

}
