package com.tellyouiam.alittlebitaboutspring.dto.treatment;

import com.opencsv.bean.CsvCustomBindByName;
import com.tellyouiam.alittlebitaboutspring.converter.CsvDateConverter;
import com.tellyouiam.alittlebitaboutspring.utils.converter.CsvStringConverter;

import java.util.Date;

public class ImportTreatmentHistoryDTO {
	@CsvCustomBindByName(column = "Horse Name", converter = CsvStringConverter.class)
	private String horseName;
	
	@CsvCustomBindByName(column = "Due Date", converter = CsvDateConverter.class)
	private Date dueDate;
	
	@CsvCustomBindByName(column = "Task Type", converter = CsvStringConverter.class)
	private String taskType;
	
	@CsvCustomBindByName(column = "Category", converter = CsvStringConverter.class)
	private String category;
	
	@CsvCustomBindByName(column = "Specific", converter = CsvStringConverter.class)
	private String specific;
	
	@CsvCustomBindByName(column = "Description", converter = CsvStringConverter.class)
	private String description;
	
	@CsvCustomBindByName(column = "Completed Date", converter = CsvDateConverter.class)
	private Date completedDate;
	
	@CsvCustomBindByName(column = "Comment", converter = CsvStringConverter.class)
	private String comment;
	
	public String getHorseName() {
		return horseName;
	}
	
	public void setHorseName(String horseName) {
		this.horseName = horseName;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	public String getTaskType() {
		return taskType;
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getSpecific() {
		return specific;
	}
	
	public void setSpecific(String specific) {
		this.specific = specific;
	}
	
	public Date getCompletedDate() {
		return completedDate;
	}
	
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
}
