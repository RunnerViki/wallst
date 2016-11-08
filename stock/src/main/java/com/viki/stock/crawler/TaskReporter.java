package com.viki.stock.crawler;

import com.viki.stock.bean.TaskWatermark;
import com.viki.stock.config.MailUtil;
import com.viki.stock.dao.TaskWatermarkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskReporter {

	@Autowired
	@Qualifier("TaskWatermarkDao")
	private TaskWatermarkDao taskWatermarkDao;
	
	SimpleDateFormat reportSDF = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run(){
		List<TaskWatermark> taskReports = taskWatermarkDao.taskReport(null);
		StringBuilder sb = new StringBuilder();
		for(TaskWatermark taskWatermark : taskReports){
			sb.append(taskWatermark.getTaskName()+"\t" + reportSDF.format(taskWatermark.getLastExecuted()) 
					+ "\t" + reportSDF.format(taskWatermark.getNextExecute()) + "\n");
		}
		new MailUtil().sendMailOneStep(String.format("%s任务报告", reportSDF.format(new Date())), sb.toString());
	}
}
