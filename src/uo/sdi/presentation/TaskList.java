package uo.sdi.presentation;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import uo.sdi.dto.Task;

public class TaskList extends ListDataModel<Task> implements SelectableDataModel<Task>{

	public TaskList(List<Task> listOfTasks) {
		this.setWrappedData(listOfTasks);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Task getRowData(String task) {
		for (Task t : (List<Task>) getWrappedData()){
			if (task.equals(t.getId().toString())){
				return t;
			}
		}
		return null;
	}

	@Override
	public Object getRowKey(Task task) {
		return task.getId();
	}

}
